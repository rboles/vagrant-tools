
package org.sboles.vstory.lift.model

import _root_.net.liftweb.util.Props
import _root_.net.liftweb.common.{Full, Logger}
import _root_.net.liftweb.http.LiftRules
import _root_.net.liftweb.mapper.{DB, ConnectionManager}
import _root_.net.liftweb.mapper.{DefaultConnectionIdentifier, StandardDBVendor}

import org.sboles.vstory.lift.lib.db.DBConnection

class MyTestDBVendor { }

class DBUtil {

  private val logger = Logger(classOf[DBUtil])

  def boot: Unit = {
    if ( !DB.jndiJdbcConnAvailable_? ) {
      logger.info("Initializing DB connection")
      
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBConnection)
    }
  }
}
