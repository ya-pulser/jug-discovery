package jug.discovery

/**
 */
trait UnpackLink[T] {
  def unpackLink(path: String): Option[T]
}
