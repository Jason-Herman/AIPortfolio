<?php
require 'mysql.php';

header('Content-Type: application/json');
$postString = file_get_contents('php://input'); //gets data from ajax post request
$data = json_decode($postString);

$date = $data->incidentDate;
$description = $data->descrit;
$latitude = $data->lat;
$longitude = $data->long;
$username = $data->username;

if (gettype($date) && strlen($date) == 0) {
    $date = 'null';
}
if (gettype($description) && strlen($description) == 0) {
    $description = 'null';
}
if (gettype($latitude) && strlen($latitude) == 0) {
    $latitude = 'null';
}
if (gettype($longitude) && strlen($longitude) == 0) {
    $longitude = 'null';
}
if (gettype($username) && strlen($username) == 0) {
    $username = 'null';
}

$query="INSERT INTO Incident (Date, Latitude, Longitude, Description, Username)
VALUES ('$date', $latitude, $longitude, '$description', '$username')";

/* connect to database */
$connect = new mysql(mysql::$db_address);
$connect = $connect->Connect();
$result = $connect->query($query);

$status = array();

if ($result) {
    $status["success"]=true;
}
else{
    $status["success"]=false;
}
echo json_encode($status);
