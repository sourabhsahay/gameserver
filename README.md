Game Server 

Creating a Game Server is challenging on many levels.Services are stateful (different than conventional E-commerce model), scale is high and write:read ratio may be as much as 3:1.
Also there are issues of network latency which may require not only a faster protocol, but also data compression.

While part of scalability can be achieved through horizontal scaling, it is not as simple as scaling many servers. Inter-server communication between services will increase latency, so that also has to be kept in mind. 
 :
This document is divided in two parts : -
Game Server Architecture and implementation (As per problem)
Scalability and Future Scope

Game Server Architecture And Implementation:

Networking : 
       Since Game requires fast transport between different services, conventional TCP may not be able to perform at the frame rate required. For this, UDP has been used for the implementation for most of the game fast exchanges with server. For conventional services like Login, TCP or WebSocket is fine. For sending chats to game, the data needs to be consistent (chats need to appear in Sequence in which they arrive, so that has been handled in TCP/Websocket). For full duplex communication, Websocket is preferable to conventional HTTP. 

Data Marshalling/UnMarshalling:
It is important to marshall/unmarshall the data for communication over the network for maximum efficiency. Sometimes, the packets do not arrive at one go and are collated together, so marshalling scheme will help packing as much data as possible. For illustration purpose, in the solution Json format has been used. But it can be readily replaced by binary formats : 
For Flash based clients, AMF protocol can be used.
Unity based clients can be made to work with protobuf. The implementation contains import of protostuff library which helps in annotation driven protobuff generation.

To that effect in the codebase, two servers are implemented.
com.junglee.task.server.impl.TCPServer (Used for Login and Chat Server)
com.junglee.task.server.impl.UDPServer (Used for game session communication)

Database Handling:
Database can be decided depending upon storage and access pattern and consistency/availability guarantees. Stats are past data for user which can be stored in NoSQL solution like MongoDB. State/Session for user can be stored in fast distributed caches like Redis or Mem-cache. Both the game data and chat tend to be write-heavy, so Nosql wins in this regard over conventional DB. Nosql these days come with eventual consistency guarantees 
And implementation contains (LSM tree instead of conventional B Trees) which results in high throughput. 
Chat :The difference is that chat requires data to be highly consistent which can be achieved with a bit of latency (All chats connected to one channel can be partitioned in one partition, so latency becomes an issue). Using read replicas and write master, chats can be taken care of.Writes can be written to majority of nodes and masters keeps track of which nodes have this value. Failure and recovery can be managed by another machine with master which takes over if master fails.

Game session writes : This needs to be highly available with eventual consistency. Also failure and recovery can be taken care by consistent hashing.

In Memory storage of data : Kyro can be used for storing the data in memory. This gives a very high compression compared to conventional Java.

Thread Management : Since a game server maintains state and has high write requirement at the same time, it makes sense to not block one thread for each incoming connection. Using Java NIO Channel  and event loops helps in this regard. Netty has been used in the project for building up things.
Moreover, the internal communication between services is asynchronous and event-driven which scales better than conventional threading scheme. This has been proven time and again in nginx and new systems like AKKA.
That said, there are places for thread synchronization (creating new tables, adding new players to Table) which have been handled in (PokerTable and PokerTableManager).

Event Dispatcher : com.junglee.task.event.impl.ExecutorEventDispatcher is used for handling events. For good scalability, as mentioned in the previous point, it is necessary that communication is asynchronous in nature and event dispatcher helps in that. Also, as mentioned in the problem document, it helps in keeping the services loosely coupled.
 For example, 
 Incoming packets are regarded as events for which handlers are used for processing. Even the game logic is contained in an eventHandler.
For pushing data for Stats, event handler is used.
For chat processing, event handler is used.
The communication between TableManager(PokerTableManager) and PokerTable(at which a particular game instance is being played) is through event Dispatcher.
In this manner the same Event Dispatcher  pool can be used across multiple services.

Authentication, Player Creation and Player Management :  Authentication is done in TCPUpStreamHandler. Currently the DBLookUpServiceImpl is used for user and gameChannel(explaned later). Chats are contained in ChatServiceImpl. For illustration purposes, in-memory Java HashMap is used, but it can be readily replaced by DB.

Game Table Management/Player Management/Game Logic.
This will require a bit explanation
Game state is managed by GameStateManagerService (get or Set State, serialize/deserialize game states etc)
For maintaining user actions  for a game, PlayerSession is used. A Player can play mutiple games together and has a set of sessions for each of these games.
A player session is linked to a GameChannel where multiple players collaborate. For instance, 
A poker table could be one game channel, a chessboard can be another.
Game Logic is handled in a “handler” in PokerTable (which extends GameChannel).

So for a game Channel, A Player Session is proxy for remote client. 
A SessionManager  is used for maintaining Session. First incoming request is Login, upon which PlaySession is created and linked to a PokerTable instance. Also SessionManager stores
<remoteAddress, PlaySession> key-value pair which can be subsequently used.

Data Flow : - Incoming Message (Session events)
Network => PlayerSession => PlayerSession Handler(attached in Poker Table) => Game Channel( Or Poker Table) => PokerSessionHandler



Outgoing event (NetworkEvent) : Used for broad-cast and sending individually
For sending individually, PlayerSession is directly used, which uses TCP/UDPSender to send.
For broadcast, PokerTable is used, which calls event dispatcher, but all PlayerSession(s) are registered, so they get the event. Exactly in the reverse order

Sessions can be stored in a key-value store like RIAK or cached in Redis/Memcached.

GAME LOGIC : Game Logic is contained in PokerSessionHandler. As it is event driven, it is handled whenever there is an input from player. This is in continuation of even-driven design given in problem statement.


Project Setup :
All different parts are wired together using spring DI framework. Starting point is GameServer.


Extensibility:
Any other game can be developed extending GameChannelSession and developing custom-logic. At Server level, new/custom protocol can be registered by implementing com.junglee.task.server.Server.
As the various services are wired through interfaces, the implementation can be changed.

Not covered : If all players leave, destroy the table. (Made it more consistent with if everyone moves, return to pool, like in case of game over, though other can be implemented easily).

Future Scope :
EventsDispatcher can be extended to make sure that it is more cognizant of sending events not only based on events +sessions.
The event handling is based on handler and source/handlers are separated. There maybe cases where callbacks are required. Akka frame work provides this along with consistent hashing routing, so can be used to scale this further.
