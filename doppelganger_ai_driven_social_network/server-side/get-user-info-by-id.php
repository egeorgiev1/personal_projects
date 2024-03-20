<?php
/**
 * User: emilxp
 * Date: 5/16/2018
 * Time: 12:40 AM
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

// Check if user id input is set
if(!isset($_POST["id"])) {
    handleLoginException(400, "'id' parameter not provided");
}

// Retrieve user email input
$user_id = (int)$_POST["id"];

if(!is_numeric($user_id)) {
    handleLoginException(400, "'id' parameter must be a number");
}

// Check if user_info has been set for this user
// NOTE: We should probably do an INNER JOIN, but a RIGHT JOIN works too
$query = $db->prepare("
    SELECT user_info.*, users.id, users.email
    FROM users
    RIGHT JOIN user_info ON user_info.user_id = users.id
    WHERE users.id=:user_id
");

$query->bindParam(":user_id",$user_id);

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

$result = $query->fetchObject();

if($result == false) {
    handleLoginException(404, "User info has not been set for this user.");
}

// Output the result
http_response_code(200);
echo json_encode($result);

?>