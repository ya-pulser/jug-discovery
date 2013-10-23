package jug.discovery.akka

import akka.actor.{ActorSystem, ActorRef}
import jug.discovery.UnpackLink

/**
 */
class UnpackLinkToActorRef(actorSystem:ActorSystem) extends UnpackLink[ActorRef] {
  def unpackLink(path: String): Option[ActorRef] = Some(actorSystem.actorFor(path))
}
