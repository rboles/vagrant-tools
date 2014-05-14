
package org.sboles.vstory.lift.lib.rest

import scala.xml.NodeSeq
import scala.collection.mutable.Queue

import net.liftweb.common.{Box, Full, Logger}
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.mapper.By

import org.sboles.vstory.lift.model._

object VagrantRest extends RestHelper {

  private val logger = Logger(classOf[VagrantRest])

  serve {
    case Req("api" :: "armor_types" :: _, "xml",
             GetRequest) => armorTypesToXml

    case Req("api" :: "material_types" :: _, "xml",
             GetRequest) => materialTypesToXml

    case Req("api" :: "weapon_types" :: _, "xml",
             GetRequest) => weaponTypesToXml

    case Req("api" :: "armor_items" :: _, "xml",
             GetRequest) => armorToXml

    case Req("api" :: "weapon_items" :: _, "xml",
             GetRequest) => weaponToXml

    case Req("api" :: "armor_material_combo" :: _, "xml",
             GetRequest) => armorMaterialComboToXml

    case Req("api" :: "weapon_material_combo" :: _, "xml",
             GetRequest) => weaponMaterialComboToXml

    case Req("api" :: "armor_combo" :: _, "xml",
             GetRequest) => armorComboToXml

    case Req("api" :: "weapon_combo" :: _, "xml",
             GetRequest) => weaponComboToXml
  }

  def materialTypesToXml: LiftResponse = {
    val hasId    = S.param("id").toOption
    val isAtomic = S.param("atomic").toOption
    val isArmor  = S.param("armor").toOption
    val isShield = S.param("shield").toOption
    val isWeapon = S.param("weapon").toOption

    val results = hasId match {
      case Some(v) => MaterialType.findAll(By(MaterialType.id, v.toInt))
      case _ => MaterialType.findAll
    }

    val materials = results filter { m => {
      (hasId match {
        case Some(v) => m.id.toString == v
        case _ => true
      }) &&
      (isAtomic match {
        case Some(v) => m.atomic.toString == v.toUpperCase
        case _ => true
      }) &&
      (isArmor match {
        case Some(v) => m.armor.toString == v.toUpperCase
        case _ => true
      }) &&
      (isWeapon match {
        case Some(v) => m.weapon.toString == v.toUpperCase
        case _ => true
      }) &&
      (isShield match {
        case Some(v) => m.shield.toString == v.toUpperCase
        case _ => true
      })
    } }

    <material_types>{ materials map { _.toXml } }</material_types>
  }

  def armorTypesToXml: LiftResponse = {
    val hasId = S.param("id").toOption

    val results = hasId match {
      case Some(v) => ArmorType.findAll(By(ArmorType.id, v.toInt))
      case _ => ArmorType.findAll
    }

    <armor_types>{ results map { _.toXml } }</armor_types>
  }

  def weaponTypesToXml: LiftResponse = {
    val hasId = S.param("id").toOption

    val results = hasId match {
      case Some(v) => WeaponType.findAll(By(WeaponType.id, v.toInt))
      case _ => WeaponType.findAll
    }

    <weapon_types>{ results map { _.toXml } }</weapon_types>
  }

  def armorToXml: LiftResponse = {
    val hasId = S.param("id").toOption
    val hasType = S.param("type_id").toOption
    val isAtomic = S.param("atomic").toOption

    val results = {
      hasId match {
        case Some(v) => Armor.findAll(By(Armor.id, v.toInt))
        case None => hasType match {
          case Some(v) => Armor.findAll(By(Armor.itemType, v.toInt))
          case None => isAtomic match {
            case Some(v) => Armor.findAll(By(Armor.atomic, v.toUpperCase))
            case None => Armor.findAll
          }
        }
      }
    }

    val items = results filter { r => {
      (hasId match {
        case Some(v) => r.id.toString == v
        case _ => true
      }) &&
      (hasType match {
        case Some(v) => r.itemType.toString == v
        case _ => true
      }) &&
      (isAtomic match {
        case Some(v) => r.atomic.toString == v.toUpperCase
        case _ => true
      })
    } }

    <armor_items>{ items map { _.toXml } }</armor_items>
  }

  def weaponToXml: LiftResponse = {
    val hasId = S.param("id").toOption
    val hasType = S.param("type_id").toOption
    val isAtomic = S.param("atomic").toOption

    val results = {
      hasId match {
        case Some(v) => Weapon.findAll(By(Weapon.id, v.toInt))
        case None => hasType match {
          case Some(v) => Weapon.findAll(By(Weapon.itemType, v.toInt))
          case None => isAtomic match {
            case Some(v) => Weapon.findAll(By(Weapon.atomic, v.toUpperCase))
            case None => Weapon.findAll
          }
        }
      }
    }

    val items = results filter { r => {
      (hasId match {
        case Some(v) => r.id.toString == v
        case _ => true
      }) &&
      (hasType match {
        case Some(v) => r.itemType.toString == v
        case _ => true
      }) &&
      (isAtomic match {
        case Some(v) => r.atomic.toString == v.toUpperCase
        case _ => true
      })
    } }

    <weapon_items>{ items map { _.toXml } }</weapon_items>
  }

  def armorMaterialComboToXml: LiftResponse = {
    val m1 = S.param("m1").toOption
    val m2 = S.param("m2").toOption
    val hasT1 = S.param("t1").toOption
    val hasT2 = S.param("t2").toOption

    val results = ArmorMaterialCombo.findAll(m1, m2)

    val items = results filter { r => {
      (hasT1 match {
        case Some(v) => r.t1.toString == v
        case _ => true
      }) &&
      (hasT2 match {
        case Some(v) => r.t2.toString == v
        case _ => true
      })
    } }

    <armor_material_combinations>{
      items map { _.toXml }
    }</armor_material_combinations>
  }

  def weaponMaterialComboToXml: LiftResponse = {
    val m1 = S.param("m1").toOption
    val m2 = S.param("m2").toOption
    val hasT1 = S.param("t1").toOption
    val hasT2 = S.param("t2").toOption

    val results = WeaponMaterialCombo.findAll(m1, m2)

    val items = results filter { r => {
      (hasT1 match {
        case Some(v) => r.t1.toString == v
        case _ => true
      }) &&
      (hasT2 match {
        case Some(v) => r.t2.toString == v
        case _ => true
      })
    } }

    <weapon_material_combinations>{
      items map { _.toXml }
    }</weapon_material_combinations>
  }

  def armorComboToXml: LiftResponse = {
    val a = S.param("a1").toOption
    val b = S.param("a2").toOption

    val results = ArmorCombo.findAll(a, b)

    <armor_combinations>{
      results map { _.toXml }
    }</armor_combinations>
  }

  def weaponComboToXml: LiftResponse = {
    val a = S.param("w1").toOption
    val b = S.param("w2").toOption

    val results = WeaponCombo.findAll(a, b)

    <weapon_combinations>{
      results map { _.toXml }
    }</weapon_combinations>
  }
}

class VagrantRest { }
