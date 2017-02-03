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
-- Table structure for table `Resource-Capability`
--

DROP TABLE IF EXISTS `Resource-Capability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Resource-Capability` (
  `ID` int(11) NOT NULL,
  `Capability` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`,`Capability`),
  CONSTRAINT `Capability-Resource` FOREIGN KEY (`ID`) REFERENCES `Resource` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Resource-Capability`
--

LOCK TABLES `Resource-Capability` WRITE;
/*!40000 ALTER TABLE `Resource-Capability` DISABLE KEYS */;
INSERT INTO `Resource-Capability` VALUES (1,'Communication Device'),(1,'GPS'),(1,'Onboard Computer'),(1,'Patrolling'),(1,'Walk'),(2,'Depth Finder'),(2,'Outboard Motor'),(4,'100m Depth'),(5,'Crane Transport'),(7,'Talk'),(13,'Video Recording'),(153,'Mail Delivery');
/*!40000 ALTER TABLE `Resource-Capability` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 15:06:23
