
package org.sboles.vstory.lift.lib.datatype

import org.sboles.vstory.lift.model.MaterialType
import org.sboles.vstory.lift.model.ArmorMaterialCombo
import org.sboles.vstory.lift.model.WeaponMaterialCombo

/**
 * Provides a container for material type data
 *
 * @author sboles
 */
class Material {

  var id: String       = ""
  var name: String     = ""
  var short: String    = ""
  var strength: String = ""
  private var _atomic: Boolean = true
  private var _armor: Boolean  = false
  private var _weapon: Boolean = false
  private var _shield: Boolean = false

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

  def this(_id: String, _name: String, atomic: Boolean) {
    this(_id, _name)
    _atomic = atomic
  }

  /**
   * Initialize from material type model
   * @param m Material type model
   */
  def this(m: MaterialType) {
    this(m.id.toString, m.name.toString, m.atomic.toString)
    short    = m.short.toString
    strength = m.strength.toString
    armor    = m.armor.toString
    weapon   = m.weapon.toString
    shield   = m.shield.toString
  }

  /**
   * Initialize from result of an armor material combo
   * @param m Armor material combo
   */
  def this(m: ArmorMaterialCombo) {
    this
    id       = (m.result.obj.map(_.id.is.toString) openOr "")
    name     = (m.result.obj.map(_.name.is.toString) openOr "")
    atomic   = (m.result.obj.map(_.atomic.is.toString) openOr "")
    short    = (m.result.obj.map(_.short.is.toString) openOr "")
    strength = (m.result.obj.map(_.strength.is.toString) openOr "")
    armor    = (m.result.obj.map(_.armor.is.toString) openOr "")
    weapon   = (m.result.obj.map(_.weapon.is.toString) openOr "")
    shield   = (m.result.obj.map(_.shield.is.toString) openOr "")
  }

  /**
   * Initialize from result of an weapon material combo
   * @param m Weapon material combo
   */
  def this(m: WeaponMaterialCombo) {
    this
    id       = (m.result.obj.map(_.id.is.toString) openOr "")
    name     = (m.result.obj.map(_.name.is.toString) openOr "")
    atomic   = (m.result.obj.map(_.atomic.is.toString) openOr "")
    short    = (m.result.obj.map(_.short.is.toString) openOr "")
    strength = (m.result.obj.map(_.strength.is.toString) openOr "")
    armor    = (m.result.obj.map(_.armor.is.toString) openOr "")
    weapon   = (m.result.obj.map(_.weapon.is.toString) openOr "")
    shield   = (m.result.obj.map(_.shield.is.toString) openOr "")
  }

  /**
   * @param True if material is atomic, false if not
   */
  def atomic_=(v: String): Unit = {
    _atomic = v.substring(0,1).toLowerCase match {
      case "t" => true
      case _ => false
    }
  }

  /**
   * @return True if material is atomic; false if not
   */
  def atomic: Boolean = _atomic

  /**
   * @param True if material is armor, false if not
   */
  def armor_=(v: String): Unit = {
    _armor = v.substring(0,1).toLowerCase match {
      case "t" => true
      case _ => false
    }
  }

  /**
   * @param True if material is armor; false if not
   */
  def armor_=(v: Boolean): Unit = _armor = v

  /**
   * @return True if material is armor; false if not
   */
  def armor: Boolean = _armor

  /**
   * @param True if material is weapon, false if not
   */
  def weapon_=(v: String): Unit = {
    _weapon = v.substring(0,1).toLowerCase match {
      case "t" => true
      case _ => false
    }
  }

  /**
   * @return True if material is weapon; false if not
   */
  def weapon_=(v: Boolean): Unit = _weapon = v

  /**
   * @param True if material is weapon; false if not
   */
  def weapon: Boolean = _armor

  /**
   * @param True if material is shield, false if not
   */
  def shield_=(v: String): Unit = {
    _shield = v.substring(0,1).toLowerCase match {
      case "t" => true
      case _ => false
    }
  }

  /**
   * @param True if material is weapon; false if not
   */
  def shield_=(v: Boolean): Unit = _shield = v

  /**
   * @return True if material is shield; false if not
   */
  def shield: Boolean = _armor

  /**
   * @return Tuple of id, name
   */
  def toTuple: (String, String) = {
    (id, name)
  }
}
