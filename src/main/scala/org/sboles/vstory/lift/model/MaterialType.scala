
package org.sboles.vstory.lift.model

import scala.xml.Elem

import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonDSL._
import net.liftweb.mapper._

class MaterialType extends LongKeyedMapper[MaterialType] {

  def getSingleton = MaterialType

  def primaryKeyField = id

  object id       extends MappedLongIndex(this)
  object name     extends MappedString(this, 45)
  object short    extends MappedString(this, 10)
  object atomic   extends MappedString(this, 1)
  object strength extends MappedInt(this) 
  object armor    extends MappedString(this, 1)
  object weapon   extends MappedString(this, 1)
  object shield   extends MappedString(this, 1)

  def toJson: JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("short" -> short.toString) ~
    ("atomic" -> atomic.toString) ~
    ("strength" -> strength.toString) ~
    ("armor" -> armor.toString) ~
    ("weapon" -> weapon.toString) ~
    ("shield" -> shield.toString)
  }

  override def toXml: Elem = {
    <material_type id={id.toString}>
    <name>{name.toString}</name>
    <short>{short.toString}</short>
    <atomic>{atomic.toString}</atomic>
    <strength>{strength.toString}</strength>
    <armor>{armor.toString}</armor>
    <weapon>{weapon.toString}</weapon>
    <shield>{shield.toString}</shield>
    </material_type>
  }

  def toJson(slot: String): JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("slot" -> slot)
  }

  def toXml(slot: String): Elem = {
    <material_type id={id.toString} slot={slot}>{
      name.toString
    }</material_type>
  }

  def toMinJson: JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString)
  }

  def toMinJson(slot: String): JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("slot" -> slot)
  }

  def toMinXml: Elem = {
    <material_type id={id.toString}>{
      name.toString
    }</material_type>
  }

}

object MaterialType extends MaterialType with LongKeyedMetaMapper[MaterialType] {
  override def dbTableName = "material_type"
}

