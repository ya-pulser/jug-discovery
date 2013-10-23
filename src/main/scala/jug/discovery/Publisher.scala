package jug.discovery

/**
 */
trait Publisher[K] {
  def publish(item: K, name: String)

  def unpublish(item: K, name: String)
}

trait Subscriber[K] {
  def subscribe(topic: String): CachedRemoteReferences[K]
}