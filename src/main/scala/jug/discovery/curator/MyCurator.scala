package jug.discovery.curator

import com.netflix.curator.framework.CuratorFrameworkFactory
import com.netflix.curator.retry.RetryOneTime
import jug.discovery.Logging

/**
  */
class MyCurator private(zkUrl: String) extends Logging {

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
  def apply(zkUrl: String): MyCurator = new MyCurator(zkUrl)
}

