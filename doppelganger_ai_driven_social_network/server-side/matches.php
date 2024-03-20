<?php
/**
 * User: emilxp
 * Date: 5/16/2018
 * Time: 1:14 AM
 */

require __DIR__ . '/vendor/autoload.php';
$db = new \PDO('mysql:dbname=accounts;host=localhost;charset=utf8mb4', 'root', '');
$auth = new \Delight\Auth\Auth($db, null, null, false);
$rekognition = new Aws\Rekognition\RekognitionClient([
    'version' => 'latest',
    'region' => 'us-east-1',
    'credentials' => [
        'key' => 'AKIAIVQHCVCV56ZSNRCQ',
        'secret' => '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'
    ]
]);

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

// Check if the required inputs are provided
if(!isset($_GET["from"])) {
    handleLoginException(400, "'from' query parameter not provided");
}

if(!isset($_GET["to"])) {
    handleLoginException(400, "'to' query parameter not provided");
}

// Retrieve the inputs(as strings)
$from = (int)$_GET["from"];
$to = (int)$_GET["to"];

// Check if the required inputs are numeric strings
if(!is_numeric($from)) {
    handleLoginException(400, "'from' query parameter must be a number");
}

if(!is_numeric($to)) {
    handleLoginException(400, "'to' query parameter must be a number");
}

// Cast the inputs to integers
$from = (int)$from;
$to = (int)$to;

// Check if the required inputs meet the constraints
if($to > 4096) {
    handleLoginException(400, "'to' can't be bigger than 4096");
}

if($from > $to) {
    handleLoginException(400, "'from' can't be bigger than 'to'");
}

if($from < 0) {
    handleLoginException(400, "'from' can't be negative");
}

if($to-$from+1 > 20) {
    handleLoginException(400, "Can't return more than 20 elements");
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

// Query Amazon Rekognition for the matching faces
$res = null;
try {
    $res = $rekognition->searchFaces([
        "CollectionId" => "main_collection",
        "FaceId" => $result->face_id,
        "FaceMatchThreshold" => 70, // 70%
        "MaxFaces" => $to
    ]);
} catch(Aws\Rekognition\Exception\RekognitionException $e) {
    handleLoginException(500, "An internal server error occurred");
}

if(count($res['FaceMatches']) < $from) {
    echo "[]";
    http_response_code(200);
    exit();
}

// Slice the array of results to fit the "from" and "to" constraints
$slicedResults = array_slice($res['FaceMatches'], $from-1, $to-$from+1); // $from can be maximum 4096(cuz of the Amazon Rekognition limit and also because you won't have that many doppelgangers, the matches are gonna get progressively less similar to you)

// Transform the array to be an array of face_ids
$faceIds = array_map(function($match) { return "'".$match["Face"]["FaceId"]."'"; }, $slicedResults);

// Get the user info of the users with the retrieved face ids
$combinedFaceIds = implode(', ', $faceIds);

$query = $db->prepare("SELECT user_info.*, users.id, users.email
    FROM users
    RIGHT JOIN user_info ON user_info.user_id = users.id
    WHERE face_id IN (".$combinedFaceIds.")");

//$query->bindParam(":combinedFaceIds",$combinedFaceIds); // can't do it like that, cuz it's gonna get escaped

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

// Output the results in JSON format
$resultsArray = [];
while($result = $query->fetchObject()) {
    //unset($result->user_id);
    array_push($resultsArray, $result);
}
echo json_encode($resultsArray);
http_response_code(200);


?>