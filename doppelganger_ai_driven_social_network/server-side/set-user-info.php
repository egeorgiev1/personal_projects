<?php
/**
 * User: emilxp
 * Date: 5/14/2018
 * Time: 1:06 AM
 */

require __DIR__ . '/vendor/autoload.php';
$db = new \PDO('mysql:dbname=accounts;host=localhost;charset=utf8mb4', 'root', '');
$auth = new \Delight\Auth\Auth($db, null, null, false);

function handleLoginException($responseCode, $message) {
    http_response_code($responseCode);

    // Create response
    $response = new stdClass();
    $response->rationale = $message;

    // Echo response in json
    echo json_encode($response);
    exit();
}

// If not logged in, respond with error code 401
if(!$auth->isLoggedIn()) {
    handleLoginException(401, "Please login");
}

// Check if input is set
if(!isset($_POST["name"])) {
    handleLoginException(400, "'name' parameter not provided");
}

if(!isset($_POST["occupation"])) {
    handleLoginException(400, "'occupation' parameter not provided");
}

if(!isset($_POST["birthday"])) {
    handleLoginException(400, "'birthday' parameter not provided");
}

if(!isset($_POST["interests"])) {
    handleLoginException(400, "'interests' parameter not provided");
}

if(!isset($_POST["introduction"])) {
    handleLoginException(400, "'introduction' parameter not provided");
}

// Retrieve inputs
$name = $_POST["name"];
$occupation = $_POST["occupation"];
$birthday = $_POST["birthday"];
$interests = $_POST["interests"];
$introduction = $_POST["introduction"];

// Protects from SQL injection
$query = $db->prepare("
    INSERT INTO user_info
    (user_id, `name`, occupation, birthday, interests, introduction)
    VALUES (:user_id, :name, :occupation, STR_TO_DATE(:birthday, '%Y-%m-%d'), :interests, :introduction)
    ON DUPLICATE KEY UPDATE
    user_id=:user_id, `name`=:name, occupation=:occupation, birthday=STR_TO_DATE(:birthday, '%Y-%m-%d'), interests=:interests, introduction=:introduction
");

$user_id = $auth->id(); // Because it can't be passed by reference it needs to be put in a variable

$isValid = $query->bindParam(":user_id",$user_id, PDO::PARAM_INT);

if(!$isValid) {
    handleLoginException(500, "Internal server error");
}

$isValid = $query->bindParam(":name",$name, PDO::PARAM_STR, 128);

if(!$isValid || strlen($name) == 0) {
    handleLoginException(400, "Invalid name");
}

$isValid = $query->bindParam(":occupation",$occupation, PDO::PARAM_STR, 64);

if(!$isValid || strlen($occupation) == 0) {
    handleLoginException(400, "Invalid occupation");
}

$isValid = $query->bindParam(":birthday",$birthday, PDO::PARAM_STR);

// Not the best date regex, but it does the job. In the worst case the query is going to fail.
if(!$isValid || !preg_match("/\d{4}-[0-1][0-9]-[0-3][0-9]/", $birthday)) {
    handleLoginException(400, "Invalid birthday");
}

$isValid = $query->bindParam(":interests",$interests);

if(!$isValid || strlen($interests) == 0) {
    handleLoginException(400, "Invalid interests");
}

$isValid = $query->bindParam(":introduction",$introduction, 256);

if(!$isValid || strlen($introduction) == 0) {
    handleLoginException(400, "Invalid introduction");
}

$hasSucceeded = $query->execute();

if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred");
}

handleLoginException(200, "");

?>