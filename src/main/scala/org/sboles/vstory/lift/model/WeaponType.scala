
package org.sboles.vstory.lift.model

import scala.xml.Elem

import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonDSL._
import net.liftweb.mapper._

class WeaponType extends LongKeyedMapper[WeaponType] {

  def getSingleton = WeaponType

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 45)

  def toJson: JObject = {
    ("id" -> id.toString) ~ ("name" -> name.toString)
  }

  override def toXml: Elem = {
    <weapon_type id={id.toString}>{name.toString}</weapon_type>
  }

  def toJson(slot: String): JObject = {
    ("id" -> id.toString) ~ ("name" -> name.toString) ~ ("slot" -> slot)
  }

  def toXml(slot: String): Elem = {
    <weapon_type id={id.toString} slot={slot}>{
      name.toString
    }</weapon_type>
  }
}

object WeaponType extends WeaponType with LongKeyedMetaMapper[WeaponType] {
  override def dbTableName = "weapon_type"
}

