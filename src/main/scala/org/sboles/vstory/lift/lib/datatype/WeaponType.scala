
package org.sboles.vstory.lift.lib.datatype

import org.sboles.vstory.lift.model.{WeaponType => WeaponTypeModel}

class WeaponType extends ItemType {

  def this(_id: String, _name: String) {
    this
    id = _id
    name = _name
  }

  def this(m: WeaponTypeModel) {
    this(m.id.toString, m.name.toString)
  }

  def this(l: List[WeaponTypeModel]) {
    this(l(0))
  }
}
