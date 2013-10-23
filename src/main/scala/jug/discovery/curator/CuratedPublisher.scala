package jug.discovery.curator

import org.slf4j.LoggerFactory
import com.netflix.curator.framework.CuratorFramework
import com.netflix.curator.x.discovery.ServiceInstance
import java.util.concurrent.atomic.AtomicReference
import com.netflix.curator.framework.state.{ConnectionState, ConnectionStateListener}
import jug.discovery.{PackLink, Publisher}

/**
 */
class CuratedPublisher[K](curator: MyCurator, discovery: MyDiscovery, packer:PackLink[K]) extends Publisher[K] {

  private val log = LoggerFactory.getLogger(getClass)

  val listener = new ConnectionStateListener() {
    def stateChanged(client: CuratorFramework, newState: ConnectionState) = {
      if (newState == ConnectionState.RECONNECTED) {
        log.info("reregistering services on restored connection to zookeeper")
        reregister()
      }
    }
  }

  curator.curator.getConnectionStateListenable.addListener(listener)

  def close() {
    curator.curator.getConnectionStateListenable.removeListener(listener)
    registered.getAndSet(List.empty).foreach(discovery.discovery.unregisterService)
  }

  private val registered = new AtomicReference[List[ServiceInstance[String]]](List.empty)

  private def reregister() = registered.get.foreach(discovery.discovery.registerService)

  def toServiceDescription(item: K, topic: String): ServiceInstance[String] = {
    val service: ServiceInstance[String] = ServiceInstance.builder[String]()
      .name(topic)
      .id(packer.uniqId(item, topic))
      .payload(packer.packLink(item))
      .build()
    service
  }

  override def publish(item: K, name: String) = {
    val service = toServiceDescription(item, name)
    log.info("publishing service " + service)
    discovery.discovery.registerService(service)
    this.synchronized(registered.set(registered.get :+ service))
  }

  override def unpublish(item: K, name: String) = {
    val service = toServiceDescription(item, name)
    log.info("un-publishing service " + service)
    discovery.discovery.unregisterService(service)
    this.synchronized(registered.set(registered.get.filter(_.equals(service))))
  }

}
