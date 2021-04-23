Automated Valet Car Parking System
========================
This application automates a car parking system that provides parking lot rentals for vehicles and manages its revenue.

Each vehicle upon entry can only park in a lot available for that vehicle type. If there are no lots available for that
vehicle type, it should be denied entry into space. 

All the lots are distinctly numbered (eg: CarLot1, CarLot2,...,MotorcycleLot1, MotorcycleLot2,...).

Each vehicle upon entering is allocated to the lot with the lowest number for that vehicle type (eg: a car entering a
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
Exit SGX1234A 1613545602
Enter car SGP2937F 1613546029
Enter car SDW2111W 1613549730
Enter car SSD9281L 1613549740
Exit SDW2111W 1613559745
```

The first two lines initialize a parking space with the indicated number of parking slots for the particular vehicle type.

For each subsequent line, there would be two types of events:

- **Vehicle entering the space: Enter \<motorcycle\|car> \<vehicle number> \<timestamp>**

The program should print out either *accept* or *reject* based on the lot availability. If the vehicle is accepted,
the program should also return the name of the lot being occupied by it.

- **Vehicle exiting the space: Exit \<vehicle number> \<timestamp>**

The program prints out the released lot and the parking fee.

Given the example above, the program output would look like this:

```
Allocated 3 lots for Motorcycle
Allocated 4 lots for Car
Accepted at 2021-02-17T14:05:02: MotorcycleLot1
Accepted at 2021-02-17T14:05:02: CarLot1
MotorcycleLot1 released at 2021-02-17T15:06:42: $2.00 charged for $1.00/hour
Accepted at 2021-02-17T15:13:49: CarLot2
Accepted at 2021-02-17T16:15:30: CarLot3
Accepted at 2021-02-17T16:15:40: CarLot4
CarLot3 released at 2021-02-17T19:02:25: $6.00 charged for $2.00/hour
```

### Scope and Limitations
- The parking system will not keep track of the revenue generated but only calculates the parking fee upon vehicle exit.
- Vehicle types are hardcoded and limited to only Car and Motorcycle with an hourly rate of $2 and $1 respectively.

### How to run the application (Ubuntu 16.04)
Execute the following in your terminal:
```
sudo apt-get install -y zip
cd
wget https://github.com/mettyoung/automated-valet-car-parking-system/archive/refs/heads/master.zip
unzip master.zip
cp automated-valet-car-parking-system-master/app/install-jdk11.sh install-jdk11.sh
./install-jdk11.sh
source ~/.bashrc
java -version
cd automated-valet-car-parking-system-master/app
./mvnw clean package
cd target
java -jar automated-valet-car-parking-system.jar ../src/main/resources/input-sample.txt
```

### Architecture
By adopting [clean architecture](https://blog.ndepend.com/introduction-clean-architecture/), we achieve higher flexibility. 
We can think of it as a **ports and adapters architecture** where at the heart of the software lies the application core 
which contains both our use cases and domain model. The application core exposes both input and output ports which can support 
different adapters for different purposes.

For example, we can plug both REST and MVC adapters into the application core to serve both types of applications. We can even
change the database vendor from SQL to NoSQL in order to scale easier. We can also improve our reads by denormalizing 
our persistence entities as needed. All without impacting our application core because clean architecture enables us to write 
loosely coupled codebase.

Now, let's begin with a high-level component diagram of our architecture.

#### C3 - Component Level
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c3-component.puml)

According to the diagram, both presentation and persistence layers point inward to the application core. 

Even though the natural flow of control between the application and persistence layer is towards the latter, 
we can still invert the flow of dependency by **dependency inversion** using delegates. This is accomplished
by defining an interface of the repository in the domain layer and letting the persistence layer provide an
implementation for it and injecting it into the IoC container.

Since this is only a prototype application, I will only provide the **InMemoryVehicleTypeRepository** which hardcodes
vehicles limited to cars and motorcycles. 

If you wish to add more vehicles, you can add them to the **InMemoryVehicleTypeRepository** at compile-time and 
the application will automatically support those.

If vehicle types will be frequently updated, I suggest implementing **JpaVehicleTypeRepository**.
By doing so, we can just modify the vehicle types in the database without recompiling the application. 

#### C4 - Domain
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-domain.puml)

This layer defines the business logic for the parking system using an object-oriented approach. 

*ParkingSpace* accepts *VehicleLots* to dynamically allocate parking lots per vehicle type. *VehicleTypeRepository* 
enables adding more vehicle types without modifying the domain model.

The rest are self-explanatory.
  
#### C4 - Application
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-application.puml)

We define two use cases - enter and exit vehicle. These commands delegate success and failure handlers
to the presentation layer. This enables the presentation layer to properly define its display logic according to its medium.

*ParkingSpace* is designed to be injectable which enables these commands to work with multiple parking spaces.
But it will depend on the presentation layer if it will support single or multiple parking spaces. 

#### C4 - Presentation
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-presentation.puml)

We use [Chain of Responsibility](https://refactoring.guru/design-patterns/chain-of-responsibility) pattern to create the *ConsoleInputInterpreter*. 
It can accept multiple interpreters where the appropriate interpreter will read a line from STDIN and execute the corresponding application command.

If we want to support more commands, we can just create a new interpreter and add it to the *ConsoleInputInterpreter*. This follows the 
[Open-Closed Principle](https://stackify.com/solid-design-open-closed-principle/) from SOLID design principles.

Since the ConsoleUI is only meant for being a prototype, it has been designed under the following assumptions:

- Vehicle types are defined during compile time. This means it will not support *JpaVehicleTypeRepository* out of the box.
- Only one parking space will be used - which is accomplished by [Registry](https://martinfowler.com/eaaCatalog/registry.html)
and [Singleton](https://refactoring.guru/design-patterns/singleton) patterns.

*DefaultInterpreter* will be invoked if the input command from STDIN is not recognized.

#### Time Sequence Diagram
![](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mettyoung/automated-valet-car-parking-system/master/docs/c4-sequence.puml)

### The Journey
I opted for a test-driven development approach for the domain layer. Since the presentation and persistence adapters are
just prototypal implementation, I opted out writing tests for them as I do not see the need.

I have also listed the tech stack I've used in this project.

#### Tech Stack
1. Java
2. Maven
3. Spock Framework

### Copyright
Copyright © 2021, Emmett Young, All rights reserved.

No reproduction and usage is allowed in whole or in part for distribution, personal and commercial purposes.