[![Travis CI](https://travis-ci.org/maxamel/SpringZKAuth.svg)](https://travis-ci.org/maxamel/SpringZKAuth)<br/>
[![Code Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.maxamel:SpringZKAuth&metric=coverage)](https://sonarcloud.io/api/badges/measure?key=com.github.maxamel:SpringZKAuth&metric=coverage)<br/>

[![Quality Gate](https://sonarcloud.io/api/project_badges/quality_gate?project=com.github.maxamel:SpringZKAuth)](https://sonarcloud.io/api/project_badges/quality_gate?project=com.github.maxamel:SpringZKAuth)<br/>

# SpringZKAuth : A zero-knowledge authentication application

A Spring project utilizing zero-knowledge password proof for secure and private authentication. Users are continuously authenticated throughout their session using rotating session IDs.

Status: In development

# Overview

[Zero-knowledge](https://en.wikipedia.org/wiki/Zero-knowledge_proof) is a cryptographic method one can use when required to prove knowledge of a certain secret without revealing anything about the secret itself.
This method is utilized in this project to provide a zero-knowledge password proof (ZKPP) authentication mechanism, meaning proving the knowledge of a password without revealing anything about it. Traditionally, when a user logs into a system, he transmits his user and password over the (possibly encrypted) network. Various security methods exist in order to ensure this password is stored securely, such as hashing, salting, etc. 
This project eliminates the need for any of these methods. Specifically, the password of the user is kept completely secret, and is never transmitted over the wire to the server. The server receives only a cryptographic representation (i.e., a one-way function) of the password, that is irreversible and no one can deduce the original password from it. Additionally, the session ID of the user is periodically rotated on the server side (optional). This session ID is also kept secret and the server only transmits a hint via a message broker (e.g., Kafka) about the new session ID. These hints rely on the [discrete logarithm](https://en.wikipedia.org/wiki/Discrete_logarithm) problem, to ensure only the user can compute the necessary information, and no one else. This makes session hijacking practically useless since the session ID is only valid for a very short period of time (configurable). 

# Usage

The purpose of the project is to provide a POC-level system, and a demonstration of how the concept of zero-knowledge can assist in application security. 
If you want to build a RESTful service which provides enhanced security and privacy through ZKPP and continuous authentication, then you can use this project as a starting point.
However, the content to be served by the service is up to you. Currently the guts of the application is just keeping diary entries of users and providing secure, authenticated access to them. You can add your own APIs, DB tables, and all the rest, according to the needs of your own application.


# How does it work?

Let's dive into the nuts and bolts of the cryptographic magic going on behind the scenes.
Firstly, there are two large numbers that are publicly known to everyone. These are the cyclic group generator (g) and a large prime (N). These numbers are carefully picked out, so do not touch them unless you know what you're doing. 
When a user wants to register to the system he provides a password as an input (on client-side only) and this password is hashed to produce a unique representation of the password, call it x. The client program then computes (g^x mod N) and sends it to the server-side. Note that neither the server, nor anyone else listening in on the communication can derive x from (g^x mod N). The server saves that information. When the user wants to start consuming APIs, he inputs the password for the user, and waits for a challenge from the server. The server comes up with a large number y, and challenges the user with (g^y mod N). Now both client and server can compute the solution to the challenge. The client by performing ((g^y)^x mod N) and the server by performing ((g^x)^y mod N). Once the server receives the solution from the client he can verify it against his own and grant access if it is correct. From this point on, this solution serves as the session ID for that user. Optionally, you can configure the server to issue a new challenge via a Kafka message broker. These challenges are issued in configurable intervals, and are effective immediately, so all future client requests must contain the new session ID.

Here is an example of a client registering and then making arbitrary requests.

<p align="center">
  <img src="https://github.com/maxamel/SpringZKAuth/blob/master/diagram.png" />
</p>

Note that session ID rotating is not described in the diagram, but it is explained further on. 


# Features

* Register and remove users
* Write/Edit/Remove diaries per user
* Authentication using zero-knowledge password proof
* Continuous authentication by publishing challenges to Kafka message broker (Optional)
* Configurable session inactivity thresholds
* A client-side GUI application to interact with the system
* No login API, after first password prompt all future authentications are done in the background
* High test coverage
* Automatic scale-up and scale-down of Kafka topics according to active users

# Prerequisites

* Java 8 or above

* NodeJS 5.6.0 or above

* Confluent Open Source Platform 2.11 (Optional)

* H2 database

* Gradle 4.3 or above


# Installation

Assuming you have the above programs installed, follow the below steps to install:

Clone and build the repository.
```
git clone https://github.com/maxamel/SpringZKAuth.git
cd SpringZKAuth
gradle clean build
```

From the command line install the following modules:
```javascript
npm install big-integer

npm install jquery
npm install -g browserify
npm install alertify
```

Go to src/SecureDiary/js and run:
```
browserify script.js -o bundle.js
```

If you don't want to use Kafka (for continuous authentication) you need to disable it from src/main/resources/application.yml:
```
kafka:
  enabled: false
```

If you want the continuous authentication feature follow the below steps to install [Confluent Platform](https://docs.confluent.io/current/installation/) which comes bundled with Kafka, Zookeeper and a bunch of other useful software. Here are instructions for Ubuntu or CentOS installations:

## Ubuntu: 
Add repository and key:
```
wget -qO - https://packages.confluent.io/deb/4.0/archive.key | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://packages.confluent.io/deb/4.0 stable main"
```
Install and run:
```
sudo apt-get update && sudo apt-get install confluent-platform-oss-2.11
confluent start
```

## CentOS:
Add Package:
```
sudo rpm --import https://packages.confluent.io/rpm/4.0/archive.key
```

Add file named confluent.repo to /etc/yum.repos.d/ with the contents:
```
[Confluent.dist]
name=Confluent repository (dist)
baseurl=https://packages.confluent.io/rpm/4.0/7
gpgcheck=1
gpgkey=https://packages.confluent.io/rpm/4.0/archive.key
enabled=1

[Confluent]
name=Confluent repository
baseurl=https://packages.confluent.io/rpm/4.0
gpgcheck=1
gpgkey=https://packages.confluent.io/rpm/4.0/archive.key
enabled=1
```

Clean up, install and run:
```
sudo yum clean all
sudo yum install confluent-platform-oss-2.11
confluent start
```

Open your Kafka server.properties file (usually it's in /etc/kafka/) and make sure the following lines are present:
```
auto.create.topics.enable=true
num.partitions=1
listeners=PLAINTEXT://YOUR_KAFKA_IP:9092
zookeeper.connect=YOUR_ZOOKEEPER_IP:2181
delete.topic.enable=true
log.retention.ms=60000
```
Open your kafka-rest.properties file (usually it's in /etc/kafka-rest/) and make sure the following lines are present:
```
zookeeper.connect=YOUR_ZOOKEEPER_IP:2181
bootstrap.servers=PLAINTEXT://YOUR_KAFKA_IP:9092
```

Enable Kafka in src/main/resources/application.yml and fill in the kafka and zookeeper IP:
```
kafka:
  enabled: true
  zookeeper:
    url: "YOUR_ZOOKEEPER_IP:2181"
  broker:
    url: "YOUR_KAFKA_IP:9092" 
```
Restart Confluent:
```
confluent stop
confluent start
```

Lastly, you can adjust the inactivity threshold and challenge intervals (in milliseconds) in src/main/resources/application.yml.
The challenge Frequency is only valid if Kafka is enabled.
```
security:
  basic:
    enabled: false
  session:
    inactivityKickOut: 120000
    challengeFrequency: 30000
```
That's it. Open the index.html in src/SecureDiary folder and start writing!

<p align="center">
  <img src="https://github.com/maxamel/SpringZKAuth/blob/master/demo.png" />
</p>

# License

Published under the MIT License. This basically means the software is free and anyone can use it however they wish. No liability or warranty.

