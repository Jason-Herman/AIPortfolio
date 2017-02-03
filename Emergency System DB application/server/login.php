<?php

require 'mysql.php';



if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    header('Content-Type: application/json');
    $postString = file_get_contents('php://input'); //gets data from ajax post request
    $data = json_decode($postString);

    if (empty($data->password) || empty($data->username)) {
        $errorMsg = "Please provide both username and password.";
        echo json_encode(array('error' => $errorMsg));
    } else {

        /* connect to database */
        $connect = new mysql(mysql::$db_address);
        $connect = $connect->Connect();
        $query = "SELECT * FROM User WHERE Username = '$data->username' AND Password = '$data->password'";

        $result = $connect->query($query);
        if ($result->num_rows == 0) {
            /* login failed */
            $errorMsg = "Login failed.  Please try again.";

            echo json_encode(array('error' => $errorMsg));

        } else {
            /* login successful */
            //process records one by one
			
            
            while ($row = $result->fetch_assoc()) {
                $user =array();
                $user["Username"]=$row["Username"];
                $user["Name"]=$row["Name"];
				$user["Type"]=$row["UserType"];

                $hasMoreInfo=false;
                if($row["UserType"]=="1"){
                    $queryUserData = "SELECT PopulationSize FROM Municipality WHERE Username = '$data->username'";
                    $hasMoreInfo=true;
                }
                else if($row["UserType"]=="2"){
                    $queryUserData = "SELECT Jurisdiction FROM GovernmentAgency WHERE Username = '$data->username'";
                    $hasMoreInfo=true;
                }
                else if($row["UserType"]=="3"){
                    $queryUserData = "SELECT Location FROM Company WHERE Username = '$data->username'";
                    $hasMoreInfo=true;
                }
                if( $hasMoreInfo==true){
                    $resultDetail = $connect->query($queryUserData);
                    if ($resultDetail->num_rows >0) {
                        while ($rowDetail = $resultDetail->fetch_row()) {
                            $user["Detail"]=$rowDetail[0];
                        }
                    }
                }

							

                //e.g when multiple records
                //array_push($allData, $user)
                //TODO Add queries to retrieve data if company, individual, municipality, etc
            }
			
            //e.g when multiple records
            //echo json_encode($allData);
            echo json_encode($user);
        }
    }
}
?>
