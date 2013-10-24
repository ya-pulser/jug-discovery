package jug.discovery.curator

import com.netflix.curator.x.discovery.ServiceDiscovery
import java.util.concurrent.atomic.AtomicReference
import jug.discovery.{UriResolver, CachedRemoteReferences, Subscriber}

/**
  */
class CuratedSubscriber[K](discovery: ServiceDiscovery[String], unpacker: UriResolver[K]) extends Subscriber[K] {

  private val handle = new AtomicReference[Map[String, CachedRemoteReferenceToCuratorAdapter[K]]](Map.empty)

  def subscribe(topic: String): CachedRemoteReferences[K] = {
    handle.get.get(topic) match {
      case Some(x) => x.refs
      case None =>
        synchronized {
          val refs: CachedRemoteReferences[K] = new CachedRemoteReferences[K](unpacker)
          val adapter = new CachedRemoteReferenceToCuratorAdapter(topic, discovery, refs)
          handle.set(handle.get.updated(topic, adapter))
          refs
        }
    }
  }
}
