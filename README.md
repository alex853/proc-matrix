# ProcMatrix project

## How to start the services

Prerequisites and notes:
 * The project runs on Java 17
 * The project expects JAVA_HOME environment variable to be set to appropriate JRE
 * The project is built using Gradle, it comes with Gradle Wrapper in the project
 * Start scripts are written in Windows '.bat' files form, there are '.sh' or similar scripts prepared

There are following components provided and their starting scripts:
 * Storage service - Use 'start-storage-service.bat' command file to start it. 
 * Computations service - Use 'start-computations-service.bat' command file to start it.
 * Set of integration tests - Use 'start-integration-tests.bat' command file. It runs all 
   integration tests. One of them is really heavy on memory (see Performance notes below).
 * Minimalistic 'Hello World' app which calculates a sum of two matrices with 3 x 3 dimensions - Use 'start-hello-world-app.bat' command file.

Performance notes:
 * Storage service and Computations service uses 8GB of heap memory
 * Integration tests require 14GB of heap memory
 * Please take these figures in mind before starting the project
