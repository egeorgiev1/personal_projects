<?php
/**
 * User: emilxp
 * Date: 5/17/2018
 * Time: 4:55 PM
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

// Get user info
$query = $db->prepare("
    SELECT *
    FROM user_info
    WHERE user_id=:user_id
");

$user_id = $auth->id();
$query->bindParam(":user_id",$user_id);

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

$result = $query->fetchObject();

if($result == false) {
    handleLoginException(401, "User info has not been set for this user.");
}

// Check if the profile and matchmaking pictures have been set for this user
if(!isset($result->profile_pic_name) || !isset($result->match_pic_name)) {
    handleLoginException(401, "Profile and/or matchmaking picture not set.");
}

// Check if the user_id and message parameters are provided
if(!isset($_POST["user_id"])) {
    handleLoginException(400, "'user_id' parameter not provided");
}

if(!isset($_POST["message"])) {
    handleLoginException(400, "'message' parameter not provided");
}

// Retrieve input parameters
$partnerId = $_POST["user_id"];
$message = $_POST["message"];

// Check if the conversation partner is a fully setup user
$query = $db->prepare("
    SELECT *
    FROM user_info
    WHERE user_id=:user_id
");

$query->bindParam(":user_id",$partnerId);

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

$result = $query->fetchObject();

if($result == false) {
    handleLoginException(401, "Invalid partner.");
}

// Check if the profile and matchmaking pictures have been set for this user
if(!isset($result->profile_pic_name) || !isset($result->match_pic_name)) {
    handleLoginException(401, "Invalid partner.");
}

// Add message to the database
$query = $db->prepare("
    INSERT INTO user_messages
    (sender_id, receiver_id, message)
    VALUES (:sender_id, :receiver_id, :message)
");

$currentUserId = $auth->id();
$isValid = $query->bindParam(":sender_id",$currentUserId, PDO::PARAM_INT);

if(!$isValid) {
    handleLoginException(500, "Internal server error");
}

$isValid = $query->bindParam(":receiver_id",$partnerId, PDO::PARAM_INT);

if(!$isValid) {
    handleLoginException(500, "Internal server error");
}

$isValid = $query->bindParam(":message",$message, PDO::PARAM_STR, 4096);

if(!$isValid || strlen($message) == 0) {
    handleLoginException(400, "Invalid message");
}

$hasSucceeded = $query->execute();

if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred");
}

// Return HTTP 200
handleLoginException(200, "");

?>