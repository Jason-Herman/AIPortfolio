<?php
require 'mysql.php';
require 'common.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    header('Content-Type: application/json');
    $postString = file_get_contents('php://input'); //gets data from ajax post request
    $data = json_decode($postString);

    //action is what specified in ajax call - what to do if to get to php script with multiple functions
    if (!empty($data->action)) {

        //populate dropdowns
        if ($data->action == 'getDropopdownsData') {
            $isESF = true;
            $isIncident = true;
            getDropopdownsData($isESF, $isIncident); //located in common.php -> can be used to get data for Add new Resource
        }
        //search data
        if ($data->action == 'searchResource') {
            searchResource($data);
        }

        //request resource
        if ($data->action == 'requestResource') {
            requestResource($data);
        }
    }
}


function searchResource($searchFields)
{
    $keyword = $searchFields->keyword;
    $esf = $searchFields->esf;
    $incident = $searchFields->incident;
    $location = $searchFields->location;
    $username = $searchFields->username;

    if (gettype($incident) && strlen($incident) == 0) {
        $incident = 'null';
    }
    if (gettype($esf) && strlen($esf) == 0) {
        $esf = 'null';
    }
    if (gettype($location) && strlen($location) == 0) {
        $location = 'null';
    }

    $query = "CALL search_resource('$keyword',$esf,$incident,$location,'$username')";

    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $result = $connect->query($query);

    $resources = array();
    if ($result->num_rows != 0) {


        while ($row = $result->fetch_assoc()) {
            $data = array();
            $data["ResourceID"] = $row["ResourceID"];
            $data["ResourceName"] = $row["ResourceName"];
            $data["ResourceStatus"] = $row["ResourceStatus"];
            $data["Cost"] = $row["Cost"];
            $data["CostDescription"] = $row["CostDescription"];
            $data["OwnerName"] = $row["OwnerName"];
            $data["OwnerUsername"] = $row["OwnerUsername"];
            $data["Distance"] = $row["Distance"];
            $data["IsRequestedByUser"] = $row["IsRequestedByUser"];
            $data["NextAvailableDate"] = $row["NextAvailableDate"];
            $data["ResourceRequestID"] = $row["ResourceRequestID"];
            $data["ScheduledRepairID"] = $row["ScheduledRepairID"];

            array_push($resources, $data);
        }
    }
    echo json_encode($resources);
}


function requestResource($data)
{
    $resourceID = $data->resourceID;
    $incidentID = $data->incidentID;
    $returnDate = $data->returnDate;
    $username = $data->username;


    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();
    $status = array();

    //save resource request
    $resourceRequestID = 0;
    $query = "CALL request_resource($resourceID,$incidentID,'$returnDate','$username',  @resourceRequestID)";
    $call = $connect->prepare($query);
    $call->execute();

    //get out parameter - ID of insert Resource request
    $select = $connect->query('SELECT  @resourceRequestID');
    $result = $select->fetch_assoc();

    $resourceRequestID = $result['@resourceRequestID'];

    if ($resourceRequestID != 0) {

        $status["success"] = true;
        $status["hasAlreadyBeenRequested"] = true;
        $connect->next_result();
        $queryUpdatedResourceInfo = "CALL get_resource_info($resourceID, $resourceRequestID)";
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
                $data["NextAvailableDate"] = $row["NextAvailableDate"];

                array_push($resources, $data);
            }
        }

        $status["resourceUpdated"] = $resources;

    } else {
        $status["success"] = false;
    }
    echo json_encode($status);
}


?>
