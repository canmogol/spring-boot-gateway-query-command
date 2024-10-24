# Applications

There are/will be four Spring Boot applications in this repository; Gateway, Query, Command and Job.

## Gateway

A Spring Boot application that exposes the REST endpoint, handles authentication, authorization, validations and forwarding calls to downstream servers, in this case the Query API and Command API.

## Query API

A Spring Boot application that handles only the query (stateless, GET, maybe some POST for search) requests.

## Command API

A Spring Boot application that handles only the command (state changing, PUT, POST, PATCH and DELETE) requests.

## Job

A Spring Boot application that does not expose any REST endpoints, but handles the scheduled executions, consumes events from queues, writes events to queue and persists data to database.


### Test

You can use the [Gateway.postman_collection.json](postman/Gateway.postman_collection.json) collection to test the gateway.
