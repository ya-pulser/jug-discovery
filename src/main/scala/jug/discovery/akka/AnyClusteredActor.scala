package jug.discovery.akka

import akka.actor.{ActorRef, ActorLogging, Actor}
import jug.discovery.CachedRemoteReferences

/**
 */
class AnyClusteredActor(service: CachedRemoteReferences[ActorRef]) extends Actor with ActorLogging {
  def receive = {
    case msg => service.fetchOne match {
      case Some(actor) =>
        log.debug("forwarding ... me: " + self + ", to:" + actor + ", sender: " + sender)
        actor forward msg
      case None =>
        log.error("no actor, forwarding to dead letters")
        context.system.deadLetters forward msg
    }
  }
}
