
package org.sboles.vstory.lift.lib.datatype

import org.sboles.vstory.lift.model.{Armor => ArmorModel}
import org.sboles.vstory.lift.model.{ArmorCombo => ArmorComboModel}

/**
 * Provides a container for armor data
 *
 * @author sboles
 */
class Armor extends ItemData {

  /**
   * @param _id Armor id
   */
  def this(_id: String) {
    this
    id = _id
  }

  /**
   * @param _id Armor id
   * @param _name Armor name
   */
  def this(_id: String, _name: String) {
    this(_id)
    name = _name
  }

  /**
   * @param _id Armor id
   * @param _name Armor name
   * @param _atomic True if atomic; false if not
   */
  def this(_id: String, _name: String, _atomic: String) {
    this(_id, _name)
    atomic = _atomic
  }

  /**
   * @param _id Armor id
   * @param _name Armor name
   * @param _atomic True if atomic; false if not
   */
  def this(_id: String, _name: String, _atomic: Boolean) {
    this(_id, _name)
    atomic = _atomic
  }

  /**
   * Initialize from an armor model
   * @param m Armor model
   */
  def this(m: ArmorModel) {
    this(m.id.toString, m.name.toString, m.atomic)
  }

  /**
   * Initialize from the result of an armor combination
   * @param m Armor combination model
   */
  def this(m: ArmorComboModel) {
    this
    id     = (m.result.obj.map(_.id.is.toString) openOr "")
    name   = (m.result.obj.map(_.name.is.toString) openOr "")
    atomic = (m.result.obj.map(_.atomic.is.toString) openOr "")
  }
}
