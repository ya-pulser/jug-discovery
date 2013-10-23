package jug.discovery.example

import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory
import akka.actor.{Props, ActorRef, ActorSystem}
import jug.discovery.curator.{ConfigForCurated, CuratedSubscriber}
import jug.discovery.akka.{AnyClusteredActor, UnpackLinkToActorRef}

/**
 */
object Subscriber {
  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {

    val config = ConfigFactory.parseString("" +
      "akka.actor.provider = \"akka.remote.RemoteActorRefProvider\" \n" +
      "akka.remote.netty.port=0"
    )

    val as = ActorSystem("XySystem", config)

    val subscriber = new CuratedSubscriber[ActorRef](
      ConfigForCurated("127.0.0.1:2181", "/discovery-root"),
      new UnpackLinkToActorRef(as))

    val references = subscriber.subscribe("mega-test-actor")

    val actor = as.actorOf(Props(new AnyClusteredActor(references)))

    log.info("Ready to пыщь пыщь")

    actor ! "Hello, world!"

    log.info("Let's sleep")

    Thread.sleep(60 * 1000)
    log.info("Let's awake")

    actor ! "Time to sleep!"

    log.info("We done!")

    as.shutdown()

  }

}
