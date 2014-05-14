
package org.sboles.vstory.lift.model

import net.liftweb.json.JsonAST.{JArray,JObject}
import net.liftweb.json.JsonDSL._
import scala.xml.Elem

import net.liftweb.mapper._

/**
 * Models weapon material combinations
 *
 * @author sboles
 */
class WeaponMaterialCombo extends LongKeyedMapper[WeaponMaterialCombo] {

  def getSingleton = WeaponMaterialCombo

  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object m1 extends MappedLongForeignKey(this, MaterialType) {
    override def dbColumnName = "material_1"
  }

  object m2 extends MappedLongForeignKey(this, MaterialType) {
    override def dbColumnName = "material_2"
  }

  object t1 extends MappedLongForeignKey(this, WeaponType) {
    override def dbColumnName = "object_type_1"
  }

  object t2 extends MappedLongForeignKey(this, WeaponType) {
    override def dbColumnName = "object_type_2"
  }

  object result extends MappedLongForeignKey(this, MaterialType)

  def toJson: JObject = {
    val materials = List(
      (m1.obj.map(_.toJson("1")) openOr new JObject(Nil)),
      (m2.obj.map(_.toJson("2")) openOr new JObject(Nil))
    )
    val types = List(
      (t1.obj.map(_.toJson("1")) openOr new JObject(Nil)),
      (t2.obj.map(_.toJson("2")) openOr new JObject(Nil))
    )
    val r = (result.obj.map(_.toMinJson) openOr new JObject(Nil))

    ("material_types" -> new JArray(materials)) ~
    ("weapon_types" -> new JArray(types)) ~
    ("results" -> r)
  }

  override def toXml: Elem = {
    val materials = List(
      (m1.obj.map(_.toXml("1")) openOr <material_type slot="1" />),
      (m2.obj.map(_.toXml("2")) openOr <material_type slot="2" />)
    )
    val types = List(
      (t1.obj.map(_.toXml("1")) openOr <weapon_type slot="1" />),
      (t2.obj.map(_.toXml("2")) openOr <weapon_type slot="2" />)
    )
    val r = (result.obj.map(_.toMinXml) openOr <material_type />)

    <combination>
    <material_types>{materials}</material_types>
    <weapon_types>{types}</weapon_types>
    <results>{r}</results>
    </combination>
  }
}

object WeaponMaterialCombo extends WeaponMaterialCombo with LongKeyedMetaMapper[WeaponMaterialCombo]
{
  override def dbTableName = "material_combo_weapon"

  /**
   * @param a Material type ID
   * @return List of material type combinations where m1 is in first slot
   */
  def findAll(a: Int): List[WeaponMaterialCombo] =
    WeaponMaterialCombo.findAll(By(m1, a))

  /**
   * @param a Material type ID
   * @return List of material type combinations where m1 is in first slot
   */
  def findAll(a: String): List[WeaponMaterialCombo] =
    findAll(a.toInt)

  /**
   * @param a Material type ID
   * @param b Material type ID
   * @return List of material type combinations where m1 is in first slot
   * and m2 is in the second slot
   */
  def findAll(a: Int, b: Int): List[WeaponMaterialCombo] =
    WeaponMaterialCombo.findAll(By(m1, a), By(m2, b))

  /**
   * @param a Material type ID
   * @param b Material type ID
   * @return List of material type combinations where m1 is in first slot
   * and m2 is in the second slot
   */
  def findAll(a: String, b: String): List[WeaponMaterialCombo] =
    findAll(a.toInt, b.toInt)

  /**
   * Find combination by material and type
   * @param a Material type ID
   * @param b Material type ID
   * @param c Weapon type ID
   * @param d Weapon type ID
   * @return List of material type combinations
   */
  def findAll(a: Int, b: Int, c: Int, d: Int): List[WeaponMaterialCombo] =
    WeaponMaterialCombo.findAll(By(m1, a), By(m2, b), By(t1, c), By(t2, d))

  /**
   * Find combination by material and type
   * @param a Material type ID
   * @param b Material type ID
   * @param c Weapon type ID
   * @param d Weapon type ID
   * @return List of material type combinations
   */
  def findAll(a: String, b: String, c: String, d: String): List[WeaponMaterialCombo] =
    findAll(a.toInt, b.toInt, c.toInt, d.toInt)

  /**
   * @param a Material type ID in slot 1 (optional)
   * @param b Material type ID in slot 2 (optional)
   * @return List of material type combinations determined by the content
   * of slots 1 and 2.
   */
  def findAll(a: Option[String], b: Option[String]): List[WeaponMaterialCombo] = {
    a match {
      case Some(mA) => b match {
        case Some(mB) => findAll(mA, mB)
        case None => findAll(mA)
      }
      case None => b match {
        case Some(mB) => findAll(mB)
          case None => List()
      }
    }
  }
}
