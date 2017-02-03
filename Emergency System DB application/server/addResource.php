<?php
require 'mysql.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    header('Content-Type: application/json');
    $postString = file_get_contents('php://input'); //gets data from ajax post request
    $data = json_decode($postString);

    $resourceName = $data->resource;
    $primaryESF = $data->esf;
    $secondaryESF = $data->esfsSelectAdditional;
    $model = $data->model;
    $capabilties = $data->capabilities;
    $lat = $data->lat;
    $long = $data->long;
    $cost = $data->cost;
    $per = $data->per;
    $username = $data->username;


    if (gettype($resourceName) && strlen($resourceName) == 0) {
        $resourceName = 'null';
    }
    if (gettype($primaryESF) && strlen($primaryESF) == 0) {
        $primaryESF = 'null';
    }
    if (gettype($model) && strlen($model) == 0) {
        $model = 'null';
    }
    if (gettype($lat) && strlen($lat) == 0) {
        $lat = 'null';
    }
    if (gettype($long) && strlen($long) == 0) {
        $long = 'null';
    }
    if (gettype($cost) && strlen($cost) == 0) {
        $cost = 'null';
    }
    if (gettype($per) && strlen($per) == 0) {
        $per = 'null';
    }
    if (gettype($username) && strlen($username) == 0) {
        $username = 'null';
    }
}

$query = "INSERT INTO Resource (ResourceName, Model, Latitude, Longitude, Cost, Status, UserName, UniqueNumber, CostDescription)
VALUES ('$resourceName', '$model', $lat ,$long, $cost, 'Available', '$username', $primaryESF, '$per')";

/* connect to database */
$connect = new mysql(mysql::$db_address);
$connect = $connect->Connect();
$result = $connect->query($query);

if (!$result) {
    echo false;
} else {
    $last_id = $connect->insert_id;

    for ($i = 0; $i < sizeof($secondaryESF); $i++) {
        $temp = "INSERT INTO HasAdditional (ID, UniqueNumber) VALUES (" . $last_id . ", " . $secondaryESF[$i] . ");";
        $result = $connect->query($temp);
        if (!$result) {
            echo false;
        }
    }


    for ($i = 0; $i < sizeof($capabilties); $i++) {
        $tempie = (string)$capabilties[$i];
        $temp = "INSERT INTO erms.`Resource-Capability` (ID, Capability) VALUES (" . $last_id . ", " . "'" . $tempie . "'" . ");";
        $result = $connect->query($temp);
        if (!$result) {
            echo false;
        }
    }
}


echo json_encode(true);
