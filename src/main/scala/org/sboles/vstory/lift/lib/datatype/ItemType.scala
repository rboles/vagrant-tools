
package org.sboles.vstory.lift.lib.datatype

class ItemType {

  var id: String = _
  var name: String = _

  def this(_id: String, _name: String) {
    this
    id = _id
    name = _name
  }

  def toTuple: (String, String) = (id, name)
}
