CREATE DATABASE IF NOT EXISTS erms;
USE erms;


DROP TABLE IF EXISTS Municipality; 
DROP TABLE IF EXISTS Company; 
DROP TABLE IF EXISTS GovernmentAgency; 
DROP TABLE IF EXISTS Individual; 
DROP TABLE IF EXISTS `Repair`; 
DROP TABLE IF EXISTS HasAdditional; 
DROP TABLE IF EXISTS `Resource-Capability`; 
DROP TABLE IF EXISTS ResourceRequest; 
DROP TABLE IF EXISTS Incident; 
DROP TABLE IF EXISTS Resource; 
DROP TABLE IF EXISTS ESF; 
DROP TABLE IF EXISTS CostOption; 
DROP TABLE IF EXISTS `User`; 


CREATE TABLE IF NOT EXISTS `User`(
		Username varchar(20) NOT NULL, 
		Name varchar(36) NOT NULL, 
		Password varchar(20) NOT NULL, 
		PRIMARY KEY (Username)
	) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS Municipality (
		Username varchar(20) NOT NULL, 
		PopulationSize int(11) NOT NULL, 
		PRIMARY KEY (Username), 
		FOREIGN KEY (Username) REFERENCES User (Username)
	) ENGINE = InnoDB; 


CREATE TABLE IF NOT EXISTS Company(
		Username varchar(20) NOT NULL, 
		Location varchar(36) NOT NULL, 
		PRIMARY KEY (Username), 
		FOREIGN KEY (Username) REFERENCES User (Username)
	) ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS GovernmentAgency(
		Username varchar(20) NOT NULL, 
		Jurisdiction varchar(20) NOT NULL, 
		PRIMARY KEY (Username), 
		FOREIGN KEY (Username) REFERENCES User (Username)
	) ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS Individual(
		Username varchar(20) NOT NULL, 
		JobTitle varchar(36) NOT NULL, 
		DateHired datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, 
		PRIMARY KEY (Username), 
		FOREIGN KEY (Username) REFERENCES User (Username)
	) ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS ESF(
		UniqueNumber int NOT NULL AUTO_INCREMENT, 
		Description varchar(200), 
		PRIMARY KEY (UniqueNumber)
	) ENGINE = INNODB;


 CREATE TABLE IF NOT EXISTS CostOption( 
		Description varchar(200) NOT NULL, 
		TimePeriod TIMESTAMP NOT NULL, 
		PRIMARY KEY (Description)
	) ENGINE = INNODB; 
    
ALTER TABLE ESF AUTO_INCREMENT = 1;


CREATE TABLE IF NOT EXISTS Resource(
		ID int NOT NULL, 
		ResourceName varchar(100) NOT NULL, 
		Model varchar(100) NULL, 
		Latitude float NOT NULL, 
		Longitude float NOT NULL, 
		Cost float NOT NULL, 
		Status set('Available', 'In Use', 'In Repair') NOT NULL DEFAULT 'Available', 
		UserName varchar(20) NOT NULL, 
		UniqueNumber int NULL, 
		CostDescription varchar(200) NOT NULL, 
		PRIMARY KEY (ID), 
		FOREIGN KEY (Username) REFERENCES User (Username), 
		FOREIGN KEY (UniqueNumber) REFERENCES ESF (UniqueNumber), 
		FOREIGN KEY (CostDescription) REFERENCES CostOption(Description)
	) ENGINE = INNODB; 




CREATE TABLE IF NOT EXISTS Repair(
		Username varchar(20) NOT NULL, 
		ID int NOT NULL AUTO_INCREMENT, 
		Duration int NOT NULL, 
		Status  set('0', '1') NOT NULL DEFAULT '0', 
		DateStarted datetime NULL, 
		PRIMARY KEY (Username, ID), 
		FOREIGN KEY (Username) REFERENCES User (Username), 
		FOREIGN KEY (ID) REFERENCES Resource (ID)
	) ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS `Resource-Capability`(
		ID int NOT NULL, 
		Capability varchar(200), 
		PRIMARY KEY (ID, Capability), 
		FOREIGN KEY (ID) REFERENCES Resource (ID)
	)ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS HasAdditional(
		ID int NOT NULL, 
		UniqueNumber int NOT NULL, 
		PRIMARY KEY (ID, UniqueNumber), 
		FOREIGN KEY (ID) REFERENCES Resource (ID), 
		FOREIGN KEY (UniqueNumber) REFERENCES ESF (UniqueNumber)
	) ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS Incident(
		ID int NOT NULL AUTO_INCREMENT, 
		Date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, 
		Latitude float NOT NULL, 
		Longitude float NOT NULL, 
		Description varchar(200) NOT NULL, 
		Username varchar(20) NOT NULL, 
		PRIMARY KEY (ID), 
		FOREIGN KEY (Username) REFERENCES User (Username)
	) ENGINE = INNODB; 


CREATE TABLE IF NOT EXISTS ResourceRequest(
		ID int NOT NULL AUTO_INCREMENT, 
		IncidentID int NOT NULL, 
		ReturnDate datetime NOT NULL, 
		Status set('0', '1') NOT NULL DEFAULT '0',
		StartDate datetime NULL, 
		PRIMARY KEY (ID, IncidentID), 
		FOREIGN KEY (ID) REFERENCES Resource (ID), 
		FOREIGN KEY (IncidentID) REFERENCES Incident (ID)
	) ENGINE = INNODB;
	
		-- Dumping data for table 'user'
INSERT INTO `user`(`Username`, `Name`, `Password`) VALUES ('user1','John Doe','password')
    
