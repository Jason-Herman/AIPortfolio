<?php

//isESF  - boolean flag whether to get ESF data
//isIncident - boolean flag whether to get Incidents

//populates dropdowns: move to common.php so can be used in new resource
function getDropopdownsData($isESF,$isInsident){
    $allData = array ();
    /* connect to database */
    $connect = new mysql(mysql::$db_address);
    $connect = $connect->Connect();

    if($isESF){

        $query = "SELECT * FROM ESF";
        $result = $connect->query($query);

        if ($result->num_rows !=0) {
            $esfs=array();
            while ($row = $result->fetch_assoc()) {
                $data = array();
                $data["UniqueNumber"]=$row["UniqueNumber"];
                $data["Description"]=$row["Description"];


                array_push($esfs, $data);

            }
            $allData["esfs"]= $esfs;
        }
    }


    if($isInsident){
        $query = "SELECT * FROM Incident;";
        $result = $connect->query($query);

        if ($result->num_rows !=0) {
            $incidents=array();
            while ($row = $result->fetch_assoc()) {
                $data =array();
                $data["ID"]=$row["ID"];
                $data["Description"]=$row["Description"];
                $data["IncidentUsername"]=$row["Username"];

                array_push($incidents, $data);

            }
            $allData["incidents"]= $incidents;
        }
    }

    echo json_encode($allData);
}


?>
