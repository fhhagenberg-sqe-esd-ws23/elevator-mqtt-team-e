# Instructions for Downloading and Installing the Elevator Controller

## Step 1: Download the package

   1. Navigate to the "Releases" section of the GitHub repository.
   2. Download the package in the latest release, which contains the .jar file of the Elevator Controller (e.g., ElevatorController_Team_E-1.0.0.jar).

## Step 2: Unpacking the archive

   1. Once the download is complete, locate the downloaded file on your computer.
   2. Extract the contents of the archive, if it's in a compressed format, to a folder of your choice.

## Step 3: Running the Elevator Controller

   1. Before launching the Elevator Controller, ensure that the Elevator Simulator is already started and running.
   2. Open a command console or terminal window.
   3. Navigate to the folder where you extracted the .jar file.
   4. Run the .jar file by executing the command: ```java -jar .\ElevatorController_Team_E-1.0.0.jar```
   5. If you want to stop the Elevator Controller press ```Ctrl + C```.

This command starts the Elevator Controller application. Ensure that Java is installed on your system and the path to java is correctly set up in your environment variables for the command to work properly.
By following these steps, you should have the Elevator Controller up and running, interfacing with the Elevator Simulator as expected.

___

# Test Concept for Elevator Controller

## Overview:
Our test concept for the Elevator Controller is designed to ensure high quality and reliability of the system. We employ a combination of Whitebox Testing and Mutation Testing to comprehensively assess our code.

## Whitebox Testing with JUnit:
Whitebox Testing, also known as structural testing, involves testing the internal structures or workings of the Elevator Controller. By using JUnit, a popular unit testing framework for Java, we are able to create and execute tests that cover various scenarios and edge cases. This approach allows us to verify the functionality of individual units of code in a controlled environment.

## Mocking Dependencies with Mockito:
To isolate the units of code during testing, we use Mockito, a mocking framework for Java. Mockito enables us to create mock objects for the Elevator Controller's dependencies. This is crucial for unit testing as it allows us to test each part of the code in isolation from others, ensuring that tests are not affected by external factors such as database connections or network access.

## Mutation Testing:
In addition to Whitebox Testing, we implement Mutation Testing to further evaluate the quality and effectiveness of our unit tests. Mutation Testing involves making small changes (mutations) to the code and then running the tests to see if they detect and fail due to these mutations. This method helps us identify weaknesses in our test suite, ensuring that our tests are robust and capable of catching potential bugs.

## Conclusion:
The combination of detailed Whitebox Testing, dependency isolation through Mockito, and the rigorous assessment provided by Mutation Testing forms a comprehensive and effective test strategy for our Elevator Controller. This approach ensures that our system is not only functioning as intended but is also resilient against future changes and potential errors.

***

# Elevator Control System MQTT Topics

This document provides an overview and description of the MQTT topics used by the Elevator Control System. The system is designed to communicate various aspects of the elevator's status and control commands through these topics.

## MQTT Topic Structure

The MQTT topics follow a structured format: `ElevatorController/{elevator_id}/{attribute}`

Where:
- `{elevator_id}` is a unique identifier for each elevator.
- `{attribute}` represents a specific attribute or control command for the elevator.

## MQTT Topics and Descriptions

### Status Topics

These topics provide information about the current status of each elevator.

1. **ElevatorController/{elevator_id}/floorNum**  
   - **Description**: Reports the current floor number on which the elevator is located.
   - **Payload**: An integer representing the floor number.

2. **ElevatorController/{elevator_id}/position**  
   - **Description**: Provides the current position of the elevator in terms of its physical location in the elevator shaft.
   - **Payload**: An integer indicating the position.

3. **ElevatorController/{elevator_id}/target**  
   - **Description**: Indicates the target floor towards which the elevator is currently moving.
   - **Payload**: An integer representing the target floor number.

4. **ElevatorController/{elevator_id}/committed_direction**  
   - **Description**: Shows the current committed direction of the elevator (up, down, or no movement).
   - **Payload**: An integer where specific values denote direction (e.g., 0 for no movement, 1 for up, 2 for down).

5. **ElevatorController/{elevator_id}/door_status**  
   - **Description**: Provides the current status of the elevator doors (open, closed, opening, closing).
   - **Payload**: An integer indicating the door status.

6. **ElevatorController/{elevator_id}/speed**  
   - **Description**: Reports the current speed of the elevator.
   - **Payload**: An integer indicating the speed of the elevator.

7. **ElevatorController/{elevator_id}/acceleration**  
   - **Description**: Provides information about the current acceleration of the elevator.
   - **Payload**: An integer indicating the acceleration rate.

8. **ElevatorController/{elevator_id}/capacity**  
   - **Description**: Indicates the maximum capacity of the elevator.
   - **Payload**: An integer representing the capacity in terms of number of individuals.

9. **ElevatorController/{elevator_id}/weight**  
   - **Description**: Reports the current weight detected inside the elevator.
   - **Payload**: An integer indicating the weight in kilograms.

### Control Topics

These topics are used to send control commands to the elevators.

1. **ElevatorController/{elevator_id}/set_target**  
   - **Description**: Command to set a new target floor for the elevator.
   - **Payload**: An integer representing the desired target floor number.

2. **ElevatorController/{elevator_id}/set_direction**  
   - **Description**: Command to change the committed direction of the elevator.
   - **Payload**: An integer indicating the new direction (e.g., 0 for no movement, 1 for up, 2 for down).

