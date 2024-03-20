<?php
/**
 * User: emilxp
 * Date: 5/17/2018
 * Time: 2:53 PM
 */

require __DIR__ . '/../vendor/autoload.php';
$db = new \PDO('mysql:dbname=accounts;host=localhost;charset=utf8mb4', 'root', '');
$auth = new \Delight\Auth\Auth($db, null, null, false);

?>

<!DOCTYPE html>
<html>
<head>
</head>
<body>
<p>
    Current user: <?= $auth->getEmail(); ?>
</p>

<h2>Post Receive</h2>
<form action="/auth/receive.php" method="post">
    Email: <input type="text" name="email" value="emilxp3@gmail.com"><br>
    Password: <input type="text" name="password" value="12345"><br>
    <input type="submit" value="Submit">
</form>

<h2>Logout</h2>
<form action="/auth/logout.php" method="post">
    <input type="submit" value="Submit">
</form>

<h2>Delete User</h2>
<form action="/auth/delete-user.php" method="post">
    <input type="submit" value="Submit">
</form>

<h2>Login</h2>
<form action="/auth/login.php" method="post">
    Email: <input type="text" name="email" value="emilxp3@gmail.com"><br>
    Password: <input type="text" name="password" value="12345"><br>
    <input type="submit" value="Submit">
</form>

<h2>Register</h2>
<form action="/auth/register.php" method="post">
    Email: <input type="text" name="email" value="emilxp3@gmail.com"><br>
    Password: <input type="text" name="password" value="12345"><br>
    Confirm Password: <input type="text" name="confirm_password" value="12345"><br>
    <input type="submit" value="Submit">
</form>

<h2>Set User Info</h2>
<form action="/auth/set-user-info.php" method="post">
    Name: <input type="text" name="name" value="name"><br>
    Occupation: <input type="text" name="occupation" value="occupation"><br>
    Birthday: <input type="text" name="birthday" value="1998-01-23"><br>
    Interests: <input type="text" name="interests" value="some, bla, some"><br>
    Introduction: <input type="text" name="introduction" value="some introduction"><br>
    <input type="submit" value="Submit">
</form>

<h2>Set Profile Picgture</h2>
<form action="/auth/set-profile-picture.php" method="post" enctype="multipart/form-data">
    Select image to upload:<br/><br/>
    <input type="file" name="picture" id="picture"><br/><br/>
    <input type="submit" value="Upload Image" name="submit">
</form>

<h2>Set Matchmaking Picture</h2>
<form action="/auth/set-matchmaking-picture.php" method="post" enctype="multipart/form-data">
    Select image to upload:<br/><br/>
    <input type="file" name="picture" id="picture"><br/><br/> <!-- CHANGE THE NAMES??? moze bi change da e just picture??? -->
    <input type="submit" value="Upload Image" name="submit">
</form>

<h2>Get User Info</h2>
<!-- TODO: neka go napravim sas GET variable, koito da e email -->
<form action="/auth/user-info.php" method="post">
    Email: <input type="text" name="email" value="emilxp3@gmail.com"><br>
    <input type="submit" value="Submit">
</form>

<h2>Send Message</h2>
<form action="/auth/send-message.php" method="post">
    User Id: <input type="text" name="user_id" value="7"><br>
    Message: <input type="text" name="message" value="This is an example message"><br>
    <input type="submit" value="Submit">
</form>

<h2>Get Messages</h2>
<form action="/auth/get-messages.php" method="post">
    After timestamp: <input type="text" name="timestamp" value="2011-12-21 02:20:30"><br>
    Number of Messages: <input type="text" name="number_of_messages" value="20"><br>
    <input type="submit" value="Submit">
</form>

<h2>Amazon Rekognition Faces</h2>
<?php
$rekognition = new Aws\Rekognition\RekognitionClient([
    'version' => 'latest',
    'region' => 'us-east-1',
    'credentials' => [
        'key' => 'AKIAIVQHCVCV56ZSNRCQ',
        'secret' => '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'
    ]
]);

$res = null;
try {
    $res = $rekognition->listFaces([
        'CollectionId' => 'main_collection',
        'MaxResults' => 20
    ]);

    var_dump($res);

    if(isset($res['NextToken'])) {
        $res = $rekognition->listFaces([
            'CollectionId' => 'main_collection',
            'MaxResults' => 20,
            'NextToken' => $res['NextToken']
        ]);

        var_dump($res);
    }
} catch(Aws\Rekognition\Exception\RekognitionException $e) {
    handleLoginException(500, "An internal server error occurred");
}
?>

</body>
</html>
