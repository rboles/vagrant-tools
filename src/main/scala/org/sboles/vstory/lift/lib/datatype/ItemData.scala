
package org.sboles.vstory.lift.lib.datatype

/**
 * Generic container that describes an item (armor, weapon, etc). Items are
 * assumed to have an item type and a material and may (or may not) be
 * atomic.
 *
 * @author sboles
 */
class ItemData {

  var id: String = _
  var name: String = _

  var itemType: ItemType = new ItemType
  var material: Material = new Material

  private var _atomic: Boolean = true

  def this(_id: String) {
    this
    id = _id
  }

  def this(_id: String, _name: String) {
    this(_id)
    name = _name
  }

  def this(_id: String, _name: String, atomic: String) {
    this(_id, _name)
    this.atomic = atomic
  }

  def this(_id: String, _name: String, _atomic: Boolean) {
    this(_id, _name)
    atomic = _atomic
  }

  /**
   * @param True if item is atomic, false if not
   */
  def atomic_=(v: String): Unit = {
    _atomic = v.substring(0,1).toLowerCase match {
      case "t" => true
      case _ => false
    }
  }

  def atomic_=(v: Boolean): Unit = _atomic = v

  /**
   * @return True if item is atomic; false if not
   */
  def atomic: Boolean = _atomic

  /**
   * @return Tuple of id, name
   */
  def toTuple: (String, String) = {
    (id, name)
  }
}
