
package org.sboles.vstory.lift.model

import _root_.junit.framework._
import Assert._

/**
 * DB connection for testing:
 * http://jgoday.wordpress.com/2009/12/25/lift-testing-with-dbunit-and-specs/
 */
object ArmorTest {

  def suite: Test = {
    val suite = new TestSuite(classOf[ArmorTest])
    suite
  }
    
  def main(args: Array[String]): Unit = {
    junit.textui.TestRunner.run(suite)
  }
}

class ArmorTest extends TestCase("Armor") {

  // make sure a db connection exists
  override def setUp: Unit = {
    new DBUtil().boot
  }

  def testFindAll: Unit = {
    println("+++ Testing Armor.findAll")

    try {
      val armor = Armor.findAll
      if ( armor.length == 0 ) {
        println("ERROR: expected armor items")
        assertTrue(false)
      } else {
        println(" - Got "+armor.length+" items")
      }
    } catch {
      case e: Exception => {
        println("Error: Failed to get armor items")
        println(e.getMessage)
      }
    }
  }
}
