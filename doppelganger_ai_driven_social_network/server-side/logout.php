<?php
/**
 * User: emilxp
 * Date: 5/13/2018
 * Time: 9:00 PM
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

try {
    $auth->logOut();
} catch (\Delight\Auth\AuthError $e) {
    echo get_class($e);
    handleLoginException(403, "An authentication error occurred.");
}

?>