<?php

require __DIR__ . '/vendor/autoload.php';
$db = new \PDO('mysql:dbname=accounts;host=localhost;charset=utf8mb4', 'root', '');

function handleLoginException($responseCode, $message) {
    http_response_code($responseCode);

    // Create response
    $response = new stdClass();
    $response->rationale = $message;

    // Echo response in json
    echo json_encode($response);
    error_log($response);
    exit();
}

$str = file_get_contents('php://input');

for($n = 0; $n < strlen($str); $n++) {
    if($str[$n] == "]") {
        $str = substr($str, $n+2);
        break;
    }
}

var_dump(json_decode($str));

$json = json_decode($str, $assoc=true)

// Add reading to the database
$query = $db->prepare("
    INSERT INTO scans
    (ssid, bssid, rssi, dev_id, scan_time, security, channel)
    VALUES (:ssid, :bssid, :rssi, :dev_id, :scan_time, :security, :channel)
");

$isValid = $query->bindParam(":ssid", $message, PDO::PARAM_STR, 4096);

if(!$isValid || strlen($message) == 0) {
    handleLoginException(400, "Invalid ssid");
}

$hasSucceeded = $query->execute();

if(!$hasSucceeded) {
    handleLoginException(500, "An internal server error occurred");
}




?>