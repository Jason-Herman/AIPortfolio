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
-- Table structure for table `Repair`
--

DROP TABLE IF EXISTS `Repair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Repair` (
  `Username` varchar(20) NOT NULL,
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Duration` int(11) NOT NULL,
  `Status` set('0','1','2') NOT NULL DEFAULT '0',
  `DateStarted` datetime DEFAULT NULL,
  `ResourceID` int(11) NOT NULL,
  `DateRecordCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Username`,`ID`),
  KEY `ID` (`ID`),
  KEY `Reapir_Resource_idx` (`ResourceID`),
  CONSTRAINT `Repair_Resource` FOREIGN KEY (`ResourceID`) REFERENCES `Resource` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Repair_User` FOREIGN KEY (`Username`) REFERENCES `User` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Repair`
--

LOCK TABLES `Repair` WRITE;
/*!40000 ALTER TABLE `Repair` DISABLE KEYS */;
INSERT INTO `Repair` VALUES ('delta',1,10,'1','2016-11-22 00:00:00',154,'2016-11-22 17:34:48'),('delta',3,10,'0',NULL,21,'2016-11-22 20:03:32');
/*!40000 ALTER TABLE `Repair` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 15:06:04
