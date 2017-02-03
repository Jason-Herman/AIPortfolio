<?php
require 'mysql.php';
require 'common.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    header('Content-Type: application/json');
    $postString = file_get_contents('php://input'); //gets data from ajax post request
    $data = json_decode($postString);

    //action is what specified in ajax call - what to do if to get to php script with multiple functions
    if (!empty($data->action)) {

        //deploy resource - common.php to be able to use  resource status
        if ($data->action == 'deploy') {
            deployResource($data);
        }

        if($data->action=='requestRepairResource'){
            requestRepairResource($data);
        }
    }
}



function deployResource($data){
    $resourceRequestID = $data->resourceRequestID;
    $incidentID = $data->incidentID;
    $username = $data->username;
    $resourceID = $data->resourceID;

    $query = "CALL deploy_resource($resourceRequestID, $incidentID, '$username')";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {
        $status["success"] = true;

        $connect->next_result();
        $queryUpdatedResourceInfo="CALL get_resource_info($resourceID, $resourceRequestID)";
        $resultUpdatedResource = $connect->query($queryUpdatedResourceInfo);

        $resources = array();
        if ($resultUpdatedResource->num_rows != 0) {

            while ($row = $resultUpdatedResource->fetch_assoc()) {
                $data = array();
                $data["ResourceID"] = $row["ResourceID"];
                $data["ResourceName"] = $row["ResourceName"];
                $data["ResourceStatus"] = $row["ResourceStatus"];
                $data["OwnerName"] = $row["OwnerName"];
                $data["OwnerUsername"] = $row["OwnerUsername"];
                $data["NextAvailableDate"]=$row["NextAvailableDate"];

                array_push($resources, $data);
            }
        }
        $status["resorceUpdated"]=$resources;
    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}


/* adds resource to Repair table */
function requestRepairResource($data){
    $resourceID = $data->repairResourceID;
    $resourceRequestID = $data->requestResourceID;

    if(is_null($resourceRequestID) ||(is_string($resourceRequestID) && strlen($resourceRequestID)==0)){
        $resourceRequestID='null';
    }
    $username = $data->username;
    $duration = $data->duration;

    $query = "CALL request_repair('$username',$resourceID, $duration)";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {
        $status["success"] = true;

        $connect->next_result();
        $queryUpdatedResourceInfo="CALL get_resource_info($resourceID, $resourceRequestID)";
        $resultUpdatedResource = $connect->query($queryUpdatedResourceInfo);

        $resources = array();
        if ($resultUpdatedResource->num_rows != 0) {

            while ($row = $resultUpdatedResource->fetch_assoc()) {
                $data = array();
                $data["ResourceID"] = $row["ResourceID"];
                $data["ResourceName"] = $row["ResourceName"];
                $data["ResourceStatus"] = $row["ResourceStatus"];
                $data["OwnerName"] = $row["OwnerName"];
                $data["OwnerUsername"] = $row["OwnerUsername"];
                $data["NextAvailableDate"]=$row["NextAvailableDate"];

                array_push($resources, $data);
            }
        }
        $status["resorceUpdated"]=$resources;


    } else {
        $status["success"] = false;
    }

    echo json_encode($status);

}