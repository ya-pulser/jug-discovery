package jug.discovery

import java.util.concurrent.atomic.AtomicReference
import org.slf4j.LoggerFactory
import scala.util.Random

/**
 */
class CachedRemoteReferences[T](unpacker:UnpackLink[T]) {

  protected val log = LoggerFactory.getLogger(this.getClass)

  val handle = new AtomicReference[Map[String, T]](Map.empty)

  def cacheChanged(items: Iterable[RemoteReference]) {
    val published = handle.get()

    val newState: Map[String, T] = (for (k <- items) yield {
      (k.id,
        published.get(k.id) match {
          case Some(x) => x
          case None =>
            val a = unpacker.unpackLink(k.ref)
            a.get
        })
    }).toMap

    // todo: how correctly close ActorRef.forName when they to go the GC

    log.info("available actors: " + newState.keySet)
    handle.set(newState)
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
