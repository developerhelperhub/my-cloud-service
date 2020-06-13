# My Cloud Service 1.0

This repostiry contains the development of the my cloud service. This project includes reactive based monitor dashboard has been implemented.

## Technology
* Backend 
  * Spring Boot Framework 
    * Netflix Discovery Pattern 
    * Netflix Zuul API Gateway
    * Netflix Circuit Breaker
    * Netflix Loadbalancer
    * Oauth2 Server and Resource
    * MongoDB Repository
    * Actuator
      * JMX 
      * Jolokia
    * JMX 
    * Oauth2 Rest Template
    * Security
      * Basic Security
      * Oauth2 
      * JWT Token
  * Other Framework
    * Jolokia
    * InfluxDB Java Client
    * Lombok
  * Database
    * MongoDB
    * Influxdb
  * Docker
    * Docker Compose
    * Docker Hub
    * Docker 
      * Build
      * Volume
      * Links
  * Maven
    * Module
    * Dependency Management
    * Dockerfile Maven Plugin
  * My Cloud Service 
    * Identity Service
    * Discovery Service
    * Monitor Service
    * Monitor Scheduler Service
    * API Gateway
    * My Cloud Dashboard
* Frontend 
  * React JS
  * Server Sent Event (Event Source)
  * D3 & Morris js chart
  * Bootstrap
  * Awesome Font
  * CSS
  * JQueries
  * HTML


#### In progress .....

## Run Commands

#### Running the services
$ cd /setup
$ docker-compose up

#### Running My Cloud Dashboard
$ cd /my-cloud-dashboard
$ npm start

## Screens
* Login Page
<img src="docs/screenshots/login.png" width="1000" height="600">

* Dashboard Page
<img src="/docs/screenshots/dashboard.png" width="1000" height="600">

* Discovery Dashboard Page
<img src="docs/screenshots/discovery-dashboard.png" width="1000" height="600">

* Monitor Dashboard Page (Info Tab)
<img src="docs/screenshots/monitor-dashboard.png" width="1000" height="600">

* Monitor Dashboard Page (Info Tab - Graph)
<img src="docs/screenshots/monitor-dashboard-info.png" width="1000" height="400">

* Monitor Dashboard Page (Info Instance)
<img src="docs/screenshots/monitor-dashboard-instance.png" width="1000" height="600">

* Monitor Dashboard Page (Info Instance Details)
<img src="docs/screenshots/monitor-dashboard-instance-details.png" width="1000" height="600">

* Identity Client Page
<img src="docs/screenshots/identity-client.png" width="1000" height="600">

* Identity User Page
<img src="docs/screenshots/identity-user.png" width="1000" height="600">

* Sample React Admin UI - Dashboard Page
<img src="docs/screenshots/admin-sample-dashboard.png" width="1000" height="600">

* Sample React Admin UI - Data Table Page
<img src="docs/screenshots/admin-sample-datatable.png" width="1000" height="600">
