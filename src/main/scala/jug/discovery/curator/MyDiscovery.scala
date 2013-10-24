package jug.discovery.curator

import com.netflix.curator.framework.CuratorFramework
import com.netflix.curator.utils.EnsurePath
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder
import jug.discovery.Logging

/**
  */
class MyDiscovery private(curator: CuratorFramework, prefix: String) extends Logging {

  new EnsurePath(prefix).ensure(curator.getZookeeperClient)

  val discovery = ServiceDiscoveryBuilder
    .builder(classOf[String])
    .basePath(prefix)
    .client(curator)
    .build()
  discovery.start()
  log.info("Started discovery over " + curator)

  def close() {
    discovery.close()
  }
}

object MyDiscovery {

  def apply(zkUrl: String, prefix: String): MyDiscovery = MyDiscovery(MyCurator(zkUrl), prefix)

  def apply(curator: MyCurator, prefix: String): MyDiscovery = new MyDiscovery(curator.curator, prefix)

}
