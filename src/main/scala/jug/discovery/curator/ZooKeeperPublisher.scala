package jug.discovery.curator

import com.netflix.curator.framework.CuratorFramework
import com.netflix.curator.framework.state.{ConnectionState, ConnectionStateListener}
import com.netflix.curator.x.discovery.ServiceInstance
import java.util.concurrent.atomic.AtomicReference
import jug.discovery.{Logging, PackLink, Publisher}

/**
  */
class ZooKeeperPublisher[K](curator: CuratorFramework,
                            discovery: MyDiscovery,
                            packer: PackLink[K]) extends Publisher[K] with Logging {

  private val listener = new ConnectionStateListener() {
    def stateChanged(client: CuratorFramework, newState: ConnectionState) = {
      if (newState == ConnectionState.RECONNECTED) {
        log.info("reregistering services on restored connection to zookeeper")
        reregister()
      }
    }
  }

  private val registered = new AtomicReference(List.empty[ServiceInstance[String]])

  curator.getConnectionStateListenable.addListener(listener)

  def close() {
    curator.getConnectionStateListenable.removeListener(listener)
    registered.getAndSet(List.empty).foreach(discovery.discovery.unregisterService)
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

  private def reregister() = registered.get.foreach(discovery.discovery.registerService)

  private def toServiceDescription(item: K, topic: String): ServiceInstance[String] = {
    val service: ServiceInstance[String] = ServiceInstance.builder[String]()
      .name(topic)
      .id(packer.uniqId(item, topic))
      .payload(packer.packLink(item))
      .build()
    service
  }

}
