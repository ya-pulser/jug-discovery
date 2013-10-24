package jug.discovery.example

import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import jug.discovery.Logging
import jug.discovery.akka.PackLinkToActorRef
import jug.discovery.curator.{ZooKeeperPublisher, MyDiscovery, MyCurator}

/**
  */
object Publisher extends Logging {

  def main(args: Array[String]) {

    class TestActor extends Actor {

      override def preStart(): Unit = {
        log.info("Starting " + this)
      }

      override def postStop(): Unit = {
        log.info("Stopping " + this)
      }

      def receive = {
        case msg => log.info(msg.toString + " from " + sender)
      }
    }

    val config = ConfigFactory.parseString(
      """
        |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.port=0
      """.stripMargin
    )

    val curator = MyCurator("localhost:2181")
    val discovery = MyDiscovery(curator, "/discovery-root").discovery

    val as = ActorSystem("MySystem", config)

    val publisher = new ZooKeeperPublisher[ActorRef](curator.curator, discovery, new PackLinkToActorRef(as))

    val actor1 = as.actorOf(Props[TestActor], name = "vasya")
    publisher.publish(actor1, "mega-test-actor")

    log.info("Ready to serve ... " + new PackLinkToActorRef(as).packLink(actor1))

  }


}
