
package org.sboles.vstory.lift.model

import _root_.junit.framework._
import Assert._

object ArmorMaterialComboTest {

  def suite: Test = {
    val suite = new TestSuite(classOf[ArmorMaterialComboTest])
    suite
  }

  def main(args: Array[String]): Unit = {
    junit.textui.TestRunner.run(suite)
  }
}

class ArmorMaterialComboTest extends TestCase("Armor") {

  // make sure a db connection exists
  override def setUp: Unit = {
    new DBUtil().boot
  }

  /*
  def testFindAll: Unit = {
    println("+++ Testing ArmorMaterialCombo.findAll")

    try {
      val combos = ArmorMaterialCombo.findAll
      if ( combos.length == 0 ) {
        println("ERROR: expected armor material combos")
        assertTrue(false)
      } else {
        println(" - Get "+combos.length+" combos")
      }
    } catch {
      case e: Exception => {
        println("Error: Failed to get armor material combos")
        println(e.getMessage)
      }
    }
  }
  */

  def testFindAllIntInt: Unit = {
    println("+++ Testing ArmorMaterialCombo.findAll(3, 7)")

    try {
      val combos = ArmorMaterialCombo.findAll(3, 7)
      if ( combos.length == 0 ) {
        println("ERROR: expected armor material combos")
        assertTrue(false)
      } else {
        println(" - Got "+combos.length+" combos")
      }
    } catch {
      case e: Exception => {
        println("Error: Failed to get armor material combos")
        println(e.getMessage)
      }
    }
  }
}
