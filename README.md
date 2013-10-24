Implementation of Service Discovery Design Pattern for Scala actors backed by Curator.

We have environment where service providing actors can be started and stopped on any available node and we should balance work load across them.

Key terms:
- service provider
  some entity, that can server user requests
- reference
  address of the service provider
- shared memory
  communication media to transfer reference to user
- user
  some entity, that wants to use provider's services
- topic
  shared memory divided on topics to groups of service providers per service type

Current implementation:
- service provider
  your akka actor (for example matcher of entities to some set of ids), that publishes it's remote link into the shared memory

- shared memory
  implemented as netflix curator service discovery library running over zookepper ensemble 
  list of all registered providers are available for subscibers as instance of CachedRemoteRefereces
  each registration is "pushed" to all listeners and automatically updated when some service provider stops or disconnected

- topic
  for example - matcher / saver etc.
  
- pair of PackLinkToActorRef / UnpackLinkToActorRef - provides serialization / deserialization compatible with the shared memory provider

Example of usage can be found in 'src/main/scala/jug/discovery/example' folder.

Other implementation can use shared memory implementaion in form of database table where providers publish their references and background timer task that pulls the table data into the subscribers memory.

If you do not use akka actors, but use some kind of REST / http based providers - you implement other PackLink / UnpackLink pair ...
