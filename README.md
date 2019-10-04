# Simple Smarthome System [![Build Status](https://app.bitrise.io/app/81cc63d1690a1e94/status.svg?token=on4RMefgYNlBU7zLh4zEqQ&branch=master)](https://app.bitrise.io/app/81cc63d1690a1e94)
---
Simple Smarthome System is smarthome monitoring software built with Kotlin, Ktor Server, PostgreSql and Vue.js. It monitors your mqtt broker of choise and shows your sensors status on screen.
The idea behind the project is monitoring system that is not bloated with features, works on top of mqtt and all the settings are avilable in UI. The goal is to have system that easly integrates with Tasmota firmware and zigbee2mqtt devices.

## Getting started
1. install mqtt broker (f.e. Mosquitto)
2. install PostgreSql
3. download SSS binaries from release page
4. extract archive
6. run SSS
```
./smarthome
```
7. open http://0.0.0.0:8080/#/settings and fill up your credentials for PostgreSql and mqtt broker
8. restart server
9. configure sensors at http://0.0.0.0:8080/#/sensors
10. your dashboard is ready at http://0.0.0.0:8080/#/


## Running SSS as a service on Raspberry Pi
1. open file `bin/smarthome.service` 
2. change paths for `ExecStart` and `WorkingDirectory` depending on where you have exctracted distribution zip.
3. copy `smarthome.service` file to systemd dir  
```bash
sudo cp bin/smarthome.service /etc/systemd/system/smarthome.service
```
    
4. run the service: 
```bash
sudo systemctl enable smarthome.service
sudo systemctl start smarthome.service
```
5. if you want to read logfile of the service you can run:
```bash
sudo journalctl -u smarthome.service -f -b
```


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
All contributions are welcome. Please contact me at pawel.bochenski@netguru.pl for futher details or open issues with bugs and suggestions.