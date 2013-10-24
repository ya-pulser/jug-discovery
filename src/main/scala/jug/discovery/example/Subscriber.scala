package jug.discovery.example

import akka.actor.{Props, ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import jug.discovery.Logging
import jug.discovery.akka.{AnyClusteredActor, UriResolverToActorRef}
import jug.discovery.curator.{ConfigForCurated, CuratedSubscriber}

/**
 */
object Subscriber extends Logging{

  def main(args: Array[String]) {

    val config = ConfigFactory.parseString("" +
      "akka.actor.provider = \"akka.remote.RemoteActorRefProvider\" \n" +
      "akka.remote.netty.port=0"
    )

    val as = ActorSystem("XySystem", config)

    val subscriber = new CuratedSubscriber[ActorRef](
      ConfigForCurated("127.0.0.1:2181", "/discovery-root"),
      new UriResolverToActorRef(as))

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
