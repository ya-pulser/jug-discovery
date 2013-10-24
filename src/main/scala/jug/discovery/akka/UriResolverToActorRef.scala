package jug.discovery.akka

import akka.actor.{ActorSystem, ActorRef}
import jug.discovery.UriResolver

/**
 */
class UriResolverToActorRef(actorSystem:ActorSystem) extends UriResolver[ActorRef] {
  def resolve(path: String): Option[ActorRef] = Some(actorSystem.actorFor(path))
}
