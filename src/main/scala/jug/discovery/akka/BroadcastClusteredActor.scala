package jug.discovery.akka

import akka.actor.{ActorRef, ActorLogging, Actor}
import jug.discovery.CachedRemoteReferences

/**
 */
class BroadcastClusteredActor(service: CachedRemoteReferences[ActorRef]) extends Actor with ActorLogging {
   def receive = {
     case msg => service.fetchAll.foreach(_ forward msg)
   }
 }
