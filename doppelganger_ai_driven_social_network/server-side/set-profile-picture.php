<?php
/**
 * User: emilxp
 * Date: 5/14/2018
 * Time: 3:05 AM
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

// Check if the user is logged in
if(!$auth->isLoggedIn()) {
    handleLoginException(401, "Please login");
}

// Get basic file upload info
$fileName = $_FILES["picture"]["name"];
$filePath = $_FILES["picture"]["tmp_name"];

// Check if null
if(!isset($fileName) || !isset($filePath)) {
    handleLoginException(500, "File was not uploaded properly.");
}

// Check if file is uploaded
if(!file_exists($filePath) || !is_uploaded_file($filePath)) {
    handleLoginException(500, "File was not uploaded properly.");
}

// Check if the uploaded file is an image
$isImage = getimagesize($filePath);
if(!$isImage) {
    handleLoginException(415, "Uploaded file is not an image.");
}

// Check if MIME type is supported: jpg, png, gif ot webp
$mimeType = $isImage["mime"];
if(
    !(
        strcmp($mimeType, "image/jpeg") == 0 ||
        strcmp($mimeType, "image/png") == 0  ||
        strcmp($mimeType, "image/gif") == 0  ||
        strcmp($mimeType, "image/webp") == 0
    )
) {
    handleLoginException(415, "Image can only be jpeg, png, gif and webp.");
}

// Check if the image is 400x400
if($isImage[0] != 400 || $isImage[1] != 400) {
    handleLoginException(400, "Image can only be 400x400.");
}

// Check if the user has setup his basic user information
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

$result = $query->fetchAll();

if(count($result) < 1) {
    handleLoginException(401, "Can't set a profile picture before setting basic user information.");
}

// Get the old image filename
$oldImageFilename = $result[0]["profile_pic_name"];

// Generate a random name for this file
$randomName = "";
try {
    $randomName = bin2hex(openssl_random_pseudo_bytes(64));
} catch (Exception $e) {
    handleLoginException(500, "A server error occurred. Please try again later.");
}
// Add the proper extension corresponding to the MIME type
//var_dump(explode("/", $mimeType));

$extension = explode("/", $mimeType)[1];
$randomName = "$randomName.$extension";

//var_dump($randomName);

// Check for name collision(very unlikely)
$newPath = __DIR__."/picture/{$randomName}";
if(file_exists($newPath)) {
    handleLoginException(500, "A server error occurred. Please try again later.");
}

// Move the file to the correct directory
$isSuccessful = rename($filePath , $newPath);
if(!$isSuccessful) {
    handleLoginException(500, "A server error occurred. Please try again later.");
}

// Change the profile picture in the database
$query = $db->prepare("
    UPDATE user_info SET profile_pic_name=:profile_pic_name WHERE user_id=:user_id
");

$user_id = $auth->id();
$query->bindParam(":user_id",$user_id);
$query->bindParam(":profile_pic_name",$randomName);

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

// Delete the old image if there was a prior image set as the profile picture
///echo "$oldImageFilename";
if(!empty($oldImageFilename)) {
    $oldFilePath = __DIR__."/picture/{$oldImageFilename}";
    $hasSucceeded = unlink($oldFilePath);
    if(!$hasSucceeded) {
        handleLoginException(500, "An internal server error occurred.");
    }
}

handleLoginException(200, "");

?>