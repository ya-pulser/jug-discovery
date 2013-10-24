package jug.discovery

import org.slf4j.LoggerFactory

/**
 * TODO
 */
trait Logging {
  val log = LoggerFactory.getLogger(super.getClass)
}
