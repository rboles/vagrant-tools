
package org.sboles.vstory.lift.model

import net.liftweb.json.JsonAST.JObject
import net.liftweb.json.JsonDSL._
import scala.xml.Elem

import net.liftweb.mapper._

class Weapon extends LongKeyedMapper[Weapon] {

  def getSingleton = Weapon

  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 40)
  object atomic extends MappedString(this, 1)
  object itemType extends MappedLongForeignKey(this, WeaponType) {
    override def dbColumnName = "type"
  }

  def toJson: JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("atomic" -> atomic.toString) ~
    ("weapon_type" -> (itemType.obj.map(_.toJson) openOr new JObject(Nil)))
  }

  override def toXml: Elem = {
    val typeId:String = (itemType.obj.map(_.id.is.toString) openOr "")
    val typeName:String = (itemType.obj.map(_.name.is.toString) openOr "")

    <weapon id={id.toString}>
    <name>{name.toString}</name>
    <atomic>{atomic.toString}</atomic>
    <weapon_type id={typeId}>{typeName}</weapon_type>
    </weapon>
  }

  def toMinJson: JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString)
  }

  def toMinXml: Elem = {
    <weapon id={id.toString}>{name.toString}</weapon>
  }

  def toMinJson(slot: String): JObject = {
    ("id" -> id.toString) ~
    ("name" -> name.toString) ~
    ("slot" -> slot)
  }

  def toMinXml(slot: String): Elem = {
    <weapon id={id.toString} slot={slot}>{name.toString}</weapon>
  }
}

object Weapon extends Weapon with LongKeyedMetaMapper[Weapon]
{
  override def dbTableName = "weapon"
}

