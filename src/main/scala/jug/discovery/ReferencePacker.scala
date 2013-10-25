package jug.discovery

/**
 */
trait ReferencePacker[T] {
  def pack(t: T): String

  def uniqueId(t: T, tag: String): String
}
