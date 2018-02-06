# SpringZKAuth : A zero-knowledge authentication scheme

A Spring project utilizing zero-knowledge-password-proof for authentication and also provides changing session IDs (configurable).

Status: In development



# Overview

[Zero-knowledge](https://en.wikipedia.org/wiki/Zero-knowledge_proof) is a cryptographic method one can use when required to prove knowledge of a certain secret without revealing anything about the secret itself.
This method is utilized in this project to provide a zero-knowledge-password-proof (ZKPP) authentication mechanism, meaning proving the knowledge of a password without revealing anything about it. Traditionally, when a user logs into a system, he transmits his user and password over the (possibly encrypted) network. Various security methods exist in order to ensure this password remains a secret, such as hashing, salting, etc. 
This project provides enhanced security in the form of ZKPP, and changing session IDs. Specifically, the password of the user is kept completely secret, and is never transmitted over the wire. Additionally, the session ID of the user is periodically changed on the server side. This session ID is also kept secret and the server only transmits a hint via a message broker (e.g. kafka) about the new session ID. This hint relies on the [discrete logarithm](https://en.wikipedia.org/wiki/Discrete_logarithm) problem, to ensure only the user can compute the new session ID, and no one else. This makes session hijacking practically useless since the sessionID is only valid for a very short period of time (configurable). 
The server can also set inactivity thresholds on the session so that if a user is idle for a certain period of time his session is invalidated and he wioll have to perform the authentication again.

Here is an example of a client registering and then making arbitrary requests.

<p align="center">
  <img src="https://github.com/maxamel/SpringZKAuth/blob/master/diagram.png" />
</p>

Note that session changing is not described in the diagram, but it is explained further on.

# Features

* Authentication using zero-knowledge password proof
* Changing session IDs by publishing challenges to Kafka message broker
* Users are sent challenges on separate Kafka topics which are opened only for them and deleted upon session invalidation

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

# Usage

The purpose of the project is to provide an infrastructure. If you want to build a RESTful service which provides enhanced security and privacy through ZKPP and changing sessionIDs then this project is for you.
However, the content to be served by the service is up to the user. Currently the logic of the application is just keeping records of users and providing secure, authenticated access to them. 

# Running the Javascript client

The JavaScript client supports three basic commands, the minimum required to demonstrate the concepts of ZKPP. Register, remove and fetch. Register and remove are the basic endpoints for user management. Anyone can register a user as long as such a user does not exist in the database. Only the registered user can remove himself from the system. 
The only endpoint a user can consume once authenticated is the fetch command, which is basically fetching the user data. There is no login command. A user just starts consuming the API. If he is not authenticated, he will be required to enter his password, which will be used to solve challenges sent to him during the session. 

COMMANDS: 

        REGISTER IP:port name
        REMOVE IP:port name
        FETCH IP:port name



# License

Published under the MIT License. This basically means the software is free and anyone can use it however they wish. No liability or warranty.

