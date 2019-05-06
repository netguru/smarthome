# Simple Smarthome System
---
Simple Smarthome System is smarthome monitoring software built with Kotlin, Ktor Server, PostgreSql and Vue.js. It monitors your mqtt broker of choise and shows your sensors status on screen.
The idea behind the project is monitoring system that is not bloated with features, works on top of mqtt and all the settings are avilable in UI. The goal is to have system that easly integrates with Tasmota firmware and zigbee2mqtt devices.

## Getting started
1. install mqtt broker (f.e. Mosquitto)
2. install PostgreSql
3. download SSS binaries from release page
4. extract archive
5. create empty file `config.json` in bin folder
6. run SSS
    ```
    ./smarthome
    ```
7. open http://0.0.0.0:8080/#/settings and fill up your credentials for PostgreSql and mqtt broker
8. restart server
9. configure sensors at http://0.0.0.0:8080/#/sensors
10. your dashboard is ready at http://0.0.0.0:8080/#/

## Roadmap
System is in its early stages so it contains bugs and some features are missig.
The roadmap of the project contains:
 - Creating DockerFile for easy deployment
 - Support for sending messages other than boolean values
 - Creating groups of sensors and showing them groupped on dashboard
 - Some simple rules system
 - changing settings without restart of server
 - scripts that install SSS as service on Raspberry Pi

## Contribution
All contributions are welcome. Please contact me at pawel.bochenski@netguru.pl for futher details.