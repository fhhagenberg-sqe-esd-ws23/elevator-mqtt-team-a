# sqelevator-proj
SQElevator MQTT Group A

## Requirements
- Java Runtime (version 17 or later)
- MQTT broker (a local one works best)

## How to run the system
1. Download the 4 artifacts from the latest release and put them in the same directory
   - elevator.properties
   - mqtt-elevator-team-a-algorithm-0.0.1-SNAPSHOT-jar-with-dependencies.jar
   - mqtt-elevator-team-a-mqttadapter-0.0.1-SNAPSHOT-jar-with-dependencies.jar
   - start-system.bat
2. Make changes to the configuration file (elevator.properties) if necessary. Options and defaults:
   - rmi_address=localhost (host of RMI registry with elevator simulator)
   - rmi_port=1099 (port of RMI registry with elevator simulator)
   - rmi_name=ElevatorSim (name of IElevator object in the RMI registry)
   - mqtt_address=localhost (host of the mqtt broker)
   - mqtt_port=1883 (port of the mqtt broker)
   - polling_interval=250 (time between polls to the RMI interface and mqtt messages sent by the mqtt adapter)
   - exit_line=exit (command line input for stopping the program(s))
4. Start the elevator simulator and create a new scenario (so that the mqtt adapter has something to connect to)
5. Start the script-file start-system.bat
6. Wait for 2 seconds until all two console windows show output
7. Play the elevator simulator scenario

## Mqtt topics for 2 elevators and 3 floors:
- building/connected
- building/elevator/count
- building/elevator/0/direction
- building/elevator/0/setdirection
- building/elevator/0/acceleration 
- building/elevator/0/button/0 
- building/elevator/0/button/1
- building/elevator/0/button/2
- building/elevator/0/capacity 
- building/elevator/0/doors
- building/elevator/0/floor 
- building/elevator/0/position 
- building/elevator/0/speed 
- building/elevator/0/weight 
- building/elevator/0/servicesfloor/0 
- building/elevator/0/servicesfloor/1
- building/elevator/0/servicesfloor/2
- building/elevator/0/setservicesfloor/0 
- building/elevator/0/setservicesfloor/1
- building/elevator/0/setservicesfloor/2 
- building/elevator/0/target
- building/elevator/0/settarget
- building/elevator/1/direction
- building/elevator/1/setdirection
- building/elevator/1/acceleration 
- building/elevator/1/button/0 
- building/elevator/1/button/1
- building/elevator/1/button/2
- building/elevator/1/capacity 
- building/elevator/1/doors
- building/elevator/1/floor 
- building/elevator/1/position 
- building/elevator/1/speed 
- building/elevator/1/weight 
- building/elevator/1/servicesfloor/0 
- building/elevator/1/servicesfloor/1
- building/elevator/1/servicesfloor/2
- building/elevator/1/setservicesfloor/0 
- building/elevator/1/setservicesfloor/1
- building/elevator/1/setservicesfloor/2 
- building/elevator/1/target
- building/elevator/1/settarget
- building/floor/count
- building/floor/height
- building/floor/0/down
- building/floor/0/up 
- building/floor/1/down
- building/floor/1/up
- building/floor/2/down
- building/floor/2/up
