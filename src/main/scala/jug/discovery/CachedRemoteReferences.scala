package jug.discovery

import java.util.concurrent.atomic.AtomicReference
import scala.util.Random

/**
  */
class CachedRemoteReferences[T](unpacker: UriResolver[T]) extends Logging {

  val handle = new AtomicReference[Map[String, T]](Map.empty)

  def cacheChanged(items: Iterable[RemoteReference]) {
    val published = handle.get()

    val freshHandle = (for (item <- items) yield {
      (item.id,
        published.get(item.id) match {
          case Some(x) => x
          case None =>
            val a = unpacker.resolve(item.ref)
            a.get
        })
    }).toMap

    // todo: how correctly close ActorRef.forName when they to go the GC

    log.info("available references: " + freshHandle.keySet)
    handle.set(freshHandle)
  }

  def close() = {
    handle.set(Map.empty)
  }

  def fetchOne: Option[T] = {
    val handles = handle.get
    if (handles.isEmpty) {
      None
    } else {
      val keys = handles.keySet.toSeq
      handles.get(keys(Random.nextInt(keys.size)))
    }
  }

  def fetchAll: Iterable[T] = {
    val handles = handle.get
    if (handles.isEmpty) {
      List.empty
    } else {
      handles.values
    }
  }

}
