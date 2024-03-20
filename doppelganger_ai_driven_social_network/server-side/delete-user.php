<?php
/**
 * User: emilxp
 * Date: 5/18/2018
 * Time: 2:24 AM
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

$user_id = $auth->id();

// Get user info
$query = $db->prepare("
    SELECT *
    FROM user_info
    WHERE user_id=:user_id
");

$query->bindParam(":user_id",$user_id);

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

$result = $query->fetchObject();

if($result) {
    // Delete user info
    $query = $db->prepare("
        DELETE
        FROM user_info
        WHERE user_id=:user_id
    ");

    $query->bindParam(":user_id",$user_id);

    $hasSucceeded = $query->execute();
    if(!$hasSucceeded) {
        handleLoginException(500, "An internal server error occurred.");
    }

    // Check if the profile and matchmaking pictures have been set for this user
    if(isset($result->profile_pic_name)) {
        // Delete profile picture
        $profilePicPath = __DIR__."/picture/{$result->profile_pic_name}";
        $hasSucceeded = unlink($profilePicPath);
        if(!$hasSucceeded) {
            handleLoginException(500, "An internal server error occurred.");
        }
    }

    if(isset($result->match_pic_name)) {
        // Delete matchmaking picture locally
        $profilePicPath = __DIR__."/picture/{$result->match_pic_name}";
        $hasSucceeded = unlink($profilePicPath);
        if(!$hasSucceeded) {
            handleLoginException(500, "An internal server error occurred.");
        }

        // Delete the matchmaking face from Amazon Rekognition
        $res = null;
        try {
            $res = $rekognition->deleteFaces([
                'CollectionId' => 'main_collection',
                'FaceIds' => [$result->face_id]
            ]);
        } catch(Aws\Rekognition\Exception\RekognitionException $e) {
            handleLoginException(500, "An internal server error occurred");
        }
    }
}

// Delete the messages sent by the current user
$query = $db->prepare("
    DELETE
    FROM user_messages
    WHERE sender_id=:user_id
");

$query->bindParam(":user_id",$user_id);

$hasSucceeded = $query->execute();
if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred.");
}

// Delete the current user
try {
    $auth->admin()->deleteUserById($auth->id());
} catch (\Delight\Auth\AuthError $e) {
    handleLoginException(500, "An unknown server error occurred.");
} catch (\Delight\Auth\UnknownIdException $e) {
    handleLoginException(500, "You're not logged-in.");
}

// Logout the current user
try {
    $auth->logOutEverywhere();
} catch (\Delight\Auth\AuthError $e) {
    echo get_class($e);
    handleLoginException(403, "An authentication error occurred.");
} catch (\Delight\Auth\NotLoggedInException $e) {
    handleLoginException(401, "Please login");
}

handleLoginException(200, "");

?>