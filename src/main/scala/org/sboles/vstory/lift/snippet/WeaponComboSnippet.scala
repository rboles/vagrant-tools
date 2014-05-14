
package org.sboles.vstory.lift {

  package snippet {

    import _root_.scala.xml.{NodeSeq, Text}
    import _root_.scala.collection.mutable.Queue

    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.http._
    import _root_.net.liftweb.http.js._
    import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}

    import org.sboles.vstory.lift.lib.datatype.{Weapon => WeaponData}
    import org.sboles.vstory.lift.lib.datatype.{ItemType => ItemTypeData}
    import org.sboles.vstory.lift.lib.datatype.{Material => MaterialData}
    import org.sboles.vstory.lift.lib.datatype.ComboData

    import org.sboles.vstory.lift.model.Weapon
    import org.sboles.vstory.lift.model.WeaponCombo
    import org.sboles.vstory.lift.model.WeaponMaterialCombo
    import org.sboles.vstory.lift.model.WeaponType
    import org.sboles.vstory.lift.model.MaterialType

    import ComboSnippet._

    import Helpers._

    /**
     * Static snippet methods
     *
     * @author sboles
     */
    object WeaponComboSnippet {

      private val logger = Logger(classOf[WeaponComboSnippet])

      private val sId = "WeaponComboSnippet"

      /**
       * @param suffix ID suffix
       * @return Unique ID for contents in the control
       */
      def myId(suffix: String): String = sId+"_"+suffix

      // sequence of weapon types
      val weaponTypeSeq: Seq[(String, String)] = {
        WeaponType.findAll map { v => ( v.id.toString, v.name.toString ) }
      }

      // Map of weapon type id to name
      val weaponTypeMap: Map[String, String] = {
        weaponTypeSeq map { v => ( v._1, v._2 ) } toMap
      }

      // List of atomic weapon materials
      val weaponMaterialTypeSeq: Seq[(String, String)] = {
        MaterialType.findAll(By(MaterialType.weapon, "T"),
                             By(MaterialType.atomic, "T"),
                             OrderBy(MaterialType.strength, Ascending)) map {
                               v => ( v.id.toString, v.name.toString )
                             }
      }

      // Map of material type id to name
      val materialTypeMap: Map[String, String] = {
        weaponMaterialTypeSeq.map({ v => (v._1, v._2) }).toMap
      }

      /**
       * Get a sequence of atomic weapon items: (id, name) by weapon type ID
       * @param typeId Weapon type ID
       * @return Sequence of weapon items
       */
      def weaponItemSeq(typeId: String): Seq[(String, String)] = {
        Weapon.findAll(By(Weapon.itemType, typeId.toInt),
                      By(Weapon.atomic, "T"),
                      OrderBy(Weapon.name, Ascending)) map {
                        v => (v.id.toString, v.name.toString)
                      }
      }

      /**
       * Find a material combination based on item and material types
       * @param iA Item A type ID
       * @param iB Item B type ID
       * @param mA Material A ID
       * @param mB Material B ID
       * @return Some material data or None
       */
      def materialCombo(iA: String, iB: String,
                        mA: String, mB: String): Option[MaterialData] = {

        val combos = WeaponMaterialCombo.findAll(mA, mB, iA, iB)

        combos.length match {
          case 0 => None
          case _ => Some(new MaterialData(combos(0)))
        }
      }

      /**
       * Find an weapon combination
       * @param idA Item ID A
       * @param idB Item ID B
       * @return Some weapon item data or None if no match
       */
      def weaponCombo(a: String, b: String): Option[WeaponData] = {
        val combos = WeaponCombo.findAll(a, b)

        combos.length match {
          case 0 => None
          case _ => Some(new WeaponData(combos(0)))
        }
      }

      /**
       * Combine weapon and materials
       * @param itemIdA Weapon ID
       * @param itemIdB Weapon ID
       * @param materialIdA Material ID
       * @param materialIdB Material ID
       * @return Combination summary
       */
      def combine(itemIdA: String, itemIdB: String,
                  itemTypeIdA: String, itemTypeIdB: String,
                  materialIdA: String, materialIdB: String): ComboData = {

        val a = new WeaponData(Weapon.findAll(By(Weapon.id, itemIdA.toInt))(0))
        val b = new WeaponData(Weapon.findAll(By(Weapon.id, itemIdB.toInt))(0))

        a.itemType = new ItemTypeData(itemTypeIdA, weaponTypeMap(itemTypeIdA))
        b.itemType = new ItemTypeData(itemTypeIdB, weaponTypeMap(itemTypeIdB))

        a.material = new MaterialData(materialIdA, materialTypeMap(materialIdA))
        b.material = new MaterialData(materialIdB, materialTypeMap(materialIdB))

        val combo = new ComboData(a, b)

        weaponCombo(a.id, b.id) match {
          case Some(r) => {
            materialCombo(a.itemType.id, b.itemType.id,
                          a.material.id, b.material.id) match {
              case Some(m) => r.material = m
              case None => { }
            }
            combo.result = Some(r)
          }
          case None => { }
        }

        if ( a.id != b.id ) {
          weaponCombo(b.id, a.id) match {
            case Some(r) => {
              materialCombo(b.itemType.id, a.itemType.id,
                            b.material.id, a.material.id) match {
                case Some(m) => r.material = m
                case None =>
              }
              combo.swap = Some(r)
            }
            case None => { }
          }
        }

        combo
      }
    }

    /**
     * Provides a snippet for generating weapon combos
     *
     * @author sboles
     */
    class WeaponComboSnippet extends StatefulSnippet {

      import WeaponComboSnippet._

      // weapon type ID, A
      private var _weaponTypeA = weaponTypeSeq.head._1

      // weapon type ID, B
      private var _weaponTypeB = _weaponTypeA

      // weapon ID, A
      private var _weaponItemA = ""

      // weapon ID, B
      private var _weaponItemB = ""

      // weapon A material type ID
      private var _materialTypeA = ""

      // weapon B material type ID
      private var _materialTypeB = ""

      private var submitted = false

      def dispatch: DispatchIt = {
        case "render" => render _
      }

      def render(xhtml: NodeSeq): NodeSeq = {

        def onSubmit(): Unit = { submitted = true }

        val weapons = weaponItemSeq(_weaponTypeA)

        _weaponItemA = weapons.head._1

        _weaponItemB = _weaponItemA

        _materialTypeA = weaponMaterialTypeSeq.head._1

        _materialTypeB = _materialTypeA

        val comboItems = combine(_weaponItemA, _weaponItemB,
                                 _weaponTypeA, _weaponTypeB,
                                 _materialTypeA, _materialTypeB)

        bind("e", xhtml,
             "weapon_type_a" -> SHtml.ajaxSelect(weaponTypeSeq,
                                                Full(_weaponTypeA),
                                                onChangeWeaponTypeA(_),
                                                "id" -> myId("weaponTypeA"),
                                                 "class" -> "typeSelect"),
             
             "weapon_type_b" -> SHtml.ajaxSelect(weaponTypeSeq,
                                                Full(_weaponTypeB),
                                                onChangeWeaponTypeB(_),
                                                "id" -> myId("weaponTypeB"),
                                                 "class" -> "typeSelect"),

             "weapon_item_a" -> SHtml.ajaxSelect(weapons,
                                                Full(_weaponItemA),
                                                onChangeWeaponItemA(_),
                                                "id" -> myId("weaponItemA"),
                                                 "class" -> "itemSelect"),

             "weapon_item_b" -> SHtml.ajaxSelect(weapons,
                                                Full(_weaponItemB),
                                                onChangeWeaponItemB(_),
                                                "id" -> myId("weaponItemB"),
                                                 "class" -> "itemSelect"),

             "material_type_a" -> SHtml.ajaxSelect(weaponMaterialTypeSeq,
                                                   Full(_materialTypeA),
                                                   onChangeMaterialTypeA(_),
                                                   "id" -> myId("weaponMaterialTypeA"),
                                                   "class" -> "materialSelect"),

             "material_type_b" -> SHtml.ajaxSelect(weaponMaterialTypeSeq,
                                                   Full(_materialTypeB),
                                                   onChangeMaterialTypeB(_),
                                                   "id" -> myId("weaponMaterialTypeB"),
                                                   "class" -> "materialSelect"),

             "result" -> SHtml.span(ComboSnippet.toResult(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("result")),

             "summary" -> SHtml.span(ComboSnippet.toSummary(comboItems),
                                     JsCmds.Noop,
                                     "id" -> myId("summary")))
      }

      /**
       * Update the item select to match the new type, update the default
       * item to match the first element in the item select, update the
       * summary to match the new default items.
       * @param v Weapon type
       * @return JS replace commands
       */
      def onChangeWeaponTypeA(v: String): JsCmd = {
        Thread.sleep(400)

        _weaponTypeA = v

        val weapons = weaponItemSeq(_weaponTypeA)

        _weaponItemA = weapons.head._1

        val comboItems = combine(_weaponItemA, _weaponItemB,
                                 _weaponTypeA, _weaponTypeB,
                                 _materialTypeA, _materialTypeB)

        JsCmds.seqJsToJs(List(
          JsCmds.Replace(myId("weaponItemA"),
                         SHtml.ajaxSelect(weapons,
                                          Full(_weaponItemA),
                                          onChangeWeaponItemA(_),
                                          "id" -> myId("weaponItemA"),
                                          "class" -> "itemSelect")),
          JsCmds.Replace(myId("result"),
                         SHtml.span(ComboSnippet.toResult(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("result"))),
          JsCmds.Replace(myId("summary"),
                         SHtml.span(ComboSnippet.toSummary(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("summary")))))
      }

      /**
       * Update the item select to match the new type, update the default
       * item to match the first element in the item select, update the
       * summary to match the new default items.
       * @param v Weapon type
       * @return JS replace commands
       */
      def onChangeWeaponTypeB(v: String): JsCmd = {
        Thread.sleep(400)

        _weaponTypeB = v
        
        val seq = weaponItemSeq(_weaponTypeB)

        _weaponItemB = seq.head._1

        val comboItems = combine(_weaponItemA, _weaponItemB,
                                 _weaponTypeA, _weaponTypeB,
                                 _materialTypeA, _materialTypeB)

        JsCmds.seqJsToJs(List(
          JsCmds.Replace(myId("weaponItemB"),
                         SHtml.ajaxSelect(seq,
                                          Full(_weaponItemB),
                                          onChangeWeaponItemB(_),
                                          "id" -> myId("weaponItemB"),
                                          "class" -> "itemSelect")),
          JsCmds.Replace(myId("result"),
                         SHtml.span(ComboSnippet.toResult(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("result"))),
          JsCmds.Replace(myId("summary"),
                         SHtml.span(ComboSnippet.toSummary(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("summary")))))
      }

      /**
       */
      def onChangeWeapon: JsCmd = {
        Thread.sleep(400)

        val comboItems = combine(_weaponItemA, _weaponItemB,
                                 _weaponTypeA, _weaponTypeB,
                                 _materialTypeA, _materialTypeB)

        JsCmds.seqJsToJs(List(
          JsCmds.Replace(myId("result"),
                         SHtml.span(ComboSnippet.toResult(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("result"))),
          JsCmds.Replace(myId("summary"),
                         SHtml.span(ComboSnippet.toSummary(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("summary")))))
      }

      def onChangeWeaponItemA(v: String): JsCmd = {
        _weaponItemA = v
        onChangeWeapon
      }

      def onChangeWeaponItemB(v: String): JsCmd = {
        _weaponItemB = v
        onChangeWeapon
      }

      def onChangeMaterialTypeA(v: String): JsCmd = {
        _materialTypeA = v
        onChangeWeapon
      }

      def onChangeMaterialTypeB(v: String): JsCmd = {
        _materialTypeB = v
        onChangeWeapon
      }
    }
  }
}
