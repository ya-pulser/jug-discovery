package jug.discovery

/**
 */
trait UriResolver[T] {
  def resolve(path: String): Option[T]
}
