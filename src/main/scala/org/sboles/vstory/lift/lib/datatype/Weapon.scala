
package org.sboles.vstory.lift.lib.datatype

import org.sboles.vstory.lift.model.{Weapon => WeaponModel}
import org.sboles.vstory.lift.model.{WeaponCombo => WeaponComboModel}

class Weapon extends ItemData {

  /**
   * @param _id Weapon id
   */
  def this(_id: String) {
    this
    id = _id
  }

  /**
   * @param _id Weapon id
   * @param _name Weapon name
   */
  def this(_id: String, _name: String) {
    this(_id)
    name = _name
  }

  /**
   * @param _id Weapon id
   * @param _name Weapon name
   * @param _atomic True if atomic; false if not
   */
  def this(_id: String, _name: String, atomic: String) {
    this(_id, _name)
    this.atomic = atomic
  }

  /**
   * @param _id Weapon id
   * @param _name Weapon name
   * @param _atomic True if atomic; false if not
   */
  def this(_id: String, _name: String, _atomic: Boolean) {
    this(_id, _name)
    atomic = _atomic
  }

  /**
   * Initialize from an weapon model
   * @param m Weapon model
   */
  def this(m: WeaponModel) {
    this(m.id.toString, m.name.toString, m.atomic)
  }

  /**
   * Initialize from the result of an weapon combination
   * @param m Weapon combination model
   */
  def this(m: WeaponComboModel) {
    this
    id     = (m.result.obj.map(_.id.is.toString) openOr "")
    name   = (m.result.obj.map(_.name.is.toString) openOr "")
    atomic = (m.result.obj.map(_.atomic.is.toString) openOr "")
  }
}
