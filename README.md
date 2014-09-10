scaleapp
=====

web app based on java netty.io with sharding and DHT for scaling...

used:
bootstrap for client ui & css
angularjs for client-side logic
java for server backend
netty for web server
postgresql as db server
senginx for load-balancing
maven to resolve java dependecies
docker for QA and R&D to run instances of nodes(see below) inside containers.

architecture:

cli1 cli2 ............cliN
    \     \           /
             senginx
            /   |   \
          app1 app2 .. appM
         / |  / |      /
      db1   db2 .........dbK

cli1, ..., cliK - web browser clients
senignx - node with senginx front-end for load-balancing
app1,..,appM - nodes with app instance (one java .jar running per node)
db1,...,dbK - nodes with PostgresSQL server inside (one SQL server per node)
/|\ - network connections.

senginx connects to app servers, and each app server can connect to each db instance to search, store and load data.


