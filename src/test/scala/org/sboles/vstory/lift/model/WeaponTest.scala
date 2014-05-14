
package org.sboles.vstory.lift.model

import _root_.junit.framework._
import Assert._

/**
 */
object WeaponTest {

  def suite: Test = {
    val suite = new TestSuite(classOf[WeaponTest])
    suite
  }
    
  def main(args: Array[String]): Unit = {
    junit.textui.TestRunner.run(suite)
  }
}

class WeaponTest extends TestCase("Weapon") {

  // make sure a db connection exists
  override def setUp: Unit = {
    new DBUtil().boot
  }

  def testFindAll: Unit = {
    println("+++ Testing Weapon.findAll")

    try {
      val weapon = Weapon.findAll
      if ( weapon.length == 0 ) {
        println("ERROR: expected weapon items")
        assertTrue(false)
      } else {
        println(" - Got "+weapon.length+" items")
      }
    } catch {
      case e: Exception => {
        println("Error: Failed to get weapon items")
        println(e.getMessage)
      }
    }
  }
}
