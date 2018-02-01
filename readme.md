# SpringZKAuth : A zero-knowledge authentication mechanism

A Spring project utilizing zero-knowledge-password-proof authentication with changing session IDs.
(In development)

# Overview

[Zero-knowledge](https://en.wikipedia.org/wiki/Zero-knowledge_proof) is a cryptographic method one can use when required to prove knowledge of a certain secret without revealing anything about the secret itself.
This method is utilized in this project to provide a zero-knowledge-password-proof (ZKPP) authentication mechanism, meaning proving the knowledge of a password without revealing anything about it. Traditionally, when a user logs into a system, he transmits his user and password over the (possibly encrypted) network. Various security methods exist in order to ensure this password remains a secret, such as hashing, salting, etc. 
This project provides enhanced security in the form of ZKPP, and changing session IDs. Specifically, the password of the user is kept completely secret, and is never transmitted over the wire. Additionally, the session ID of the user is periodically changed on the server side. This session ID is also kept secret and the server only transmits a hint via a message broker (e.g. kafka) about the new session ID. This hint utilizes the [discrete logarithm](https://en.wikipedia.org/wiki/Discrete_logarithm) problem, to ensure only the user can compute the new session ID, and no one else. 

# Prerequisites

Java 8

NodeJS

Kafka

H2 database

Gradle

# Installation

Assuming you have the above programs installed, follow the below steps to install:

```javascript
npm install big-integer
npm install stack-lifo
npm install kafka-node
npm install no-kafka-slim
```

# Running the Javascript client

COMMANDS: 

        REGISTER IP:port name
        REMOVE IP:port name
        FETCH IP:port name

