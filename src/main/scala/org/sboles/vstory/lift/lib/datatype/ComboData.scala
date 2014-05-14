
package org.sboles.vstory.lift.lib.datatype

/**
 * Provides a container for data describing a combination. A combination
 * includes two items and a result.
 *
 * @author sboles
 */
class ComboData(a: ItemData, b: ItemData) {
  val item1: ItemData = a
  val item2: ItemData = b

  var result: Option[ItemData] = None
  var swap: Option[ItemData] = None
}
