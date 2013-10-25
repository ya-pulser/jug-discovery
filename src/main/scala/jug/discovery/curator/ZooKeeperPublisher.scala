package jug.discovery.curator

import com.netflix.curator.x.discovery.{ServiceDiscovery, ServiceInstance}
import java.util.concurrent.atomic.AtomicReference
import jug.discovery.{Logging, ReferencePacker, Publisher}

/**
  */
class ZooKeeperPublisher[K](discovery: ServiceDiscovery[String],
                            packer: ReferencePacker[K]) extends Publisher[K] with Logging {

  private val registered = new AtomicReference(List.empty[ServiceInstance[String]])

  override def publish(item: K, name: String) = {
    val service = toServiceDescription(item, name)
    log.info("publishing service " + service)
    discovery.registerService(service)
    this.synchronized(registered.set(registered.get :+ service))
  }

  override def unpublish(item: K, name: String) = {
    val service = toServiceDescription(item, name)
    log.info("un-publishing service " + service)
    discovery.unregisterService(service)
    this.synchronized(registered.set(registered.get.filter(_.equals(service))))
  }

  def close() {
    registered.getAndSet(List.empty).foreach(discovery.unregisterService)
  }

  private def toServiceDescription(item: K, topic: String): ServiceInstance[String] = {
    val service: ServiceInstance[String] = ServiceInstance.builder[String]()
      .name(topic)
      .id(packer.uniqueId(item, topic))
      .payload(packer.pack(item))
      .build()
    service
  }

}
