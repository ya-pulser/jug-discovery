package jug.discovery.akka

import akka.actor._
import akka.remote.RemoteActorRefProvider
import jug.discovery.ReferencePacker

/**
 */
class ReferencePackerToActorRef(val actorSystem: ActorSystem) extends ReferencePacker[ActorRef] {

  // http://stackoverflow.com/a/14289707/707608
  private def buildId(defaultAddress: Address, name: String) = defaultAddress.hostPort + "=" + name

  private def defAddr(actorSystem: ActorSystem) = MyExtension(actorSystem).address

  override def pack(actor: ActorRef) = {
    //    val defaultAddress: Address = system.asInstanceOf[ExtendedActorSystem].provider.getDefaultAddress
    //    val defaultAddress: Address = system.asInstanceOf[ExtendedActorSystem].provider.rootPath.address
    val defaultAddress: Address = defAddr(actorSystem)
    val actorFullPath: String = actor.path.toStringWithAddress(defaultAddress)
    actorFullPath
  }

  override def uniqueId(t: ActorRef, name: String): String = {
    val defaultAddress: Address = defAddr(actorSystem)
//    buildId(defaultAddress, name + "." + t)
    buildId(defaultAddress, name)
  }

}

class MyExtensionImpl(system: ExtendedActorSystem) extends Extension {
  def address = system.provider match {
    case rarp: RemoteActorRefProvider => rarp.transport.address
    case _ => system.provider.rootPath.address
  }
}

object MyExtension extends ExtensionKey[MyExtensionImpl]

