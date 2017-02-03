<?php
require 'mysql.php';
require 'common.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    header('Content-Type: application/json');
    $postString = file_get_contents('php://input'); //gets data from ajax post request
    $data = json_decode($postString);

//action is what specified in ajax call - what to do if to get to php script with multiple functions
    if (!empty($data->action)) {

        //get resource status data after page loaded
        if ($data->action == 'getResourceStatus') {
            getResourceStatus($data);
        }

        //return resource
        if ($data->action == 'return') {
            returnResource($data);
        }

        //cancel resource request made by user
        if ($data->action == 'cancelRequest') {
            cancelRequest($data);
        }

        //todo: need to implement deploy in common.php?
        //deploy resource - common.php to be able to use resource status
        if ($data->action == 'deploy') {
            deployResource($data);
        }

        //reject resource request received by user
        if ($data->action == 'rejectRequest') {
            rejectRequest($data);
        }

        //cancel repair
        if ($data->action == 'cancelRepair') {
            cancelRepair($data);
        }
    }
}

function getResourceStatus($data)
{
    $username = $data->username;

    $allData = array(); //datastructure to return with all results
    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();


    $queryResourcesInUse = "CALL get_resources_in_use('$username')";
    $resultResourcesInUse = $connect->query($queryResourcesInUse);
    $resourcesInUse = array(); //stores all resources in use
    if ($resultResourcesInUse->num_rows != 0) {


        while ($row = $resultResourcesInUse->fetch_assoc()) {
            $data = array(); //specific record
            $data["ID"] = $row["ID"];

            $data["ResourceName"] = $row["ResourceName"];
            $data["Description"] = $row["Description"];
            $data["Username"] = $row["Username"];

            $data["StartDate"] = $row["StartDate"];
            $data["ReturnDate"] = $row["ReturnDate"];

            array_push($resourcesInUse, $data);
        }

    }
    $allData["resourcesInUse"] = $resourcesInUse;

    $connect->next_result();

    $queryResourcesRequestedByMe = "CALL get_resources_requested_by_user('$username')";
    $resultResourcesRequestedByMe = $connect->query($queryResourcesRequestedByMe);
    $resourcesRequestedByMe = array(); //stores all resources in use
    if ($resultResourcesRequestedByMe->num_rows != 0) {


        while ($row = $resultResourcesRequestedByMe->fetch_assoc()) {
            $data = array(); //specific record
            $data["ID"] = $row["ID"];
            $data["RequestID"] = $row["RequestID"];
            $data["ResourceName"] = $row["ResourceName"];
            $data["Description"] = $row["Description"];
            $data["Username"] = $row["Username"];
            $data["ReturnDate"] = $row["ReturnDate"];
            $data["Status"] = $row["Status"];

            array_push($resourcesRequestedByMe, $data);
        }
    }
    $allData["resourcesRequestedByMe"] = $resourcesRequestedByMe;
    $connect->next_result();

    $queryResourcesReceivedByMe = "CALL get_resource_requests_received('$username')";
    $resultResourcesReceivedByMe = $connect->query($queryResourcesReceivedByMe);
    $resourcesReceivedByMe = array(); //stores all resources in use
    if ($resultResourcesReceivedByMe->num_rows != 0) {


        while ($row = $resultResourcesReceivedByMe->fetch_assoc()) {
            $data = array(); //specific record
            $data["ID"] = $row["ID"];
            $data["ResourceName"] = $row["ResourceName"];
            $data["Description"] = $row["Description"];
            $data["Username"] = $row["Username"];
            $data["ReturnDate"] = $row["ReturnDate"];
            $data["Status"] = $row["Status"];
            $data["IncidentID"] = $row["IncidentID"];
            $data["RequestID"] = $row["RequestID"];
            $data["RequestedBy"] = $row["RequestedBy"];

            array_push($resourcesReceivedByMe, $data);
        }
    }
    $allData["resourcesReceivedByMe"] = $resourcesReceivedByMe;

    $connect->next_result();

    $queryResourcesRepair = "CALL get_repairs('$username')";
    $resultResourcesInRepair = $connect->query($queryResourcesRepair);

    $resourcesInRepair = array(); //stores all resources in use
    if ($resultResourcesInRepair->num_rows != 0) {


        while ($row = $resultResourcesInRepair->fetch_assoc()) {
            $data = array(); //specific record
            $data["ID"] = $row["ID"];
            $data["RepairID"] = $row["RepairID"];
            $data["ResourceName"] = $row["ResourceName"];
            $data["DateStarted"] = $row["DateStarted"];
            $data["ReadyBy"] = $row["ReadyBy"];
            $data["Status"] = $row["Status"];

            array_push($resourcesInRepair, $data);
        }
    }
    $allData["resourcesInRepair"] = $resourcesInRepair;


    //return all results;
    echo json_encode($allData);
}

function returnResource($data)
{
    $resourceID = $data->resourceID;
    $username = $data->username;
    $query = "CALL return_resource( '$username',$resourceID)";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {

        $status["success"] = true;
    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}

function cancelRequest($data)
{
    $requestID = $data->requestID;

    $query = "CALL cancel_request($requestID)";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {
        $status["success"] = true;
    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}

function deployResource($data)
{
    $requestID = $data->requestID;
    $incidentID = $data->incidentID;
    $username = $data->username;

    $query = "CALL deploy_resource($requestID,$incidentID,'$username')";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {
        $status["success"] = true;

    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}

function rejectRequest($data)
{
    $requestID = $data->requestID;

    $query = "CALL reject_request($requestID)";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {
        $status["success"] = true;
    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}

function cancelRepair($data)
{
    $resourceID = $data->resourceID;


    $query = "CALL cancel_repair($resourceID)";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);
    $status = array();

    if ($result) {
        $status["success"] = true;

    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}