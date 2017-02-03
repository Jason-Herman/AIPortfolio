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
-- Table structure for table `Resource`
--

DROP TABLE IF EXISTS `Resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Resource` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ResourceName` varchar(100) NOT NULL,
  `Model` varchar(100) DEFAULT NULL,
  `Latitude` float NOT NULL,
  `Longitude` float NOT NULL,
  `Cost` float NOT NULL,
  `Status` set('Available','In Use','In Repair') NOT NULL DEFAULT 'Available',
  `UserName` varchar(20) NOT NULL,
  `UniqueNumber` int(11) DEFAULT NULL,
  `CostDescription` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `UserName` (`UserName`),
  KEY `UniqueNumber` (`UniqueNumber`),
  KEY `CostDescription` (`CostDescription`),
  CONSTRAINT `Resource_ibfk_1` FOREIGN KEY (`UserName`) REFERENCES `User` (`Username`),
  CONSTRAINT `Resource_ibfk_2` FOREIGN KEY (`UniqueNumber`) REFERENCES `ESF` (`UniqueNumber`),
  CONSTRAINT `Resource_ibfk_3` FOREIGN KEY (`CostDescription`) REFERENCES `CostOption` (`Description`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Resource`
--

LOCK TABLES `Resource` WRITE;
/*!40000 ALTER TABLE `Resource` DISABLE KEYS */;
INSERT INTO `Resource` VALUES (1,'Walkie Talkie','T213',30.3322,-81.6557,20,'Available','noob',2,'day'),(2,'2015 Hummer','2015 Hummer',33.74,-84.41,200,'Available','FEMA',13,'day'),(3,'Rescue Boat','Yamaha',33.75,-84.39,500,'Available','FEMA',13,'day'),(4,'500 Ton Crane','Barnhart',33.28,-84.45,10000,'Available','mlowe',3,'week'),(5,'Diving Gear','2014',33.75,-84.39,100,'Available','AtlantaCity',9,'day'),(6,'Helicopter','Bell',33.75,-84.39,1000,'Available','AtlantaCity',1,'hour'),(7,'Walkie ','T210',29.2108,-81.0228,10,'Available','msmith',2,'week'),(8,'Telecom Device','T150',33.75,-84.39,5,'Available','FEMA',2,'day'),(9,'Showel','original',30.3119,-81.3965,10,'Available','noob',11,'week'),(11,'Golf Cart','Nissan',28.5383,-81.3792,340,'Available','FBI',3,'week'),(13,'Camera','Nicon',30.1661,-81.7065,80,'Available','noob',5,'hour'),(16,'First Aid Kit','Basic',30.3322,-81.6557,4,'Available','mlowe',6,'hour'),(17,'Band Aids','',30.3322,-81.6557,4,'Available','mlowe',6,'hour'),(18,'Flash Lights','Husky',30.3322,-81.6557,4,'Available','mlowe',9,'hour'),(19,'Emergency generators','TS 05',30.3322,-81.6557,4,'Available','mlowe',12,'hour'),(20,'Boeing Jet','Airplane',33.749,-84.388,5000,'Available','delta',7,'day'),(21,'Boeing  Jet','Airplane',33.749,-84.388,7000,'In Use','delta',7,'day'),(22,'Boeing Jet','Airplane',33.749,-84.388,6000,'Available','delta',7,'day'),(23,'Fingerprints scanner','CR1000',47.7511,-120.74,10,'Available','FBI',9,'hour'),(24,'Fingerprints scanner','CR200',47.7511,-120.74,6,'Available','FBI',9,'hour'),(25,'Emergency consulting',NULL,37.7749,-122.419,20,'Available','msmith',5,'hour'),(26,'Tractor',NULL,37.7749,-122.419,200,'Available','homeDepot',11,'day'),(27,'Trush car','Ford',37.7749,-122.419,100,'Available','homeDepot',11,'day'),(28,'Air hose',NULL,37.7749,-122.419,0,'Available','homeDepot',11,'day'),(29,'Fist Aird Kits',NULL,29.6516,-82.3248,0,'Available','noob',8,'day'),(30,'BLOOD PRESSURE MONITOR',' T240',29.6516,-82.3248,50,'In Use','noob',8,'day'),(31,'BLOOD PRESSURE MONITOR','Sumsung',29.6516,-82.3248,70,'In Use','noob',8,'day'),(32,'BLOOD PRESSURE MONITOR','QARDIO',29.6516,-82.3248,25,'Available','noob',8,'day'),(33,'Tent','2 people',29.6516,-82.3248,100,'Available','noob',6,'day'),(34,'Tent ','4 people',29.6516,-82.3248,30,'Available','homeDepot',6,'day'),(35,'Tent','6 people',29.6516,-82.3248,40,'Available','homeDepot',6,'day'),(36,'Emergency generators','Husky',29.6516,-82.3248,100,'Available','homeDepot',6,'day'),(42,'Emergency housing',NULL,28.5383,-81.3792,100,'Available','FEMA',12,'day'),(43,'Tractor','Hummer',28.5383,-81.3792,200,'Available','FEMA',11,'day'),(44,'Trush Car',NULL,28.5383,-81.3792,100,'Available','FEMA',2,'day'),(45,'emergency workers',NULL,28.5383,-81.3792,1500,'Available','FEMA',5,'week'),(46,'Medical workers',NULL,28.5383,-81.3792,5000,'Available','FEMA',5,'day'),(47,'Semi-truck','null',33.7628,-84.3928,100,'Available','coke',1,'hour'),(48,'Corporate Helicopter','Bell',33.7628,-84.3928,1000,'In Use','coke',1,'hour'),(49,'Corporate Jet','null',33.7628,-84.3928,1000,'Available','coke',1,'hour'),(50,'Walkie Talkies','null',33.7628,-84.3928,100,'Available','coke',2,'day'),(51,'Cherry Picker','null',33.7628,-84.3928,100,'Available','coke',3,'hour'),(52,'3D Printer','null',33.7628,-84.3928,100,'Available','coke',3,'hour'),(54,'SWAT VAN','null',32.5423,-83.0001,100,'Available','GSP',13,'hour'),(55,'SWAT VAN','null',34.1879,-84.9322,100,'Available','GSP',13,'hour'),(56,'Dodge Charger','null',34.1879,-84.9322,100,'Available','GSP',13,'hour'),(57,'Ford Crown Victoria','null',34.1879,-84.9322,75,'Available','GSP',13,'hour'),(58,'Ford Crown Victoria','null',34.292,-83.8977,75,'Available','GSP',13,'hour'),(59,'Backup Power Generators','null',33.7676,-84.5607,150,'Available','AtlantaCity',12,'hour'),(60,'Space Heaters','null',33.7676,-84.5607,75,'Available','AtlantaCity',5,'hour'),(61,'Fire Truck','null',33.7676,-84.5607,5000,'Available','AtlantaCity',4,'week'),(65,'AED','Phillips',33.7676,-84.5607,50,'Available','AtlantaCity',8,'day'),(124,'Garbage Car','Hammer 2000',33.864,-86.224,100,'Available','delta',1,'day'),(145,'Vitals Measurement Unit','M 205',30.1661,-81.7065,35,'Available','noob',8,'day'),(153,'Golf Cart','Nissan',30.24,-81.3853,210,'Available','noob',1,'week'),(154,'Jet','null',33.749,-84.388,3000,'In Repair','delta',1,'day');
/*!40000 ALTER TABLE `Resource` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 15:06:06
