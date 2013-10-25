package jug.discovery.akka

import akka.actor.{ActorSystem, ActorRef}
import jug.discovery.ReferenceUnpacker

/**
 */
class ReferenceUnpackerToActorRef(actorSystem:ActorSystem) extends ReferenceUnpacker[ActorRef] {
  def unpack(path: String): Option[ActorRef] = Some(actorSystem.actorFor(path))
}
