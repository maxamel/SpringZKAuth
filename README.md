[![Travis CI](https://travis-ci.org/maxamel/SpringZKAuth.svg)](https://travis-ci.org/maxamel/SpringZKAuth)<br/>
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=com.github.maxamel:SpringZKAuth)](https://sonarcloud.io/api/badges/gate?key=com.github.maxamel:SpringZKAuth)<br/>
[![Code Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.maxamel:SpringZKAuth&metric=coverage)](https://sonarcloud.io/api/badges/measure?key=com.github.maxamel:SpringZKAuth&metric=coverage)<br/>

# SpringZKAuth : A zero-knowledge authentication application

A Spring project utilizing zero-knowledge password proof for secure and private authentication. Users are continuously authenticated throughout their session using changing session IDs.

Status: In development

# Overview

[Zero-knowledge](https://en.wikipedia.org/wiki/Zero-knowledge_proof) is a cryptographic method one can use when required to prove knowledge of a certain secret without revealing anything about the secret itself.
This method is utilized in this project to provide a zero-knowledge password proof (ZKPP) authentication mechanism, meaning proving the knowledge of a password without revealing anything about it. Traditionally, when a user logs into a system, he transmits his user and password over the (possibly encrypted) network. Various security methods exist in order to ensure this password is kept securely, such as hashing, salting, etc. 
This project provides enhanced security in the form of ZKPP, and continuous authentication. Specifically, the password of the user is kept completely secret, and is never transmitted over the wire to the server. The server holds only a hint (i.e. a one-way function) of the password, that is irreversible and no one can deduce the original password from it. Additionally, the session ID of the user is periodically changed on the server side. This session ID is also kept secret and the server only transmits a hint via a message broker (e.g. Kafka) about the new session ID. These hints rely on the [discrete logarithm](https://en.wikipedia.org/wiki/Discrete_logarithm) problem, to ensure only the user can compute the necessary information, and no one else. This makes session hijacking practically useless since the session ID is only valid for a very short period of time (configurable). 

# Usage

The purpose of the project is to provide a POC-level system, and a demonstration of how the concept of zero-knowledge can assist in application security. 
If you want to build a RESTful service which provides enhanced security and privacy through ZKPP and continuous authentication then you can use this project as a starting point.
However, the content to be served by the service is up to you. Currently the guts of the application is just keeping records of users and providing secure, authenticated access to them. You can add your own APIs, DB tables, and all the rest, according to the needs of your own application.


# How does it work?

Let's dive into the nuts and bolts of the cryptographic magic going on behind the scenes.
Firstly, there are two large numbers that are publicly known to everyone. These are the cyclic group generator (g) and a large prime (N). These numbers are carefully picked out, so do not touch them unless you know what you're doing. 
When a user wants to register to the system he provides a password as an input (on client-side only) and this password is hashed to produce a unique representation of the password, call it x. The client program then computes (g^x mod N) and sends it to the server-side. Note that neither the server, nor anyone else listening in on the communication can derive x from (g^x mod N). The server saves that information. When the user wants to start consuming APIs, he inputs the password for the user, and waits for a challenge from the server. The server comes up with a large number y, and challenges the user with (g^y mod N). Now both client and server can compute the solution to the challenge. The client by performing ((g^y)^x mod N) and the server by performing ((g^x)^y mod N). Once the server receives the solution from the client he can verify it against his own and grant access if it is correct. From this point on, this solution serves as the session ID for that user until the server issues a new challenge via a Kafka message broker. These challenges are issued in configurable intervals, and are effective immediately, so all future client requests must contain the new session ID.

Here is an example of a client registering and then making arbitrary requests.

<p align="center">
  <img src="https://github.com/maxamel/SpringZKAuth/blob/master/diagram.png" />
</p>

Note that session ID changing is not described in the diagram, but it is explained further on. Also, the diagram shows a successful path of execution, while there could be a few other unsuccessful alternatives.


# Features

* Authentication using zero-knowledge password proof
* Continuous authentication by publishing challenges to Kafka message broker
* Configurable session inactivity thresholds
* A client-side console application to interact with the system
* No login API, after first password prompt all future authentications are done in the background
* High test coverage
* Automatic scale-up and scale-down of Kafka topics according to active users

# Prerequisites

* Java 8 or above

* NodeJS 5.6.0 or above

* Kafka 0.10.2.1 or above

* Zookeeper 3.4.9 or above

* H2 database

* Gradle 4.3 or above

Note that usually Kafka installation will install Zookeeper as well.

# Installation

Assuming you have the above programs installed, follow the below steps to install:

From the command line install the following modules:
```javascript
npm install big-integer
npm install stack-lifo
npm install kafka-node
npm install no-kafka-slim
```

Open your Kafka server.properties and make sure the following lines are present:
```
auto.create.topics.enable=true
num.partitions=1
listeners=PLAINTEXT://YOUR_KAFKA_IP:9092
zookeeper.connect=YOUR_ZOOKEEPER_IP:2181
delete.topic.enable=true
```

Restart Kafka. 

# Running the Javascript client

The JavaScript client supports three basic commands: register, remove and fetch. Register and remove are the basic endpoints for user management. Anyone can register a user as long as such a user does not exist in the database. Only the registered user can remove himself from the system. 
The third endpoint a user can consume (once authenticated), is the fetch command, which is basically fetching the users' data. 
There is no login command. A user just starts calling the API. If he is not authenticated, he will be required to enter his password, which will be used to solve challenges sent to him during the session. In each command you specify the IP and port of the server and the username.

COMMANDS: 

        REGISTER IP:port username
        REMOVE IP:port username
        FETCH IP:port username
        
A typical flow could be registering a user. Then fetching his data once. After that, as long as the inactivity threshold configured in the server is not reached, you can fetch the user data without any manual reauthentication. 

# License

Published under the MIT License. This basically means the software is free and anyone can use it however they wish. No liability or warranty.

