DROP TABLE IF EXISTS Events;
DROP TABLE IF EXISTS AuthTokens;
DROP TABLE IF EXISTS Persons;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    Username VARCHAR (255) NOT NULL,
    Password VARCHAR (255) NOT NULL,
    Email VARCHAR (255) NOT NULL,
    FirstName VARCHAR (255) NOT NULL,
    LastName VARCHAR (255) NOT NULL,
    Gender VARCHAR (1) CHECK (Gender == 'f' OR Gender == 'm'),
    PersonID VARCHAR (255),
    UNIQUE (Username, PersonID),
    PRIMARY KEY (Username)
);

CREATE TABLE Persons (
    PersonID VARCHAR (255) NOT NULL,
    AssociatedUsername VARCHAR (255) NOT NULL,
    FirstName VARCHAR (255) NOT NULL,
    LastName VARCHAR (255) NOT NULL,
    Gender VARCHAR (1) CHECK (Gender == 'f' OR Gender == 'm'),
    FatherID VARCHAR (255),
    MotherID VARCHAR (255),
    SpouseID VARCHAR (255),
    UNIQUE(PersonID),
    PRIMARY KEY (PersonID),
    FOREIGN KEY (AssociatedUsername) REFERENCES Users (Username)
);

CREATE TABLE Events (
    EventID VARCHAR(255) NOT NULL,
    AssociatedUsername VARCHAR(255) NOT NULL,
    PersonID VARCHAR (255) NOT NULL,
    Latitude DOUBLE,
    Longitude DOUBLE,
    Country VARCHAR (255),
    City VARCHAR (255),
    EventType VARCHAR (255),
    Year INTEGER,
    UNIQUE (EventID),
    PRIMARY KEY (EventID)
    FOREIGN KEY (PersonID) references Persons (PersonID)
);

CREATE TABLE AuthTokens (
    Token VARCHAR (255) NOT NULL,
    Username VARCHAR (255) NOT NULL,
    PRIMARY KEY (Token),
    FOREIGN KEY (Username) REFERENCES Users (Username)
);