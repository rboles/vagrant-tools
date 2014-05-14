
package org.sboles.vstory.lift.lib.datatype

import org.sboles.vstory.lift.model.{ArmorType => ArmorTypeModel}

class ArmorType extends ItemType {

  def this(_id: String, _name: String) {
    this
    id = _id
    name = _name
  }

  def this(m: ArmorTypeModel) {
    this(m.id.toString, m.name.toString)
  }

  def this(l: List[ArmorTypeModel]) {
    this(l(0))
  }
}
