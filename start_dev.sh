#!/bin/bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
