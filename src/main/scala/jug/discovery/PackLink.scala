package jug.discovery

/**
 */
trait PackLink[T] {
  def packLink(t: T): String

  def uniqId(t: T, tag: String): String
}
