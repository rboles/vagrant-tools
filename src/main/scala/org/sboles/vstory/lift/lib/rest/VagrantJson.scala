
package org.sboles.vstory.lift.lib.rest

import net.liftweb.common.{Box, Full, Logger}
import net.liftweb.json.JsonAST._
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.mapper.By

import org.sboles.vstory.lift.model._

object VagrantJson extends RestHelper {

  private val logger = Logger(classOf[VagrantJson])

  serve {
    case Req("api" :: "armor_types" :: _, "json",
             GetRequest) => armorTypesToJson

    case Req("api" :: "material_types" :: _, "json",
             GetRequest) => materialTypesToJson

    case Req("api" :: "weapon_types" :: _, "json",
             GetRequest) => weaponTypesToJson

    case Req("api" :: "armor_items" :: _, "json",
             GetRequest) => armorToJson

    case Req("api" :: "weapon_items" :: _, "json",
             GetRequest) => weaponToJson

    case Req("api" :: "armor_material_combo" :: _, "json",
             GetRequest) => armorMaterialComboToJson

    case Req("api" :: "weapon_material_combo" :: _, "json",
             GetRequest) => weaponMaterialComboToJson

    case Req("api" :: "armor_combo" :: _, "json",
             GetRequest) => armorComboToJson

    case Req("api" :: "weapon_combo" :: _, "json",
             GetRequest) => weaponComboToJson
  }

  def armorTypesToJson: LiftResponse = {
    val hasId = S.param("id").toOption

    val results = hasId match {
      case Some(v) => ArmorType.findAll(By(ArmorType.id, v.toInt))
      case _ => ArmorType.findAll
    }

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def materialTypesToJson: LiftResponse = {
    val hasId    = S.param("id").toOption
    val isAtomic = S.param("atomic").toOption
    val isArmor  = S.param("armor").toOption
    val isShield = S.param("shield").toOption
    val isWeapon = S.param("weapon").toOption

    val materials = hasId match {
      case Some(v) => MaterialType.findAll(By(MaterialType.id, v.toInt))
      case _ => MaterialType.findAll
    }

    val results = materials filter { m => {
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

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def weaponTypesToJson: LiftResponse = {
    val hasId = S.param("id").toOption

    val results = hasId match {
      case Some(v) => WeaponType.findAll(By(WeaponType.id, v.toInt))
      case _ => WeaponType.findAll
    }

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def armorToJson: LiftResponse = {
    val hasId = S.param("id").toOption
    val hasType = S.param("type_id").toOption
    val isAtomic = S.param("atomic").toOption

    val items = {
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

    val results = items filter { r => {
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

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def weaponToJson: LiftResponse = {
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

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def armorMaterialComboToJson: LiftResponse = {
    val m1 = S.param("m1").toOption
    val m2 = S.param("m2").toOption
    val hasT1 = S.param("t1").toOption
    val hasT2 = S.param("t2").toOption

    val combos = ArmorMaterialCombo.findAll(m1, m2)

    val results = combos filter { r => {
      (hasT1 match {
        case Some(v) => r.t1.toString == v
        case _ => true
      }) &&
      (hasT2 match {
        case Some(v) => r.t2.toString == v
        case _ => true
      })
    } }

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def weaponMaterialComboToJson: LiftResponse = {
    val m1 = S.param("m1").toOption
    val m2 = S.param("m2").toOption
    val hasT1 = S.param("t1").toOption
    val hasT2 = S.param("t2").toOption

    val combos = WeaponMaterialCombo.findAll(m1, m2)

    val results = combos filter { r => {
      (hasT1 match {
        case Some(v) => r.t1.toString == v
        case _ => true
      }) &&
      (hasT2 match {
        case Some(v) => r.t2.toString == v
        case _ => true
      })
    } }

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def armorComboToJson: LiftResponse = {
    val a = S.param("a1").toOption
    val b = S.param("a2").toOption

    val results = ArmorCombo.findAll(a, b)

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }

  def weaponComboToJson: LiftResponse = {
    val a = S.param("w1").toOption
    val b = S.param("w2").toOption

    val results = WeaponCombo.findAll(a, b)

    JsonResponse.apply(new JArray(results.map(_.toJson)))
  }
}

class VagrantJson { }
