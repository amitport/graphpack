GraphPack is a decentralized graph database.

Instead of the traditional model where the graph is managed by a single administrator, in GraphPack, there are many clients that separately manage their own graph. Such a _client graph_ includes only nodes added by the corresponding client together with those nodes' **outgoing** edges (the target of those edges may refer to nodes in other client graphs).

In GraphPack each client graph is managed by some GraphPack service that is trusted by that client. A GraphPack service remotely provides:

  1. Persistent graph structure management - nodes and outgoing edges can be read/added/removed/edited by clients
  1. Dynamic tasks - clients can attach tasks to their nodes (java functions) and specify that those tasks may be executed upon call or periodically (advanced topic)
  1. Access control - clients can specify who can access their graph structure and tasks and how (not completely implemented, although the design is well set-up to accommodate this requirement)
  1. _Simple graph access_ - access methods that transparently bridge between different services (since client graph may refer to nodes in other client graph which may be managed by a different service). This is done using a traverse/query language which is based on Neo4j's [Cypher language](http://docs.neo4j.org/chunked/stable/cypher-query-lang.html).

| **GraphPack's model:** |
|:-----------------------|
| http://graphpack.googlecode.com/svn/trunk/graphpack_model.PNG |


---


There are many cases in which GraphPack may be useful.

Some cases are inherently decentralized (the graph is the network):
  * ad-hoc networks such as mobile phones and sensors networks
  * unstructured P2P networks such as Gnutella.

Other systems manage a graph that is distributed among autonomous services:
  * semantic web
  * decentralized social networks
  * decentralized trust management systems


---


This repository includes a few eclipse project:

  * GraphPackCore - pure graphpack functionality. Persistence, task management, access control and communication issues are injected using [google guice](http://code.google.com/p/google-guice/)
  * GraphPackImpl - includes implementations for Persistence (in-memory and [JDO](http://www.oracle.com/technetwork/java/index-jsp-135919.html)), communication (using [RMI](http://www.oracle.com/technetwork/java/javase/tech/index-jsp-136424.html)) and task management (using [Quartz](http://quartz-scheduler.org/)).
  * GraphPackJavaDoc - creates the [API documentation](https://graphpack.googlecode.com/svn/trunk/GraphPackJavadoc/index.html)
  * PackCypherParser - contains the parser for the cypher based language (called PackCypher)
  * Tests - contains JUnit tests


---


A simple usage example:

```
//setup RMI registry
Registry reg = LocateRegistry.createRegistry(1099); 

//creating a GraphPack service called "s1" that uses `reg' as registry 
GraphPackService s1 = new RmiInmemoryQuartzGraphPackService("s1", reg);     

//creating a client, "c1", with two nodes: "n1" and "n2"
s1.createClient("c1");
s1.client("c1").createNode("n1");
INode n1 = s.client("c1").node("n1");

s1.client("c1").createNode("n2");
INode n2 = s1.client("c1").node("n2");

//add and edge from "n1" to "n2"
n1.addOutgoingEdge(n2.location(), Payload.EMPTY);
//start "s1"                
s1.start();

//create a second service "s2"
GraphPackService s2 = new RmiInmemoryQuartzGraphPackService("s2", reg);     
                
//create a second client ("c2") at "s2" with a node "n3"
s2.createClient("c2");
s2.client("c2").createNode("n3");
INode n3 = s2.client("c2").node("n3");

//add an edge from n2 to n3 (cross network)
n2.addOutgoingEdge(n3.location(), Payload.EMPTY);

//start "s2"
s2.start

ResultSet rs = n1.traverse("-->*0..1-->x");

//(rs will contain both n2 and n3)
```


---


If you are interested in digging into the code I suggest you'll start in GraphPackCore project, and more specifically: graphpack.local.Node#traverse (this is were the interesting stuff happens)