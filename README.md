# Island Reservations Service

The Island Reservations Service is a REST API service that manages the campsite reservations on a newly formed island in the Pacific Ocean.

## Features

- Check the availability of the campsite for a given date range.
- Make a reservation with a maximum of 3 days.
- Make a reservation with a minimum of 1 day ahead of arrival and up to 1 month in advance.
- Cancel a reservation.
- Modify an existing reservation.
- Graceful handling of concurrent requests for reservations.
- Provide appropriate error messages for error cases.

## Tech Stack

- Java
- Spring Boot
- Kafka
- Docker
- JUnit and JMeter

## Running the Application

### Prerequisites

- Docker and Docker Compose

### Steps

1. Navigate to the `reservation-service/docker/kafka/Dockerfile` directory.

2. Run the Docker Compose file for Kafka:

3. Build and start the application:


## API Documentation

### Check Campsite Availability

**Endpoint:** `/api/v1/island-reservations/availability`
**Method:** `GET`
**Parameters:** `startDate`, `endDate`

### Make a Reservation

**Endpoint:** `/api/v1/island-reservations/reservation`
**Method:** `POST`
**Payload:** `fullName`, `email`, `startDate`, `endDate`

### Cancel a Reservation

**Endpoint:** `/api/v1/island-reservations/{reservationId}/cancel`
**Method:** `POST`
**Parameters:** `reservationId`

### Modify a Reservation

**Endpoint:** `/api/v1/island-reservations/{reservationId}`
**Method:** `PUT`
**Parameters:** `reservationId`
**Payload:** `fullName`, `email`, `startDate`, `endDate`

## Architecture

The basic flow of the application is Controller -> Service -> Database -> Kafka.

### Concurrency Handling

The system uses kafka and optimistic locking to handle concurrent requests for reservations.


### Kafka

Kafka is used for handling reservation events and ensuring the system can scale to handle a large volume of requests.

## Testing

The project includes unit tests, integration tests, and concurrency tests to ensure the system behaves as expected under various scenarios.

## Concurrency Testing with JMeter

In this project, we've integrated a JMeter test plan to demonstrate how the system manages concurrency.

### Prerequisites

Make sure you have Docker and Docker Compose installed on your system.

### Instructions

1. **Start Kafka with Docker Compose:**
   Navigate to the root directory of the project and run the following command:
   ```bash
   docker-compose -f docker/kafka/Dockerfile/kafka-docker-compose.yml up -d

This command starts a Kafka instance using the provided Docker Compose configuration.

Run JMeter Tests:
In the root of the project, execute the following command:
```
./gradlew jmRun
```

This command runs the integrated JMeter tests with Gradle. Check the output to view the test results.
By following these steps, you can run JMeter tests and observe how the system handles concurrent requests.



## License

[MIT License](LICENSE)

 

