# MAAS Project - PieceOfCake

## Build Status
[![Build Status](https://travis-ci.org/HBRS-MAAS/project-pieceofcake.svg?branch=master)](https://travis-ci.org/HBRS-MAAS/project-pieceofcake)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=project-pieceofcake)](https://sonarcloud.io/dashboard?id=project-pieceofcake)


## Members
* Tim Metzler @[tmetzl](http://github.com/tmetzl)
* Sogol Haghighat @[SogolHaghighat](http://github.com/SogolHaghighat)

## Dependencies
* JADE 4.5.+
* JUnit 4.12
* JSON 20171018
* JavaFX

### JavaFX

For JavaFX you either need to use the Oracle JDK or if you use OpenJDK you need to install openjfx:

    sudo apt-get install openjfx

## Howto run

### Windows

Execute the gradle.bat

    gradlew.bat run
    
It will automatically get the dependencies and start JADE with the configured agents.
In case you want to clean you workspace run

    gradlew.bat eclipse
    
### Linux
Execute the Gradle Wrapper

    ./gradlew run

It will automatically get the dependencies and start JADE with the configured agents.
In case you want to clean you workspace run

    ./gradlew clean

## Eclipse
To use this project with eclipse run

### Windows

    gradle.bat eclipse
    
### Linux

    ./gradlew eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.
