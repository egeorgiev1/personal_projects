<?php
/**
 * User: emilxp
 * Date: 5/17/2018
 * Time: 4:56 PM
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

// Check if the last message timestamp and number_of_messages parameters are provided
if(!isset($_POST["timestamp"])) {
    handleLoginException(400, "'timestamp' parameter not provided");
}

if(!isset($_POST["number_of_messages"])) {
    handleLoginException(400, "'number_of_messages' parameter not provided");
}

// Retrieve input parameters
$timestamp = $_POST["timestamp"];
$numberOfMessages = $_POST["number_of_messages"];

// Check if number_of_messages is a numeric string
if(!is_numeric($numberOfMessages)) {
    handleLoginException(400, "'numberOfMessages' query parameter must be a number");
}

// Cast number_of_messages to int
$numberOfMessages = (int)$numberOfMessages;

// Check if the required inputs meet the constraints(not 100% exhaustive regex, but does the job)
if(!preg_match("/\d{4}-[0-1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9]/", $timestamp)) {
    handleLoginException(400, "Invalid timestamp");
}

if($numberOfMessages < 0) {
    handleLoginException(400, "'numberOfMessages' can't be negative");
}

if($numberOfMessages > 20) {
    handleLoginException(400, "Can't return more than 20 messages");
}

// Get the messages from the database
$query = $db->prepare("
    SELECT * FROM user_messages
    WHERE (sender_id=:user_id OR receiver_id=:user_id) AND receive_date > STR_TO_DATE(:timestamp, '%Y-%m-%d %H:%i:%S')
    ORDER BY receive_date
    LIMIT :number_of_messages
");

$currentUserId = $auth->id();
$isValid = $query->bindParam(":user_id",$currentUserId, PDO::PARAM_INT);

if(!$isValid) {
    handleLoginException(500, "Internal server error");
}

$isValid = $query->bindParam(":timestamp",$timestamp, PDO::PARAM_STR);

if(!$isValid) {
    handleLoginException(500, "Internal server error");
}

$isValid = $query->bindParam(":number_of_messages",$numberOfMessages, PDO::PARAM_INT);

if(!$isValid) {
    handleLoginException(500, "Internal server error");
}

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

$resultsArray = [];
while($result = $query->fetchObject()) {
    array_push($resultsArray, $result);
}

http_response_code(200);
echo json_encode($resultsArray);

?>