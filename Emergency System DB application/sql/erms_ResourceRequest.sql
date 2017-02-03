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
-- Table structure for table `ResourceRequest`
--

DROP TABLE IF EXISTS `ResourceRequest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ResourceRequest` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IncidentID` int(11) NOT NULL,
  `ReturnDate` datetime DEFAULT NULL,
  `Status` set('0','1') NOT NULL DEFAULT '0',
  `StartDate` datetime DEFAULT NULL,
  `RequestedBy` varchar(20) NOT NULL,
  `ResourceID` int(11) NOT NULL,
  `ExpectedReturnDate` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `ResourceRequest_User_idx` (`RequestedBy`),
  KEY `ResourceRequest_Incident_idx` (`IncidentID`),
  KEY `ResourceRequest_Resource_idx` (`ResourceID`),
  CONSTRAINT `ResourceRequest_Incident` FOREIGN KEY (`IncidentID`) REFERENCES `Incident` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ResourceRequest_Resource` FOREIGN KEY (`ResourceID`) REFERENCES `Resource` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ResourceRequest_User` FOREIGN KEY (`RequestedBy`) REFERENCES `User` (`Username`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ResourceRequest`
--

LOCK TABLES `ResourceRequest` WRITE;
/*!40000 ALTER TABLE `ResourceRequest` DISABLE KEYS */;
INSERT INTO `ResourceRequest` VALUES (2,94,NULL,'0',NULL,'delta',31,'2016-11-27 00:00:00'),(4,13,NULL,'1','2016-11-22 00:00:00','delta',31,'2016-11-29 00:00:00'),(5,13,NULL,'0',NULL,'delta',30,'2016-11-28 00:00:00'),(6,13,NULL,'0',NULL,'delta',47,'2016-11-30 00:00:00'),(7,94,NULL,'1','2016-11-22 00:00:00','delta',30,'2016-11-24 00:00:00'),(8,6,NULL,'1','2016-11-22 00:00:00','noob',21,'2016-12-07 00:00:00'),(9,6,NULL,'0',NULL,'noob',47,'2016-11-30 00:00:00'),(10,6,NULL,'1','2016-11-22 00:00:00','noob',48,'2016-12-17 00:00:00');
/*!40000 ALTER TABLE `ResourceRequest` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 15:06:15
