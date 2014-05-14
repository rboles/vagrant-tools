
package org.sboles.vstory.lift.model

import net.liftweb.json.JsonAST.{JArray,JObject}
import net.liftweb.json.JsonDSL._
import scala.xml.Elem

import net.liftweb.mapper._

/**
 * Models an armor combination
 *
 * @author sboles
 */
class ArmorCombo extends LongKeyedMapper[ArmorCombo] {

  def getSingleton = ArmorCombo

  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object armor1 extends MappedLongForeignKey(this, Armor) {
    override def dbColumnName = "armor_1"
  }

  object armor2 extends MappedLongForeignKey(this, Armor) {
    override def dbColumnName = "armor_2"
  }

  object result extends MappedLongForeignKey(this, Armor)

  def toJson: JObject = {
    val items = List(
      (armor1.obj.map(_.toMinJson("1")) openOr JObject(Nil)),
      (armor2.obj.map(_.toMinJson("2")) openOr JObject(Nil))
    )
    val r = (result.obj.map(_.toMinJson) openOr new JObject(Nil))

    ("armor_items" -> new JArray(items)) ~
    ("results" -> r)
  }

  override def toXml: Elem = {
    val items = List(
      (armor1.obj.map(_.toMinXml("1")) openOr <armor slot="1" />),
      (armor2.obj.map(_.toMinXml("2")) openOr <armor slot="2" />)
    )
    
    val r = (result.obj.map(_.toMinXml) openOr <armor />)

    <combination>
    <armor_items>{items}</armor_items>
    <results>{r}</results>
    </combination>
  }
}

object ArmorCombo extends ArmorCombo with LongKeyedMetaMapper[ArmorCombo]
{
  override def dbTableName = "armor_combo"

  /**
   * @param a Armor item in slot 1
   */
  def findAll(a: Int): List[ArmorCombo] =
    ArmorCombo.findAll(By(armor1, a))

  /**
   * @param a Armor item in slot 1
   */
  def findAll(a: String): List[ArmorCombo] =
    findAll(a.toInt)

  /**
   * @param a Armor item in slot 1
   * @param b Armor item in slot 2
   */
  def findAll(a: Int, b: Int): List[ArmorCombo] =
    ArmorCombo.findAll(By(armor1, a), By(armor2, b))

  /**
   * @param a Armor item in slot 1
   * @param b Armor item in slot 2
   */
  def findAll(a: String, b: String): List[ArmorCombo] =
    findAll(a.toInt, b.toInt)

  /**
   * @param a Optional armor item in slot 1
   * @param b Optional armor item in slot 2
   */
  def findAll(a: Option[String], b: Option[String]): List[ArmorCombo] = {
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
