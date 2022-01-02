#use DatabaseName;

CREATE TABLE Devices(
mob_hash varchar(255) NOT NULL,
primary key(mob_hash)
);

CREATE TABLE Contacts (
    User1_MobileHash varchar(255) NOT NULL,
    User2_MobileHash varchar(255) NOT NULL,
    date_meet int,
    duration int,
    shown int,
    FOREIGN KEY (User1_MobileHash) REFERENCES Devices(mob_hash),
    FOREIGN KEY (User2_MobileHash) REFERENCES Devices(mob_hash),
    PRIMARY KEY(User1_MobileHash,User2_MobileHash,date_meet,duration)
);
CREATE TABLE tests(
TestHash varchar(255) NOT NULL,
mob_hash  varchar(255),
date_test int,
results boolean,
primary key(TestHash),
FOREIGN KEY (mob_hash) REFERENCES Devices(mob_hash)
);

