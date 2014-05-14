
package org.sboles.vstory.lift.model

import _root_.junit.framework._
import Assert._

/**
 */
object ArmorTypeTest {

  def suite: Test = {
    val suite = new TestSuite(classOf[ArmorTypeTest])
    suite
  }
    
  def main(args: Array[String]): Unit = {
    junit.textui.TestRunner.run(suite)
  }
}

class ArmorTypeTest extends TestCase("ArmorType") {

  // make sure a db connection exists
  override def setUp: Unit = {
    new DBUtil().boot
  }

  def testFindAll: Unit = {
    println("+++ Testing ArmorType.findAll")

    try {
      val types = ArmorType.findAll
      if ( types.length == 0 ) {
        println("ERROR: expected armor types")
        assertTrue(false)
      } else {
        println(" - Got "+types.length+" armor types")
      }
    } catch {
      case e: Exception => {
        println("Error: Failed to get armor types")
        println(e.getMessage)
      }
    }
  }
}
