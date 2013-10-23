package jug.discovery.curator

import org.slf4j.LoggerFactory
import com.netflix.curator.framework.CuratorFrameworkFactory
import com.netflix.curator.retry.RetryOneTime

/**
 */
class MyCurator private(zkUrl: String) {
  private val log = LoggerFactory.getLogger(getClass)
  val curator = CuratorFrameworkFactory
    .builder()
    .connectionTimeoutMs(1000)
    .retryPolicy(new RetryOneTime(2000))
    .connectString(zkUrl)
    .build()
  curator.start()
  log.info("Started curator over " + zkUrl)

  def close() {
    curator.close()
  }
}

object MyCurator {
  def apply(zkUrl: String):MyCurator = new MyCurator(zkUrl)
}

