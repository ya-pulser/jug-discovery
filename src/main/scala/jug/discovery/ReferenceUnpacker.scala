package jug.discovery

/**
 */
trait ReferenceUnpacker[T] {
  def unpack(reference: String): Option[T]
}
