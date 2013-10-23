package jug.discovery.curator

import org.slf4j.LoggerFactory
import com.netflix.curator.utils.EnsurePath
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder

/**
 */
class MyDiscovery private(curator: MyCurator, prefix: String) {
  private val log = LoggerFactory.getLogger(getClass)

  new EnsurePath(prefix).ensure(curator.curator.getZookeeperClient)
  val discovery = ServiceDiscoveryBuilder
    .builder(classOf[String])
    .basePath(prefix)
    .client(curator.curator)
    .build()
  discovery.start()
  log.info("Started discovery over " + curator)

  def close() {
    discovery.close()
  }
}

object MyDiscovery {

  def apply(zkUrl: String, prefix: String):MyDiscovery = MyDiscovery(MyCurator(zkUrl), prefix)

  def apply(curator: MyCurator, prefix: String):MyDiscovery = new MyDiscovery(curator, prefix)

}
