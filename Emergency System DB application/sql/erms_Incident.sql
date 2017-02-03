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
-- Table structure for table `Incident`
--

DROP TABLE IF EXISTS `Incident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Incident` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Latitude` float NOT NULL,
  `Longitude` float NOT NULL,
  `Description` varchar(200) NOT NULL,
  `Username` varchar(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Username` (`Username`),
  CONSTRAINT `Incident_ibfk_1` FOREIGN KEY (`Username`) REFERENCES `User` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Incident`
--

LOCK TABLES `Incident` WRITE;
/*!40000 ALTER TABLE `Incident` DISABLE KEYS */;
INSERT INTO `Incident` VALUES (1,'2016-04-24 00:00:00',33.684,-86.224,'Flash Floods in Fulton County','FultonCounty'),(2,'2015-05-21 00:00:00',33.784,-86.224,'North GA Landslide','GeorgiaState'),(3,'2015-05-21 00:00:00',33.69,-86.25,'Midtown Power Outage','AtlantaCity'),(4,'2016-01-12 00:00:00',33.784,-86.224,'Heavy snow in North GA','GeorgiaState'),(5,'2015-05-21 00:00:00',33.69,-86.25,'Midtown Building Collapse','AtlantaCity'),(6,'2016-11-09 04:08:28',29.9012,-81.3124,'St. Augustine Flood','noob'),(7,'2016-11-29 00:00:00',22,-35,'Road Block','msmith'),(8,'2016-11-29 00:00:00',22,-35,'Broken Bridge','mlowe'),(9,'2016-11-29 00:00:00',22,-35,'Fallen Tree','mlowe'),(10,'2016-11-18 00:00:00',23,-23,'Fallen Pier','mlowe'),(11,'2016-11-15 00:00:00',33,-25,'St. Elmo Fire','mlowe'),(12,'2016-11-18 00:00:00',33,-30,'Zoobreak','mlowe'),(13,'2016-11-18 00:00:00',25,-40,'Avalanche at Cooper','delta'),(14,'2016-11-19 00:00:00',35,-15,'Break in at Sooner Bank','mlowe'),(93,'2016-11-21 00:00:00',30.3344,-81.3987,'Water pipe broke','coke'),(94,'2016-11-21 00:00:00',34.237,-84.9441,'Airplane emergency landing','delta'),(95,'2016-11-24 00:00:00',30.5622,-81.8307,'Electricity shortage','coke'),(96,'2016-11-22 00:00:00',30.5622,-81.8307,'Parking Lot Accident','coke');
/*!40000 ALTER TABLE `Incident` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 15:06:19
