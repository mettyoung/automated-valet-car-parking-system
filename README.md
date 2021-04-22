Automated Valet Car Parking System
========================

This application wishes to automate a car parking system that will accept vehicles and manage its revenue. 

### Architecture
#### C3 - Component Level
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c3-component.puml)

#### C4 - Domain
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-domain.puml)

#### C4 - Application
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-application.puml)

#### C4 - Presentation
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-presentation.puml)

#### Time Sequence Diagram
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-sequence.puml)

### Specification
This application ships with a default vehicle mapping 



Given a gigantic railway network with multiple junction stations, this application wishes to provide a facility to 
suggest routes for the bewildered traveller. Feel free to use this app in your smart phone.

### Specification 
This application ships with a default station map of the Republic of Singapore. Hence, the basic front-end facility is 
crafted specifically for it. It is also capable of creating a new virtual railway network given a different station map
defined in a CSV file. Please take note the following steps depicts how the Railway Factory construct the virtual
railway network:

  1. The CSV file adheres to the following format and creates a list of stations containing the following fields:

    Station Code,Station Name,Opening Date
    NS1,Jurong East,10 March 1990
    NS2,Bukit Batok,10 March 1990
    NS3,Bukit Gombak,10 March 1990
    
  2. The station code strictly adheres two capital letters followed by any number of numerical digits.
  3. The factory sorts the list of stations by station code in alphabetical and numerical order respectively.
  4. The factory creates connections in sequential order.
  5. The factory creates connections if their station names are equal (i.e. they are junction stations).

In the course of computing suggested routes, closed stations are not traversed. 
Please refer to the [station map](https://github.com/mettyoung/railway-routing-service/blob/master/backend/src/main/resources/StationMap.csv)
to determine which stations are closed.

Aside from providing a front-end facility, you may also access the REST API endpoint for route suggestion request:

    curl "http://li1123-50.members.linode.com/railway-routing-service/compute-path?origin=Holland%20Village&target=Bugis"
    # For a more human readable response
    curl "http://li1123-50.members.linode.com/railway-routing-service/compute-path?origin=Holland%20Village&target=Bugis" -H 'Accept: text/plain'

The suggested routes are ordered by the number of stations travelled in ascending order. It also suggests if you
need to change lines. 

### Deployment Instructions
Please refer to the [deployment guide](https://github.com/mettyoung/railway-routing-service/blob/master/devops/deployment_guide.md)
for further instructions.

### Importing a different CSV station map
To import a different CSV station map to an existing containerized web application, please modify the "RAILWAY_CSVPATH"
property in the docker-compose.yml found in the web server and just execute "docker-compose up -d" to recreate the
container with a new station map. Please follow the CSV format strictly as the application will fail to boot if the
parser deems the CSV file unreadable.

### The Journey

The architecture is still similar to the [other project](https://github.com/mettyoung/shop-management) which follows 
the vertical slices as presented [here](http://olivergierke.de/2013/01/whoops-where-did-my-architecture-go/) by Oliver 
Gierke, the Spring Data Project Lead at Pivotal.

Since this project involves more domain logic, I opted for a test-driven development approach for the entire domain 
model. It especially helped me to come up with a decent path finding algorithm as I come up with different test 
scenarios.

Furthermore, I have listed all the technologies that I've used in this project.

#### Back-end Technologies

1. Java
2. JUnit4
3. Hamcrest
4. Spring Boot
5. Spring Boot Test
6. REST
7. Bean Validation 2.0 (JSR-380)

#### Front-end Technologies
1. HTML5
2. CSS3
3. ReactJS
4. React Bootstrap

#### Dev-ops Technologies
1. Git
2. Gradle
3. Docker
4. Ansible

### Copyright
Copyright © 2019, Emmett Young, All rights reserved.

No reproduction and usage are allowed in whole or in part for distribution, personal and commercial purposes.