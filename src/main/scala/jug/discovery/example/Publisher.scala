package jug.discovery.example

import org.slf4j.LoggerFactory
import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import jug.discovery.curator.{CuratedPublisher, MyDiscovery, MyCurator}
import jug.discovery.akka.PackLinkToActorRef

/**
 */
object Publisher {
  private val log = LoggerFactory.getLogger(getClass)

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

    val config = ConfigFactory.parseString("" +
      "akka.actor.provider = \"akka.remote.RemoteActorRefProvider\" \n" +
      "akka.remote.netty.port=0"
    )

    val as = ActorSystem("MySystem", config)

    val curator = MyCurator("127.0.0.1:2181")
    val discovery = MyDiscovery(curator, "/discovery-root")
    val publisher: CuratedPublisher[ActorRef] = new CuratedPublisher[ActorRef](
      curator, discovery, new PackLinkToActorRef(as))

    val actor1 = as.actorOf(Props[TestActor], name = "vasya")
    publisher.publish(actor1, "mega-test-actor")

    log.info("Ready to serve ... " + new PackLinkToActorRef(as).packLink(actor1))

  }


}
