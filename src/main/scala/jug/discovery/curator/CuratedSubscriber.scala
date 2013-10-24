package jug.discovery.curator

import java.util.concurrent.atomic.AtomicReference
import jug.discovery.{UriResolver, CachedRemoteReferences, Subscriber}

/**
 */
class CuratedSubscriber[K] (config: ConfigForCurated, unpacker:UriResolver[K]) extends Subscriber[K] {

  val curator = MyCurator(config.zkUrls)
  val discovery = MyDiscovery(curator, config.rootPath)
  val handle: AtomicReference[Map[String, CachedRemoteReferenceToCuratorAdapter[K]]] = new AtomicReference[Map[String, CachedRemoteReferenceToCuratorAdapter[K]]](Map.empty)

  def subscribe(topic: String): CachedRemoteReferences[K] = {
    handle.get.get(topic) match {
      case Some(x) => x.refs
      case None =>
        synchronized {
          val refs: CachedRemoteReferences[K] = new CachedRemoteReferences[K](unpacker)
          val adapter = new CachedRemoteReferenceToCuratorAdapter(topic, discovery.discovery, refs)
          handle.set(handle.get.updated(topic, adapter))
          refs
        }
    }
  }
}
