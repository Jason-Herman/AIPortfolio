-- MySQL dump 10.13  Distrib 5.7.12, for Win32 (AMD64)
--
-- Host: team070.gatech.systems    Database: erms
-- ------------------------------------------------------
-- Server version	5.6.27-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping events for database 'erms'
--

--
-- Dumping routines for database 'erms'
--
/*!50003 DROP FUNCTION IF EXISTS `check_resource_requested_by_user` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` FUNCTION `check_resource_requested_by_user`(username VARCHAR (20), incidentID INT (11), resourceID INT (11)) RETURNS tinyint(1)
    READS SQL DATA
BEGIN

DECLARE is_requested tinyint(1) default 0;
/********************************************************/
/* Resource can be requested once for same the incident.
/* Function checks if that resource has been already 
requested for this incident
/********************************************************/
SELECT 
    1
INTO is_requested FROM
    ResourceRequest
WHERE
    RequestedBy = username
        AND ResourceRequest.IncidentID = incidentID
        AND ResourceRequest.ResourceID = resourceID

        AND ExpectedReturnDate = (SELECT 
            MAX(ExpectedReturnDate)
        FROM
            ResourceRequest
        WHERE
            RequestedBy = username
                AND ResourceRequest.IncidentID = incidentID
                AND ResourceRequest.IncidentID = incidentID
				AND ResourceRequest.ResourceID = resourceID);

    RETURN is_requested;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `get_distance_resource_incident` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` FUNCTION `get_distance_resource_incident`(lat1 float, lon1 float,lat2 float, lon2 float) RETURNS float
BEGIN
	  /* Calculate distance between incident and resource */
	  SET lat1=Radians(lat1);
	  SET lon1=Radians(lon1);
	  SET lat2=Radians(lat2);
	  SET lon2=Radians(lon2);
		SET @latDelta=lat2-lat1;
        SET @lonDelta=lon2-lon1;
        
       
		SET @a=POWER(SIN(@latDelta/2),2)+COS(lat1)*COS(lat2)*POWER(SIN(@lonDelta/2),2);
		SET @c=2*ATAN2(SQRT(@a),SQRT((1-@a)));

		
		/*SET @R=6371; /* kilometers*/
        SET @R=3956;  /*miles */
		SET @d=@c*@R;
RETURN @d;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `get_next_available_date` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` FUNCTION `get_next_available_date`(resourceID INT(11), resourceRequestID INT(11)) RETURNS date
BEGIN

	DECLARE dateAvailable datetime default CURDATE();
    DECLARE isScheduleForRepair varchar(1) default 0;
    DECLARE repairStatus varchar(1); 
    DECLARE scheduledRepairID int(11);
    DECLARE resourceStatus VARCHAR(20);
    
	SELECT 
    Status
INTO resourceStatus FROM
    Resource
WHERE
    Resource.ID = resourceID;
    
    /* check if it is scheduled for repair */ 
SELECT 
    ID
INTO scheduledRepairID FROM
    Repair
WHERE
    Repair.ResourceID = resourceID
        AND Status = '0'
        AND DateRecordCreated IN (SELECT 
            MIN(DateRecordCreated)
        FROM
            Repair
        WHERE
            Repair.ResourceID = resourceID
                AND Status = '0');
    
    
    If resourceStatus ='In Repair' THEN

		SELECT DateStarted  + INTERVAL Repair.Duration DAY
		INTO dateAvailable
		FROM Repair 
		WHERE Repair.ResourceID=resourceID AND DateStarted IS NOT NULL AND Status='1'
        AND DateStarted IN
			(SELECT MAX(DateStarted)
			FROM Repair 
            WHERE Repair.ResourceID=resourceID AND DateStarted IS NOT NULL AND Status='1');

    ELSEIF resourceStatus ='In Use' THEN
    
		/* check if it is scheduled for repair. It is in use and there is repair request - get return date + duration of repair */ 
        if scheduledRepairID IS NOT NULL then
			
            IF resourceRequestID IS NULL THEN
				SET resourceRequestID = get_resource_request_id(resourceID, null, null, null);
            END IF;
            
            SELECT ResourceRequest.ExpectedReturnDate  + INTERVAL Repair.Duration DAY
			INTO dateAvailable
			FROM Repair INNER JOIN ResourceRequest ON ResourceRequest.ResourceID=Repair.resourceID            
			WHERE Repair.ID=scheduledRepairID AND ResourceRequest.ID=resourceRequestID
            AND StartDate IN
				(SELECT MAX(StartDate)
				FROM ResourceRequest 
				WHERE ResourceRequest.ID=resourceRequestID);
            
		/* not scheduled for repair - display return date*/
        else
			SELECT ExpectedReturnDate
			INTO dateAvailable
			FROM ResourceRequest 
			WHERE ResourceRequest.ID=resourceRequestID AND StartDate IS NOT NULL 
			AND StartDate IN
				(SELECT MAX(StartDate)
				FROM ResourceRequest 
				WHERE ResourceRequest.ID=resourceRequestID);
        end if;
        
		
	
    ELSE 
		SET dateAvailable=CURDATE();
    END IF;
	
    SET dateAvailable=CAST(dateAvailable AS DATE);
    
RETURN dateAvailable;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `get_resource_request_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` FUNCTION `get_resource_request_id`(resourceID varchar(20), username varchar(20),
																 ownerName varchar(20),
																 incidentID int(11)) RETURNS int(11)
BEGIN
	Declare resourceRequestID int(11);
	
    
    /* specific incident*/
	if incidentID is not NULL THEN
		SELECT 
		MIN(ResourceRequest.ID)
		INTO resourceRequestID FROM
			ResourceRequest
				INNER JOIN
			Resource ON Resource.ID = ResourceRequest.ResourceID
		WHERE
			ResourceRequest.ResourceID = resourceID
				AND ResourceRequest.RequestedBy = username
				AND ResourceRequest.incidentID = incidentID
				AND ReturnDate IS NULL;
	/* resource id is empty - general inquery - find the date of last in use request for resource*/
    ELSE 
		SELECT 
		MIN(ResourceRequest.ID)
		INTO resourceRequestID FROM
			ResourceRequest
				INNER JOIN
			Resource ON Resource.ID = ResourceRequest.ResourceID
		WHERE
			ResourceRequest.ResourceID = resourceID				
			AND ReturnDate IS NULL
            AND StartDate IS NOT NULL;
    END IF;

RETURN resourceRequestID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `get_scheduled_repair_request_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` FUNCTION `get_scheduled_repair_request_id`(resourceID INT(11)) RETURNS int(11)
BEGIN

DECLARE scheduledRepairID int(11);
SELECT 
    ID
INTO scheduledRepairID FROM
    Repair
WHERE
    Repair.ResourceID = resourceID
        AND Status = '0'
        AND DateRecordCreated IN (SELECT 
            MIN(DateRecordCreated)
        FROM
            Repair
        WHERE
            Repair.ResourceID = resourceID
                AND Status = '0');
                
RETURN scheduledRepairID;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `repair_start_date` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` FUNCTION `repair_start_date`(resourceID varchar(11)) RETURNS date
BEGIN

/***********************************/
/* gets start on date for repair
/*
/***********************************/
DECLARE dateStarts datetime;
DECLARE resourceStatus VARCHAR(20);

SELECT 
    Status
INTO resourceStatus FROM
    Resource
WHERE
    Resource.ID = resourceID;
    
    IF resourceStatus ='In Use' THEN
		SELECT ExpectedReturnDate
			INTO dateStarts
			FROM ResourceRequest 
			WHERE ResourceRequest.ResourceID=resourceID AND StartDate IS NOT NULL 
			AND StartDate =
				(SELECT MAX(StartDate)
				FROM ResourceRequest 
				WHERE ResourceRequest.ResourceID=resourceID);
                
    ELSE
    
		SELECT DateStarted  
			INTO dateStarts
			FROM Repair 
			WHERE Repair.ResourceID=resourceID AND DateStarted IS NOT NULL AND Status='1'
			AND DateStarted =
				(SELECT MAX(DateStarted)
				FROM Repair 
				WHERE Repair.ResourceID=resourceID AND DateStarted IS NOT NULL AND Status='1');
              
    END IF; 
    

SET dateStarts=CAST(dateStarts AS DATE);
RETURN dateStarts;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `cancel_repair` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `cancel_repair`(IN repairID int)
BEGIN

	/* ======================================================================================================= */
/*  Stored Procedure to cancel repair if not started - based on ID of repair that has the button

/* ======================================================================================================= */

	DELETE FROM Repair 
	WHERE
		Status = '0' AND Repair.ID = repairID;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `cancel_request` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `cancel_request`(IN requestID int)
BEGIN

	/* ======================================================================================================= */
/*  Stored Procedure to cancel request - based on ID of request that has the button

/* ======================================================================================================= */

DELETE FROM ResourceRequest
WHERE ID=requestID;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `deploy_resource` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `deploy_resource`(IN requestID int(11), IN incID int(11), IN username varchar(20))
BEGIN

	/* ======================================================================================================= */
/*  Stored Procedure to deploy resource - based on ID of repair that has the button
/ returnUpdatedResourceInfo: boolean flag; When true - return updated resource info: next available, status  
/* ======================================================================================================= */


    UPDATE ResourceRequest INNER JOIN Resource ON ResourceRequest.ResourceID=Resource.ID
    SET ResourceRequest.Status = '1', StartDate=curdate(), Resource.Status = 'In Use'
	WHERE ResourceRequest.ID=requestID AND IncidentID = incID  AND Resource.UserName = username AND Resource.Status = 'Available';
	
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_repairs` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `get_repairs`(IN username varchar(20))
BEGIN

/* ======================================================================================================= */
/*  Stored Procedure to get repairs for current user - based on Username

/*  get_next_available_date - stored function; duration + start repair date for In reapir; duration + return date for In Use schedule for repair
/* ======================================================================================================= */
  
    
    SELECT Resource.ID, Repair.ID as RepairID, Resource.ResourceName, repair_start_date(Resource.ID) as DateStarted,
    get_next_available_date(Resource.ID, null) as 'ReadyBy', 
    Repair.Status
	FROM Resource INNER JOIN Repair ON Resource.ID=Repair.ResourceID
	WHERE Repair.Username=Resource.Username AND Resource.Username=username;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_resources_in_use` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `get_resources_in_use`(IN username varchar(20))
BEGIN
	
/* ======================================================================================================= */
/*  Stored Procedure to get resources in use for current user - based on Username

/* ======================================================================================================= */
  
    
    
    SELECT Resource.ID, Resource.ResourceName, Incident.Description, User.Name as Username, ResourceRequest.ID as RequestID,
    Date(ResourceRequest.StartDate) as StartDate, Date(ResourceRequest.ExpectedReturnDate) as ReturnDate
	FROM Incident 
	INNER JOIN ResourceRequest ON Incident.ID=ResourceRequest.IncidentID
	INNER JOIN Resource ON ResourceRequest.ResourceID=Resource.ID
	INNER JOIN User ON User.UserName = Resource.UserName 
	WHERE ResourceRequest.RequestedBy=username AND Resource.Status='In Use' AND StartDate IS NOT NULL
	ORDER BY Resource.ID; 
	
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_resources_requested_by_user` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `get_resources_requested_by_user`(IN username varchar(20))
BEGIN

/* ======================================================================================================= */
/*  Stored Procedure to get resources requested by current user - based on Username
/* get pending user resource requests
/* ======================================================================================================= */
  
    
	SELECT Resource.ID, ResourceRequest.ID as RequestID, Resource.ResourceName,  Incident.Description,  Resource.Username, 
    Date(ResourceRequest.ExpectedReturnDate) as ReturnDate, Resource.Status, ResourceRequest.RequestedBy, Incident.Username as IncidentUsername
	FROM ResourceRequest  
	LEFT JOIN Resource ON ResourceRequest.ResourceID=Resource.ID
	INNER JOIN Incident  ON Incident.ID=ResourceRequest.IncidentID 
   
	WHERE ResourceRequest.RequestedBy=username AND ResourceRequest.Status='0' 
			AND ResourceRequest.ReturnDate IS NULL AND Incident.Username=username 
	ORDER BY ResourceRequest.ExpectedReturnDate;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_resource_info` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `get_resource_info`(IN resourceID INT(11), IN resourceRequestID INT(11))
BEGIN
	/* retrives data for specific resource: 
    /* used in search page to update the view when :
    /* resource has been requested, sent to repair, deployed */
    
    
    /*  resourceRequestID was sent -  get info for specific reesource request*/
    IF resourceRequestID IS NOT NULL THEN
		SELECT Resource.ID as ResourceID, ResourceName, Resource.Status as ResourceStatus,
			  User.Name as OwnerName, User.Username as OwnerUsername
			
			 , get_next_available_date(ResourceRequest.ResourceID, resourceRequestID) as NextAvailableDate 
			 
		
		FROM erms.ResourceRequest 
		LEFT JOIN erms.Resource ON Resource.ID=resourceID
		LEFT JOIN erms.User ON Resource.Username=User.Username
		LEFT JOIN erms.Repair ON Repair.ResourceID=resourceID
		WHERE ResourceRequest.ID=resourceRequestID;
	
    /*  resourceRequestID not sent -  get info for repair*/
	ELSE
		SELECT Resource.ID as ResourceID, ResourceName, Resource.Status as ResourceStatus,
			  User.Name as OwnerName, User.Username as OwnerUsername
			
			 , get_next_available_date(resourceID, null) as NextAvailableDate
             
		FROM erms.Resource 
		LEFT JOIN erms.User ON Resource.Username=User.Username
		LEFT JOIN erms.Repair ON Repair.ResourceID=resourceID
		WHERE Resource.ID=resourceID;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_resource_requests_received` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `get_resource_requests_received`(IN username varchar(20))
BEGIN

/* ======================================================================================================= */
/*  Stored Procedure to get resources requests received by current user - based on Username
/* Retrieve only requests that have not been approved yet
/* ======================================================================================================= */

    
    SELECT Resource.ID, ResourceRequest.ID as RequestID, Resource.ResourceName, Incident.Description, ResourceRequest.RequestedBy, 
    Date(ResourceRequest.ExpectedReturnDate) as ReturnDate, Resource.Status, ResourceRequest.IncidentID, Resource.Username as Username
	FROM Resource 
	INNER JOIN User ON Resource.Username=User.Username 
	INNER JOIN ResourceRequest ON Resource.ID=ResourceRequest.ResourceID
	INNER JOIN Incident ON ResourceRequest.IncidentID=Incident.ID 
	WHERE Resource.Username=username AND ResourceRequest.Status='0'
	ORDER BY ResourceRequest.ExpectedReturnDate;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_search_results_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `get_search_results_data`(IN username Varchar(11), IN incidentID INT (11))
BEGIN
	
   
    
    /* get deails data for the found resources */
    
	INSERT INTO ResourcesInfo (ResourceID, ResourceName,  ResourceStatus,
		OwnerUsername, OwnerName, CostDescription, Cost ,   Distance, IsRequestedByUser, 
        ResourceRequestID, NextAvailableDate,  ScheduledRepairID)
    
       SELECT Resource.ID as ResourceID, ResourceName, Resource.Status as ResourceStatus, Resource.UserName as OwnerUsername,
       User.Name as OwnerName, CostDescription, Resource.Cost as Cost,  ResourceWithinRadius.Distance As Distance,
  
	 	check_resource_requested_by_user(username, incidentID, Resource.ID) as IsRequestedByUser,
        get_resource_request_id(Resource.ID, username, Resource.UserName, incidentID) as ResourceRequestID, 
        get_next_available_date(Resource.ID,  get_resource_request_id(Resource.ID, username,Resource.UserName, incidentID)) as NextAvailableDate,     
        get_scheduled_repair_request_id(Resource.ID) as ScheduledRepairID
	
    FROM ResourceWithinRadius 
    INNER JOIN erms.Resource ON erms.Resource.ID=ResourceWithinRadius.ResourceID
    LEFT JOIN erms.User ON Resource.Username=User.Username
    LEFT JOIN erms.Repair ON Repair.ResourceID=ResourceWithinRadius.ResourceID
    LEFT JOIN erms.ResourceRequest ON ResourceRequest.ID=ResourceWithinRadius.ResourceID
    LEFT JOIN erms.CostOption ON CostOption.Description=CostDescription 
    ORDER BY Distance, ResourceName;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `reject_request` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `reject_request`(IN requestID int)
BEGIN

	/* ======================================================================================================= */
/*  Stored Procedure to reject request - based on ID of request that has the button

/* ======================================================================================================= */

DELETE FROM ResourceRequest
WHERE ID=requestID;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `request_repair` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `request_repair`(username VARCHAR (20), IN resourceID INT(11), In repairDurationDays INT(11))
BEGIN
	/*  check if there are repairs that are completed -  compare current date and rpair unticipaded date */
    DECLARE resourceStatus varchar(20);
    DECLARE resourceOwner varchar(20);
    /* housekeeping - check if resource in repair is repaired */    
    call update_repair_status();
    
    
SELECT 
    Status, Username
INTO resourceStatus , resourceOwner FROM
    Resource
WHERE
    ID = resourceID;
    
    /* only resourcse user can add it to repair*/
    if resourceOwner=username  then
    
		/* resource "Is available" - insert record and start repairing it */
		if resourceStatus='Available' then
			INSERT INTO Repair (Username, Duration, ResourceID, Status, DateStarted) 
			VALUES (username, repairDurationDays, resourceID, '1', curDate());	
			
			UPDATE Resource 
			SET 
				Resource.Status = 'In Repair'
			WHERE
				Resource.ID = resourceID;
			
		/* resource "In Use" = schedule to repair */
	   elseif resourceStatus='In Use' then
			INSERT INTO Repair (Username, Duration, ResourceID) 
			VALUES (username, repairDurationDays, resourceID);
		end if;
    
    end if;
    
	
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `request_resource` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `request_resource`(In resourceID INT(11),In incidentID INT(11), 
In returnDate date, In requestedBy VARCHAR(20), out resourceRequestID int(11))
BEGIN
	/* insert into resource request table */
	DECLARE hasBeenRequested tinyint(1) Default 0;
    
    /* check if resource has already been requested for this incident */
SELECT 
    EXISTS( SELECT 
            1
        FROM
            ResourceRequest
        WHERE
            ResourceRequest.IncidentID = incidentID
                AND ResourceRequest.ResourceID = resourceID)
INTO hasBeenRequested;
    
    /* if resource has not been requested for this incident - request resource */
    If hasBeenRequested =0 then
		SET returnDate=CAST(returnDate AS DATETIME);
		INSERT INTO ResourceRequest (ID, IncidentID,ExpectedReturnDate, RequestedBy, ResourceID) 
		VALUES (null,incidentID, returnDate,requestedBy, resourceID);
        
        SET resourceRequestID = LAST_INSERT_ID();
    END IF;
	

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `return_resource` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `return_resource`(IN username VARCHAR(20), IN resID int)
BEGIN
	/* ======================================================================================================= */
/*  Stored Procedure to return resource - based on ID of resource that has the button

/* ======================================================================================================= */
/* Return date is specified when resource is requested (page 9). Should it then be updated here ??? Should we add expected return date column */
    SET SQL_SAFE_UPDATES=0;
    
	UPDATE Resource
    SET Status = 'Available'
	WHERE Resource.ID=resID;
    
	UPDATE ResourceRequest
    SET ReturnDate = CURDATE()  
	WHERE ResourceID=resID AND RequestedBy=username;
    
	UPDATE Resource
    SET Status = 'In Repair'
    WHERE ID IN (SELECT ResourceID FROM Repair WHERE ResourceID = resID AND Status = 0 AND DateStarted < CURDATE());
    
    UPDATE Repair
    SET Status = 1 AND DateStarted = CURDATE()
    WHERE ResourceID = resID AND Status = 0 AND DateStarted < CURDATE();

    
    SET SQL_SAFE_UPDATES=1;
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_resource` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `search_resource`(IN keyword varchar(100), IN esfID int, IN incidentID int, IN distanceRadius float, IN username VARCHAR(11))
BEGIN
	
/* ======================================================================================================= */
/*  Stored Procedure to search resource based on

    Input parameters: 
		1) User inputed String keyword that is searched in Resource name, model and capabilities
        2) Resources Primary and Secondary ESF 
        3) Selected incident
        4) Max distance between Incident and Resource: Kilometers 

/* ======================================================================================================= */

	DECLARE searchWord varchar(100);   
    DECLARE done INT DEFAULT FALSE;	
    
    
    /* loop while there are rows available*/
    DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done=1; 
    
     /* create temprary table to store resource ID that suttisfy the search parameters */
     /* temporary table content can be reached by other stored procedures
      while the connection is open */
    DROP TEMPORARY TABLE IF EXISTS ResourceWithinRadius;
    CREATE temporary TABLE ResourceWithinRadius 
    (AutoID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ResourceID INT NOT NULL, Distance FLOAT, IncidentID INT NOT NULL)
    ENGINE=MEMORY;
	
	/* create temprary table to store detailed info for resources that satisfy the search parameters */
     
    DROP TEMPORARY TABLE IF EXISTS ResourcesInfo;
    CREATE temporary TABLE ResourcesInfo 
		(ResourceID INT NOT NULL,ResourceName VARCHAR(100), CostDescription VARCHAR(200), Cost FLOAT, 
        ResourceStatus SET('Available', 'In Use', 'In Repair'),
		OwnerName VARCHAR(36), OwnerUsername VARCHAR(20), 
		Distance FLOAT, IsRequestedByUser tinyint(1), NextAvailableDate DATE, ResourceRequestID varchar(20),
        ScheduledRepairID varchar(20))
    ENGINE=MEMORY;
    
    SET SQL_SAFE_UPDATES=0;
    /*  if keyword is empty - use wild card '%' to get all values*/
    IF LENGTH(keyword)>0 THEN
		SET searchWord=  CONCAT("%",keyword,"%");
	ELSE 
		SET searchWord='%';
    END IF;
    
    
    /* House keeping - check the repair and if repair is complete, update repair status */     
     call update_repair_status(); 
    
    /* call different stored procedures based on the passed parameters */
    /* The location-based search */
	IF esfID IS NOT NULL AND incidentID IS NOT NULL and distanceRadius IS NOT NULL THEN	
		CALL search_resource_within_distance(searchWord, esfID, incidentID , distanceRadius);
     
	ELSEIF  esfID IS NULL AND incidentID IS NOT NULL and distanceRadius IS NOT NULL THEN
		CALL search_resource_within_distance_no_ESF(searchWord, incidentID , distanceRadius);
	
    /* ESF is selected but either Incident ID or distance is blank - search by primary and secondary ESF and/or keyword */
    ELSEIF esfID IS NOT NULL THEN
    
		INSERT INTO ResourceWithinRadius (ResourceID)
		SELECT DISTINCT Resource.ID AS ResourceID 
		FROM Resource 
		LEFT JOIN `Resource-Capability`
		ON Resource.ID =`Resource-Capability`.ID
		LEFT JOIN HasAdditional
		ON Resource.ID = HasAdditional.ID
		WHERE (Resource.UniqueNumber=esfID OR HasAdditional.UniqueNumber=esfID)
			AND (Model LIKE searchWord OR  ResourceName LIKE searchWord OR Capability LIKE searchWord);
		

	/* all search fields blank Or have keyword -> display all resources */
	ELSE 
		INSERT INTO ResourceWithinRadius (ResourceID) 
		SELECT DISTINCT Resource.ID AS ResourceID 
		FROM Resource
        LEFT JOIN `Resource-Capability`
		ON Resource.ID =`Resource-Capability`.ID
        WHERE Model LIKE searchWord OR  ResourceName LIKE searchWord OR Capability LIKE searchWord;		
    END IF;
    
    /* get resource details */
    CALL get_search_results_data(username, incidentID);
  
    
    /* send results to front end */
   
	SELECT DISTINCT
    *
FROM
    ResourcesInfo;
    

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_resource_within_distance` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `search_resource_within_distance`(IN keyword varchar(100), 
																	IN esfID int, IN incidentID int,
                                                                    IN distanceRadius float)
BEGIN
	
/* ======================================================================================================= */
/*  Stored Procedure to search resource based on

    Input parameters: 
		1) User inputed String keyword that is searched in Resource name, model and capabilities
        2) Resources Primary and Secondary ESF 
        3) Selected incident
        4) Max distance between Incident and Resource: Kilometers 

/* ======================================================================================================= */

    DECLARE incidentLat, incidentLon, distance FLOAT;
    DECLARE resourceID INT(11);

	IF LENGTH(keyword)=0 THEN
		SET keyword= '%' ;
	END IF;
        
   /* get latitude and longitude of the incident */
	SELECT 
    Latitude AS Latitude, Longitude AS Longitude
INTO incidentLat , incidentLon FROM
    erms.Incident
WHERE
    ID = incidentID;
    

  /* get reources withing the distance of incident */
  INSERT INTO ResourceWithinRadius
   (ResourceID,  Distance, IncidentID)
   
SELECT DISTINCT
    Resource.ID AS ID,
    get_distance_resource_incident(Resource.Latitude,Resource.Longitude, incidentLat, incidentLon) as Distance,
    incidentID
FROM
    Resource
        LEFT JOIN
    `Resource-Capability` ON Resource.ID = `Resource-Capability`.ID
        LEFT JOIN
    HasAdditional ON Resource.ID = HasAdditional.ID
WHERE
    (Resource.UniqueNumber = esfID
        OR HasAdditional.UniqueNumber = esfID)
        
		AND (Model LIKE keyword
        OR ResourceName LIKE keyword
        OR Capability LIKE keyword)
        
        AND get_distance_resource_incident(Resource.Latitude,Resource.Longitude, incidentLat,incidentLon) <= distanceRadius;

  /*SELECT * FROM ResourceWithinRadius; */
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `search_resource_within_distance_no_ESF` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `search_resource_within_distance_no_ESF`(IN keyword varchar(100), 
																	 IN incidentID int,
                                                                    IN distanceRadius float)
BEGIN
	DECLARE incidentLat, incidentLon, distance FLOAT;
    
	DROP TEMPORARY TABLE IF EXISTS ResourceGeoData;
    CREATE temporary TABLE ResourceGeoData 
    (AutoID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ResourceID INT NOT NULL, 
    Lat FLOAT,Lon FLOAT,Distance FLOAT)
    ENGINE=MEMORY;
    
    /* format key word */
	IF LENGTH(keyword)=0 THEN
		SET keyword= '%' ;
	END IF;
			
	/* get latitude and longitude of the incident */
	SELECT 
    Latitude AS Latitude, Longitude AS Longitude
	INTO incidentLat , incidentLon FROM
		erms.Incident
	WHERE
		ID = incidentID;
    
    
    /******* get resources that qualify based on the keyword************************/
    /******* calculate their distance from incident         ************************/
    INSERT INTO ResourceGeoData
    (ResourceID,  Lat, Lon,Distance)
   
	SELECT DISTINCT
    Resource.ID AS ID, Resource.Latitude, Resource.Longitude, 
    get_distance_resource_incident(Resource.Latitude,Resource.Longitude, incidentLat, incidentLon) as Distance
   
	FROM
    Resource
        LEFT JOIN
    `Resource-Capability` ON Resource.ID = `Resource-Capability`.ID
        LEFT JOIN
    HasAdditional ON Resource.ID = HasAdditional.ID
	WHERE
			Model LIKE keyword
			OR ResourceName LIKE keyword
			OR Capability LIKE keyword;
	
    
    /***pick items that are within radius ***/

    INSERT INTO ResourceWithinRadius
   (ResourceID,  Distance, IncidentID)
   
	SELECT DISTINCT
		ResourceID,
		ResourceGeoData.Distance,
		incidentID
	FROM
		ResourceGeoData
		  
	WHERE
		ResourceGeoData.Distance <= distanceRadius;

	DROP TEMPORARY TABLE IF EXISTS ResourceGeoData;
    
	/*SELECT * FROM ResourceWithinRadius; */
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `update_repair_status` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`team070`@`%` PROCEDURE `update_repair_status`()
BEGIN
	
Declare nextRepairDate datetime;
    
/* complete repair */
UPDATE Repair
        JOIN
    Resource ON Repair.ResourceID = Resource.ID 
SET 
    Resource.Status = 'Available',
    Repair.Status = '2'
WHERE
    Repair.ResourceID = resourceID
        AND Resource.Status = 'In Repair'
        AND (Repair.DateStarted + INTERVAL Repair.Duration DAY < CURDATE())
        AND Repair.Status = '1';
  
  
    
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 15:06:35
