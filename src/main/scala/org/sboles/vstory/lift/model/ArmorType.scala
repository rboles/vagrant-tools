
package org.sboles.vstory.lift.model

import scala.xml.Elem

import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonDSL._
import net.liftweb.mapper._

class ArmorType extends LongKeyedMapper[ArmorType] {

  def getSingleton = ArmorType

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 45)

  def toJson: JObject = {
    ("id" -> id.toString) ~ ("name" -> name.toString)
  }

  override def toXml: Elem = {
    <armor_type id={id.toString}>{name.toString}</armor_type>
  }

  def toJson(slot: String): JObject = {
    ("id" -> id.toString) ~ ("name" -> name.toString) ~ ("slot" -> slot)
  }

  def toXml(slot: String): Elem = {
    <armor_type id={id.toString} slot={slot}>{
      name.toString
    }</armor_type>
  }
}

object ArmorType extends ArmorType with LongKeyedMetaMapper[ArmorType] {
  override def dbTableName = "armor_type"
}

