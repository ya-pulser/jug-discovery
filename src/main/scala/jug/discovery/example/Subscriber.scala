package jug.discovery.example

import akka.actor.{Props, ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import jug.discovery.Logging
import jug.discovery.akka.{AnyClusteredActor, UriResolverToActorRef}
import jug.discovery.curator.{MyCurator, MyDiscovery, CuratedSubscriber}

/**
  */
object Subscriber extends Logging {

  def main(args: Array[String]) {

    val config = ConfigFactory.parseString(
      """
        |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.port=0
      """.stripMargin
    )

    val as = ActorSystem("XySystem", config)

    val discovery = MyDiscovery(MyCurator("localhost:2181"), "/discovery-root")

    val subscriber = new CuratedSubscriber[ActorRef](
      discovery.discovery,
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
