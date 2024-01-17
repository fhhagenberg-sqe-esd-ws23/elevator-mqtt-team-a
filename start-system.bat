@echo off
start cmd /k java -jar elevator-mqttadapter\target\mqtt-elevator-team-a-mqttadapter-0.0.1-SNAPSHOT-jar-with-dependencies.jar
timeout /t 2 /nobreak
start cmd /k java -jar elevator-algorithm\target\mqtt-elevator-team-a-algorithm-0.0.1-SNAPSHOT-jar-with-dependencies.jar
