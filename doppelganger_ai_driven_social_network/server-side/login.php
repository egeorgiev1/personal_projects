<?php
/**
 * User: emilxp
 * Date: 5/13/2018
 * Time: 8:03 PM
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

// Check if the email and password parameters are provided
if(!isset($_POST["email"])) {
    handleLoginException(400, "'email' parameter not provided");
}

if(!isset($_POST["password"])) {
    handleLoginException(400, "'password' parameter not provided");
}

$_POST["email"] = strtolower($_POST["email"]);

try {
    $auth->login($_POST["email"], $_POST["password"], (int) (60 * 60 * 24 * 365.25));
} catch (\Delight\Auth\AttemptCancelledException $e) {
    handleLoginException(500, "An unknown server error occurred.");
} catch (\Delight\Auth\AuthError $e) {;
    handleLoginException(401, "An authentication error occurred.");
} catch (\Delight\Auth\EmailNotVerifiedException $e) {
    handleLoginException(401, "Unverified email.");
} catch (\Delight\Auth\InvalidEmailException $e) {
    handleLoginException(401, "Email not registered or invalid.");
} catch (\Delight\Auth\InvalidPasswordException $e) {
    handleLoginException(401, "Wrong password.");
} catch (\Delight\Auth\TooManyRequestsException $e) {
    handleLoginException(403, "Too many requests. Try again later.");
}

handleLoginException(200, '');

?>