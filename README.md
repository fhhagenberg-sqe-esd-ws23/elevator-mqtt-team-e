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

