
package org.sboles.vstory.lift {

  package snippet {

    import _root_.scala.xml.{NodeSeq, Text}
    import _root_.scala.collection.mutable.Queue

    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.http._
    import _root_.net.liftweb.http.js._
    import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}

    import org.sboles.vstory.lift.lib.datatype.{Armor => ArmorData}
    import org.sboles.vstory.lift.lib.datatype.{ItemType => ItemTypeData}
    import org.sboles.vstory.lift.lib.datatype.{Material => MaterialData}
    import org.sboles.vstory.lift.lib.datatype.ComboData

    import org.sboles.vstory.lift.model.Armor
    import org.sboles.vstory.lift.model.ArmorCombo
    import org.sboles.vstory.lift.model.ArmorMaterialCombo
    import org.sboles.vstory.lift.model.ArmorType
    import org.sboles.vstory.lift.model.MaterialType

    import ComboSnippet._

    import Helpers._

    /**
     * Static snippet methods
     *
     * @author sboles
     */
    object ArmorComboSnippet {

      private val logger = Logger(classOf[ArmorComboSnippet])

      private val sId = "ArmorComboSnippet"

      /**
       * @param suffix ID suffix
       * @return Unique ID for contents in the control
       */
      def myId(suffix: String): String = sId+"_"+suffix

      // sequence of armor types
      val armorTypeSeq: Seq[(String, String)] = {
        ArmorType.findAll map { v => ( v.id.toString, v.name.toString ) }
      }

      // Map of armor type id to name
      val armorTypeMap: Map[String, String] = {
        armorTypeSeq map { v => ( v._1, v._2 ) } toMap
      }

      // List of atomic armor materials
      val armorMaterialTypeSeq: Seq[(String, String)] = {
        MaterialType.findAll(By(MaterialType.armor, "T"),
                             By(MaterialType.atomic, "T"),
                             OrderBy(MaterialType.strength, Ascending)) map {
                               v => ( v.id.toString, v.name.toString )
                             }
      }

      // List of atomic shield materials
      val shieldMaterialTypeSeq: Seq[(String, String)] = {
        MaterialType.findAll(By(MaterialType.shield, "T"),
                             By(MaterialType.atomic, "T"),
                             OrderBy(MaterialType.strength, Ascending)) map {
                               v => (v.id.toString, v.name.toString)
                             }
      }

      // Map of material type id to name
      val materialTypeMap: Map[String, String] = {
        armorMaterialTypeSeq.map({ v => (v._1, v._2) }).toMap ++
        shieldMaterialTypeSeq.map({ v => (v._1, v._2) }).toMap
      }

      /**
       * Get a sequence of atomic armor items: (id, name) by armor type ID
       * @param typeId Armor type ID
       * @return Sequence of armor items
       */
      def armorItemSeq(typeId: String): Seq[(String, String)] = {
        Armor.findAll(By(Armor.itemType, typeId.toInt),
                      By(Armor.atomic, "T"),
                      OrderBy(Armor.name, Ascending)) map {
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

        val combos = ArmorMaterialCombo.findAll(mA, mB, iA, iB)

        combos.length match {
          case 0 => None
          case _ => Some(new MaterialData(combos(0)))
        }
      }

      /**
       * Find an armor combination
       * @param idA Item ID A
       * @param idB Item ID B
       * @return Some armor item data or None if no match
       */
      def armorCombo(a: String, b: String): Option[ArmorData] = {
        val combos = ArmorCombo.findAll(a, b)

        combos.length match {
          case 0 => None
          case _ => Some(new ArmorData(combos(0)))
        }
      }

      /**
       * Combine armor and materials
       * @param itemIdA Armor ID
       * @param itemIdB Armor ID
       * @param materialIdA Material ID
       * @param materialIdB Material ID
       * @return Combination summary
       */
      def combine(itemIdA: String, itemIdB: String,
                  itemTypeIdA: String, itemTypeIdB: String,
                  materialIdA: String, materialIdB: String): ComboData = {

        val a = new ArmorData(Armor.findAll(By(Armor.id, itemIdA.toInt))(0))
        val b = new ArmorData(Armor.findAll(By(Armor.id, itemIdB.toInt))(0))

        a.itemType = new ItemTypeData(itemTypeIdA, armorTypeMap(itemTypeIdA))
        b.itemType = new ItemTypeData(itemTypeIdB, armorTypeMap(itemTypeIdB))

        a.material = new MaterialData(materialIdA, materialTypeMap(materialIdA))
        b.material = new MaterialData(materialIdB, materialTypeMap(materialIdB))

        val combo = new ComboData(a, b)

        armorCombo(a.id, b.id) match {
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
          armorCombo(b.id, a.id) match {
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
     * Provides a snippet for generating armor combinations
     *
     * @author sboles
     */
    class ArmorComboSnippet extends StatefulSnippet {

      import ArmorComboSnippet._

      // armor type ID, A
      private var _armorTypeA = armorTypeSeq.head._1

      // armor type ID, B
      private var _armorTypeB = _armorTypeA

      // armor ID, A
      private var _armorItemA = ""

      // armor ID, B
      private var _armorItemB = ""

      // armor A material type ID
      private var _materialTypeA = ""

      // armor B material type ID
      private var _materialTypeB = ""

      private var _submitted = false

      def dispatch: DispatchIt = { case "render" => render _ }

      def render(xhtml: NodeSeq): NodeSeq = {

        def onSubmit(): Unit = { _submitted = true }

        val armor = armorItemSeq(_armorTypeA)

        _armorItemA = armor.head._1

        _armorItemB = _armorItemA

        _materialTypeA = armorMaterialTypeSeq.head._1

        _materialTypeB = _materialTypeA

        val comboItems = combine(_armorItemA, _armorItemB,
                                 _armorTypeA, _armorTypeB,
                                 _materialTypeA, _materialTypeB)

        bind("e", xhtml,
             "armor_type_a" -> SHtml.ajaxSelect(armorTypeSeq,
                                                Full(_armorTypeA),
                                                onChangeArmorTypeA(_),
                                                "id" -> myId("_armorTypeA"),
                                                "class" -> "typeSelect"),

             "armor_type_b" -> SHtml.ajaxSelect(armorTypeSeq,
                                                Full(_armorTypeB),
                                                onChangeArmorTypeB(_),
                                                "id" -> myId("_armorTypeB"),
                                                "class" -> "typeSelect"),

             "armor_item_a" -> SHtml.ajaxSelect(armor,
                                                Full(_armorItemA),
                                                onChangeArmorItemA(_),
                                                "id" -> myId("_armorItemA"),
                                                "class" -> "itemSelect"),

             "armor_item_b" -> SHtml.ajaxSelect(armor,
                                                Full(_armorItemB),
                                                onChangeArmorItemB(_),
                                                "id" -> myId("_armorItemB"),
                                                "class" -> "itemSelect"),

             "material_type_a" -> SHtml.ajaxSelect(armorMaterialTypeSeq,
                                                   Full(_materialTypeA),
                                                   onChangeMaterialTypeA(_),
                                                   "id" -> myId("armorMaterialTypeA"),
                                                   "class" -> "materialSelect"),

             "material_type_b" -> SHtml.ajaxSelect(armorMaterialTypeSeq,
                                                   Full(_materialTypeB),
                                                   onChangeMaterialTypeB(_),
                                                   "id" -> myId("armorMaterialTypeB"),
                                                   "class" -> "materialSelect"),

             "result" -> SHtml.span(ComboSnippet.toResult(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("result")),

             "summary" -> SHtml.span(ComboSnippet.toSummary(comboItems),
                                     JsCmds.Noop,
                                     "id" -> myId("summary")))
      }

      def onChangeArmorTypeA(v: String): JsCmd = {
        Thread.sleep(400)

        val material = armorTypeMap(v) match {
          case "Shield" => shieldMaterialTypeSeq
          case _ => armorMaterialTypeSeq
        }

        val materialChange = armorTypeMap(v) match {
          case "Shield" => true
          case _ => armorTypeMap(_armorTypeA) match {
            case "Shield" => true
            case _ => false
          }
        }

        _materialTypeA = materialChange match {
          case true => material.head._1
          case false => _materialTypeA
        }

        _armorTypeA = v

        val armor = armorItemSeq(_armorTypeA)

        _armorItemA = armor.head._1

        val comboItems = combine(_armorItemA, _armorItemB,
                                 _armorTypeA, _armorTypeB,
                                 _materialTypeA, _materialTypeB)

        JsCmds.seqJsToJs(List(
          JsCmds.Replace(myId("_armorItemA"),
                         SHtml.ajaxSelect(armor,
                                          Full(_armorItemA),
                                          onChangeArmorItemA(_),
                                          "id" -> myId("_armorItemA"),
                                          "class" -> "itemSelect")),
          JsCmds.Replace(myId("armorMaterialTypeA"),
                         SHtml.ajaxSelect(material,
                                          Full(_materialTypeA),
                                          onChangeMaterialTypeA(_),
                                          "id" -> myId("armorMaterialTypeA"),
                                          "class" -> "materialSelect")),
          JsCmds.Replace(myId("result"),
                         SHtml.span(ComboSnippet.toResult(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("result"))),
          JsCmds.Replace(myId("summary"),
                         SHtml.span(ComboSnippet.toSummary(comboItems),
                                    JsCmds.Noop,
                                    "id" -> myId("summary")))))
      }

      def onChangeArmorTypeB(v: String): JsCmd = {
        Thread.sleep(400)

        val material = armorTypeMap(v) match {
          case "Shield" => shieldMaterialTypeSeq
          case _ => armorMaterialTypeSeq
        }

        val materialChange = armorTypeMap(v) match {
          case "Shield" => true
          case _ => armorTypeMap(_armorTypeB) match {
            case "Shield" => true
            case _ => false
          }
        }

        _materialTypeB = materialChange match {
          case true => material.head._1
          case false => _materialTypeB
        }

        _armorTypeB = v

        val armor = armorItemSeq(_armorTypeB)

        _armorItemB = armor.head._1

        val comboItems = combine(_armorItemA, _armorItemB,
                                 _armorTypeA, _armorTypeB,
                                 _materialTypeA, _materialTypeB)

        JsCmds.seqJsToJs(List(
          JsCmds.Replace(myId("_armorItemB"),
                         SHtml.ajaxSelect(armor,
                                          Full(_armorItemB),
                                          onChangeArmorItemB(_),
                                          "id" -> myId("_armorItemB"),
                                          "class" -> "itemSelect")),

          JsCmds.Replace(myId("armorMaterialTypeB"),
                         SHtml.ajaxSelect(material,
                                          Full(_materialTypeB),
                                          onChangeMaterialTypeB(_),
                                          "id" -> myId("armorMaterialTypeB"),
                                          "class" -> "materialSelect")),

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
       * This is the parent change handler for item change. Child item
       * change handlers should update state and then call this method
       * to update the UI.
       */
      def onChangeArmor: JsCmd = {
        Thread.sleep(400)

        val comboItems = combine(_armorItemA, _armorItemB,
                                 _armorTypeA, _armorTypeB,
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

      def onChangeArmorItemA(v: String): JsCmd = {
        _armorItemA = v
        onChangeArmor
      }

      def onChangeArmorItemB(v: String): JsCmd = {
        _armorItemB = v
        onChangeArmor
      }

      def onChangeMaterialTypeA(v: String): JsCmd = {
        _materialTypeA = v
        onChangeArmor
      }

      def onChangeMaterialTypeB(v: String): JsCmd = {
        _materialTypeB = v
        onChangeArmor
      }
    }
  }
}
