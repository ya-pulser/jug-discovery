package jug.discovery.curator

import com.netflix.curator.framework.CuratorFramework
import com.netflix.curator.framework.state.ConnectionState
import com.netflix.curator.x.discovery.ServiceDiscovery
import com.netflix.curator.x.discovery.details.ServiceCacheListener
import jug.discovery.{Logging, RemoteReference, CachedRemoteReferences}
import scala.collection.JavaConversions._


/**
  */
class CachedRemoteReferenceToCuratorAdapter[K](topic: String,
                                               discovery: ServiceDiscovery[String],
                                               val refs: CachedRemoteReferences[K]) extends Logging {

  val listen = new ServiceCacheListener() {
    override def stateChanged(p1: CuratorFramework, p2: ConnectionState) {}

    override def cacheChanged() {
      log.info("cache changed for topic " + topic)
      val keys = serviceCache.getInstances
        .map(item => RemoteReference(item.getId, item.getPayload))
      refs.cacheChanged(keys)
    }
  }

  private val serviceCache = discovery.serviceCacheBuilder().name(topic).build()
  serviceCache.addListener(listen)
  serviceCache.start()
  listen.cacheChanged()
  log.info("service cache started for topic '{}'", topic)

  def close() = {
    serviceCache.removeListener(listen)
  }

}
