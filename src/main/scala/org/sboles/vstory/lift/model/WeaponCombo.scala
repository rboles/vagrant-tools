
package org.sboles.vstory.lift.model

import net.liftweb.json.JsonAST.{JArray,JObject}
import net.liftweb.json.JsonDSL._
import scala.xml.Elem

import net.liftweb.mapper._

class WeaponCombo extends LongKeyedMapper[WeaponCombo] {

  def getSingleton = WeaponCombo

  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object weapon1 extends MappedLongForeignKey(this, Weapon) {
    override def dbColumnName = "weapon_1"
  }

  object weapon2 extends MappedLongForeignKey(this, Weapon) {
    override def dbColumnName = "weapon_2"
  }

  object result extends MappedLongForeignKey(this, Weapon)

  def toJson: JObject = {
    val items = List(
      (weapon1.obj.map(_.toMinJson("1")) openOr JObject(Nil)),
      (weapon2.obj.map(_.toMinJson("2")) openOr JObject(Nil))
    )
    val r = (result.obj.map(_.toMinJson) openOr new JObject(Nil))

    ("weapon_items" -> new JArray(items)) ~
    ("results" -> r)
  }

  override def toXml: Elem = {
    val items = List(
      (weapon1.obj.map(_.toMinXml("1")) openOr <weapon slot="1" />),
      (weapon2.obj.map(_.toMinXml("2")) openOr <weapon slot="2" />)
    )
    
    val r = (result.obj.map(_.toMinXml) openOr <weapon />)

    <combination>
    <weapon_items>{items}</weapon_items>
    <results>{r}</results>
    </combination>
  }
}

object WeaponCombo extends WeaponCombo with LongKeyedMetaMapper[WeaponCombo]
{
  override def dbTableName = "weapon_combo"

  /**
   * @param a Weapon item in slot 1
   */
  def findAll(a: Int): List[WeaponCombo] =
    WeaponCombo.findAll(By(weapon1, a))

  /**
   * @param a Weapon item in slot 1
   */
  def findAll(a: String): List[WeaponCombo] =
    findAll(a.toInt)

  /**
   * @param a Weapon item in slot 1
   * @param b Weapon item in slot 2
   */
  def findAll(a: Int, b: Int): List[WeaponCombo] =
    WeaponCombo.findAll(By(weapon1, a), By(weapon2, b))

  /**
   * @param a Weapon item in slot 1
   * @param b Weapon item in slot 2
   */
  def findAll(a: String, b: String): List[WeaponCombo] =
    findAll(a.toInt, b.toInt)

  /**
   * @param a Optional weapon item in slot 1
   * @param b Optional weapon item in slot 2
   */
  def findAll(a: Option[String], b: Option[String]): List[WeaponCombo] = {
    a match {
      case Some(iA) => b match {
        case Some(iB) => findAll(iA, iB)
        case None => findAll(iA)
      }
      case None => b match {
        case Some(iB) => findAll(iB)
          case None => List()
      }
    }
  }
}
