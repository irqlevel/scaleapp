###Scaleapp
web app framework based on java netty.io with sharding and DHT for scaling...

#### Build status:
[![build status](https://travis-ci.org/irqlevel/scaleapp.svg?branch=master)](https://travis-ci.org/irqlevel/scaleapp)

#### Architecture:
```
cli1 cli2 ............cliN
    \     \           /
             senginx
            /   |   \
          app1 app2 .. appM
         / |  / |      /
      db1   db2 .........dbK
```
- cli1, ..., cliK - web browser clients
- senignx - host with senginx front-end for load-balancing
- app1,..,appM - hosts with app instance (one java .jar running per host)
- db1,...,dbK - hosts with PostgresSQL server inside (one SQL server per host)
- /|\ - network connections.
- senginx connects to app servers, and each app server can connect to each db instance to search, store and load data.

#### Dependencies:
- bootstrap as client ui and css
- angularjs as client-side logic
- java as server backend
- netty as web server
- postgresql as db server
- senginx for load-balancing
- maven to resolve java dependecies
- docker to run different types of hosts(see below) inside containers in one physical host.

