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

// Delete and create the collection anew
$res = null;
try {
    $res = $rekognition->deleteCollection([
        "CollectionId" => "main_collection"
    ]);

    $res = $rekognition->createCollection([
        "CollectionId" => "main_collection"
    ]);
} catch(Aws\Rekognition\Exception\RekognitionException $e) {
    handleLoginException(500, "An internal server error occurred");
}

?>