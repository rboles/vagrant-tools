
package org.sboles.vstory.lift.model

import scala.xml.Elem

import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonDSL._
import net.liftweb.mapper._

class Armor extends LongKeyedMapper[Armor] {

  def getSingleton = Armor

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 40)
  object atomic extends MappedString(this, 1)
  object itemType extends MappedLongForeignKey(this, ArmorType) {
    override def dbColumnName = "type"
  }

  def toJson: JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("atomic" -> atomic.toString) ~
    ("armor_type" -> (itemType.obj.map(_.toJson) openOr new JObject(Nil)))
  }

  override def toXml: Elem = {
    val typeId:String = (itemType.obj.map(_.id.is.toString) openOr "")
    val typeName:String = (itemType.obj.map(_.name.is.toString) openOr "")

    <armor id={id.toString}>
      <name>{name.toString}</name>
      <atomic>{atomic.toString}</atomic>
      <armor_type id={typeId}>{typeName}</armor_type>
    </armor>
  }

  def toMinJson: JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString)
  }

  def toMinXml: Elem = {
    <armor id={id.toString}>{name.toString}</armor>
  }

  def toMinJson(slot: String): JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("slot" -> slot)
  }

  def toMinXml(slot: String): Elem = {
    <armor id={id.toString} slot={slot}>{name.toString}</armor>
  }
}

object Armor extends Armor with LongKeyedMetaMapper[Armor]
{
  override def dbTableName = "armor"
}
