<?php

require 'mysql.php';
require 'common.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    header('Content-Type: application/json');
    $postString = file_get_contents('php://input'); //gets data from ajax post request
    $data = json_decode($postString);
    $username = $data->username;


//action is what specified in ajax call - what to do if to get to php script with multiple functions
    if (!empty($data->action)) {
        //get resource status data after page loaded
        if ($data->action == 'getResourceStatus') {
            $connect = new mysql(mysql::$db_address);
            $connect = $connect->Connect();

            $query = "SELECT ESF.UniqueNumber AS UniqueNumber, ESF.Description AS Description, COUNT(Resource.ID) AS Total_Resources,
         (SELECT COUNT(Resource.ID)
         FROM ESF NATURAL JOIN Resource
         WHERE Resource.Username = '$username' AND Resource.Status='In Use') AS 'In_Use'
         FROM ESF NATURAL JOIN Resource
         WHERE Resource.Username = '$username'
         GROUP BY ESF.UniqueNumber;";

            $result = $connect->query($query);
            $resources = array();
            if ($result->num_rows != 0) {

                while ($row = $result->fetch_assoc()) {
                    $data = array();
                    $data["UniqueNumber"] = $row["UniqueNumber"];
                    $data["Description"] = $row["Description"];
                    $data["Total_Resources"] = $row["Total_Resources"];
                    $data["In_Use"] = $row["In_Use"];
                    array_push($resources, $data);
                }
            }

            //return all results;
            echo json_encode($resources);
        }

    }


}


?>