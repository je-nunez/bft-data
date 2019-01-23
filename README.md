# Brief

Bizantine Fault Tolerance replicas on a data object (using the [bft-smart/library](https://github.com/bft-smart/library)).

# WIP

This project is a *work in progress*. The implementation is *incomplete* and
subject to change. The documentation can be inaccurate.

# Notes

The [BFT-smart library](https://github.com/bft-smart/library) has other
examples of the library, like a
[string->string map server](https://github.com/bft-smart/library/tree/master/src/bftsmart/demo/map).
This example was inspired initially on their
[integer-counter example](https://github.com/bft-smart/library/tree/master/src/bftsmart/demo/counter).

# Compilation

With Maven:

      mvn clean
      mvn initialize
      mvn

Note: this repository has a copy of the
[BFT-SMaRt.jar library](https://github.com/bft-smart/library/tree/master/bin)
in its `lib/` directory here, since there is no
repo dependency for this library yet.

# Run example

(Note: The command-line arguments of these run examples below are in the Maven
`pom.xml` file. You may change it to add more server replicas, or change the
string that the client-writer sends, etc.)

To run the server replicas (`N` = 4, `F` = 1):

      mvn exec:java@replica-0
      mvn exec:java@replica-1
      mvn exec:java@replica-2
      mvn exec:java@replica-3

To run a client (the write example):

      mvn exec:java@client-write

To run a client (the read example):

      mvn exec:java@client-read

Example outputs of the server replicas (e.g., replica 0)
(you may change the default log-level in
`resources/log4j.properties` from `INFO` to `DEBUG` to
have more details of the BFT protocol communication):

      ... INFO [JsonStringServer.main()] (ServerViewController.java:72) - Using view stored on disk
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:142) - ID = 0
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:143) - N = 4
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:144) - F = 1
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:145) - Port = 11000
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:146) - requestTimeout = 2000
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:147) - maxBatch = 400
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:148) - Using MACs
      ... INFO [JsonStringServer.main()] (NettyClientServerCommunicationSystemServerSide.java:150) - Binded replica to IP address 127.0.0.1

When the client writer operates, a replica logs:

      ... INFO [nioEventLoopGroup-3-1] (NettyClientServerCommunicationSystemServerSide.java:225) - Session Created, active clients=0
      (1) New value was set. Current value = { "field1": "value1", "field2": [ "arr_value1", "arr_value_2" ] }

When the client reader operates, a replica logs::

      ... INFO [nioEventLoopGroup-3-2] (NettyClientServerCommunicationSystemServerSide.java:225) - Session Created, active clients=1
      (2) JSON String value: { "field1": "value1", "field2": [ "arr_value1", "arr_value_2" ] }

When the client writer operates, it logs:

      ... INFO [JsonStringClient.main()] (NettyClientServerCommunicationSystemClientSide.java:140) - Connecting to replica 0 at /127.0.0.1:11000
      ... INFO [nioEventLoopGroup-2-1] (NettyClientServerCommunicationSystemClientSide.java:265) - Channel active
      ... INFO [JsonStringClient.main()] (NettyClientServerCommunicationSystemClientSide.java:140) - Connecting to replica 1 at /127.0.0.1:11010
      ... INFO [nioEventLoopGroup-2-2] (NettyClientServerCommunicationSystemClientSide.java:265) - Channel active
      ... INFO [JsonStringClient.main()] (NettyClientServerCommunicationSystemClientSide.java:140) - Connecting to replica 2 at /127.0.0.1:11020
      ... INFO [nioEventLoopGroup-2-3] (NettyClientServerCommunicationSystemClientSide.java:265) - Channel active
      ... INFO [JsonStringClient.main()] (NettyClientServerCommunicationSystemClientSide.java:140) - Connecting to replica 3 at /127.0.0.1:11030
      ... INFO [nioEventLoopGroup-2-4] (NettyClientServerCommunicationSystemClientSide.java:265) - Channel active
      , returned value: { "field1": "value1", "field2": [ "arr_value1", "arr_value_2" ] }

With the logger's default level set to DEBUG for `bftsmart.tom.ServiceReplica` and `bftsmart.tom.ServiceProxy`, it details for the client write example (`mvn exec:java@client-write`) besides the above messages:

      # In the backend-replica:
       
      ... DEBUG [Delivery Thread] (ServiceReplica.java:293) - Processing TOMMessage from client 1001 with sequence number 0 for session -1912886745 decided in consensus 0
      ... DEBUG [Delivery Thread] (ServiceReplica.java:327) - Delivering request from 1001 via SingleExecutable
      (2) New value was set. Current value = { "field1": "value1", "field2": [ "arr_value1", "arr_value_2" ] }
      ... DEBUG [Delivery Thread] (ServiceReplica.java:341) - sending reply to 1001

      # In the client communication proxy with the backend replicas (N = 4, F = 1)
       
      ... DEBUG [JsonStringClient.main()] (ServiceProxy.java:258) - Sending request (ORDERED_REQUEST) with reqId=0
      ... DEBUG [JsonStringClient.main()] (ServiceProxy.java:259) - Expected number of matching replies: 3
      ... DEBUG [nioEventLoopGroup-2-1] (ServiceProxy.java:373) - Synchronously received reply from 0 with sequence number 0
      ... DEBUG [nioEventLoopGroup-2-2] (ServiceProxy.java:373) - Synchronously received reply from 1 with sequence number 0
      ... DEBUG [nioEventLoopGroup-2-2] (ServiceProxy.java:393) - Receiving reply from 1 with reqId:0. Putting on pos=1
      ... DEBUG [nioEventLoopGroup-2-4] (ServiceProxy.java:373) - Synchronously received reply from 3 with sequence number 0
      ... DEBUG [nioEventLoopGroup-2-4] (ServiceProxy.java:393) - Receiving reply from 3 with reqId:0. Putting on pos=3
      ... DEBUG [nioEventLoopGroup-2-1] (ServiceProxy.java:393) - Receiving reply from 0 with reqId:0. Putting on pos=0
      ... DEBUG [JsonStringClient.main()] (ServiceProxy.java:284) - Response extracted = [0:-1912886745:0]
      , returned value: { "field1": "value1", "field2": [ "arr_value1", "arr_value_2" ] }
      ... DEBUG [nioEventLoopGroup-2-3] (ServiceProxy.java:373) - Synchronously received reply from 2 with sequence number 0
      ... DEBUG [nioEventLoopGroup-2-3] (ServiceProxy.java:378) - throwing out request: sender=2 reqId=0

