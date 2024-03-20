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

// Check if user email input is set
if(!isset($_POST["email"])) {
    handleLoginException(400, "'email' parameter not provided");
}

// Retrieve user email input
$user_email = $_POST["email"]; // TODO: check za dali e definenat!!!

if(empty($user_email) == true) {
    $user_email = $auth->getEmail();
}

// Check if user_info has been set for this user
// NOTE: We should probably do an INNER JOIN, but a RIGHT JOIN works too
$query = $db->prepare("
    SELECT user_info.*, users.id, users.email
    FROM users
    RIGHT JOIN user_info ON user_info.user_id = users.id
    WHERE users.email=:user_email
");

$query->bindParam(":user_email",$user_email);

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