Automated Valet Car Parking System
========================
This application automates a car parking system which provides park rentals for vehicles and manages its revenue.

Each vehicle upon entry can only park in a lot available for that vehicle type. If there are no lots available for that
vehicle type, it should be denied an entry into the space. 

All of the lots are distinctly numbered (eg: CarLot1, CarLot2,...,MotorcycleLot1, MotorcycleLot2,...).

Each vehicle upon entering is alloted to the lot with the lowest number for that vehicle type (eg: a car entering a
parking space with the available lots CarLot2, CarLot4, CarLot5 would be assigned to CarLot2).

When a vehicle wants to exit the car park, the system will release the parking lot which the vehicle rented and charge 
them an appropriate parking fee (rounded up to the nearest hour).

### Implementation
This will be implemented as a console application where it will accept an input file as an argument. The format of the file
is described below:

```
Init motorcycle 3 
Init car 4
Enter motorcycle SGX1234A 1613541902 
Enter car SGF9283P 1613541902
Exit SGX1234A ​1613545602
Enter car SGP2937F 1613546029
Enter car SDW2111W 1613549730
Enter car SSD9281L 1613549740
Exit SDW2111W 1613559745
```

The first two lines initializes a parking space with the indicated number of parking slots for the particular vehicle type.

For each subsequent line, there would be two types of events:

- **Vehicle entering the space: Enter \<motorcycle\|car> \<vehicle number> \<timestamp>**

The program should print out either *accept* or *reject* based on the lot availability. If the vehicle is accepted,
the program should also return the name of the lot being occupied by it.

- **Vehicle exiting the space: Exit \<vehicle number> \<timestamp>**

The program prints out the released lot and the parking fee.

Given the example above, the program output would look like:

```
Accept MotorcycleLot1 
Accept CarLot1 
MotorcycleLot1 2 
Accept CarLot2 
Accept CarLot3 
Reject
CarLot3 6
```

### Scope and Limitations
- The parking system will not keep track of the revenue generated but only calculates the parking fee upon vehicle exiting.
- Vehicle types are hardcoded and limited to only Car and Motorcycle with hourly rate of $2 and $1 respectively.

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

### Deployment Instructions
Please refer to the [deployment guide](https://github.com/mettyoung/railway-routing-service/blob/master/devops/deployment_guide.md)
for further instructions.

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
Copyright © 2021, Emmett Young, All rights reserved.

No reproduction and usage are allowed in whole or in part for distribution, personal and commercial purposes.