# Storing system(cloud storage/ file sharing) using microservices 📂☁  


## Gateway 🚪

Gateway was done using pure Java 11 using java.net.http library. 
The gateway is the node that receives and forwards user tasks to the service nodes.  The gateway caches responses and balances the load of service nodes. Finally, it has a service registry and chooses from registered services when load balancing.
Load balancing is done through **Round Robin**, Cache represent a custom class similar to hash-map which will store the requests in RAM. More details about cache will be below.

## Cache 🧮
Requests get written to Cache using **consistency hashing**, we get the hashcode of the cache and of the result of the request which should be written into the cache, for example: cache1-  50, cache2-126, cache3 -230, If the request has the hash of 100 it will go to the cache2 if cache2 will be down it will connect to cache3. Using this approach we get **high availability**, so no matter what, the data will not be lost and it will be redirected to one of the caches.
Also, there is done cache replication, if data is written to a cache then others look at it and compare if they have this data or not, If they don't have it they copy the data if not then nothing is done.

## Microservices 🔬

Microservices are written in Python, FastApi, Nginx and connected to PostgreSQL. Every microservice is inside a container. Microservice nodes are the ones that do the work a client is interested in. They receive tasks, process them and send back responses. Processing requests is usually costly so we imply the help of gateways and caches. There are 4 currently running microservices. Two are for document logic, and two are for user logic. 
Documents microservices are depending on user microservices. After running docker-compose they can be accessed on local machine:  
http://localhost:8050/api/v1/docs/docs  
http://localhost:8050/api/v1/user/docs  
http://localhost:9090/api/v2/docs/docs  
http://localhost:9090/api/v2/user/docs  
There are few endpoints you can try on. But first create users in the user microservices to be able to create documents, because documents can't be created if they have no user.
## Docker 🐳

Everything is within a container inside a docker-compose file. Each microservice folder has a docker compose which has set all the images, dbs and dependencies.

## DB replication 📦➡📦📦📦

This is done using bitnami https://github.com/bitnami/bitnami-docker-postgresql-repmgr . Microservice connects to a master and in docker compose u set up the slaves which will be listening on it. Once something is written to the master then it will be replicated to its slaves.

## Architecture Diagram 📝

![alt text](https://raw.githubusercontent.com/DivineBee/microservices-cloud/main/docs/Architecture.jpg)

### <center>If it was helpful to you leave a ⭐</center>
