
package org.sboles.vstory.lift.model

import _root_.junit.framework._
import Assert._

/**
 */
object WeaponTypeTest {

  def suite: Test = {
    val suite = new TestSuite(classOf[WeaponTypeTest])
    suite
  }
    
  def main(args: Array[String]): Unit = {
    junit.textui.TestRunner.run(suite)
  }
}

class WeaponTypeTest extends TestCase("WeaponType") {

  // make sure a db connection exists
  override def setUp: Unit = {
    new DBUtil().boot
  }

  def testFindAll: Unit = {
    println("+++ Testing WeaponType.findAll")

    try {
      val types = WeaponType.findAll
      if ( types.length == 0 ) {
        println("ERROR: expected weapon types")
        assertTrue(false)
      } else {
        println(" - Got "+types.length+" weapon types")
      }
    } catch {
      case e: Exception => {
        println("Error: Failed to get weapon types")
        println(e.getMessage)
      }
    }
  }
}
