
package org.sboles.vstory.lift.lib.db

import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier}
import _root_.net.liftweb.mapper.{ConnectionIdentifier}
import _root_.net.liftweb.mapper.{DefaultConnectionIdentifier, StandardDBVendor}
import _root_.net.liftweb.util.Props

import _root_.java.sql.{Connection, DriverManager}

/**
 * Provides a database connection creation manager
 *
 * Database connection properties are defined in the props/resources files
 *
 * @author sboles
 */
object DBConnection extends ConnectionManager {
    
  private val logger = Logger(classOf[DBConnection])

  /**
   * Identifies the primary DB connection. Use this in conjunction with
   * the newConnection method
   */
  object PrimaryConnection extends ConnectionIdentifier {
    def jndiName: String = "Primary"
  }

  private var pool: List[Connection] = Nil

  private var poolSize = 0

  private var maxPoolSize = 4

  /**
   * Create a new DB connection from database parameters defined in
   * the properties file
   * @return DB connection
   */
  private def createOne: Box[Connection] = try {
    val dbDriver: String = Props.get("db.driver") openOr ""
    val dbUrl: String    = Props.get("db.url") openOr ""
    val dbUser: String   = Props.get("db.user") openOr ""
    val dbPass: String   = Props.get("db.pass") openOr ""

    val url = dbUrl + "?user=" + dbUser + "&password=" + dbPass 

    logger.info("Create DB connection to: "+dbUrl)

    Class.forName(dbDriver)

    val dm = DriverManager.getConnection(url)

    Full(dm)
  } catch {
    case e: Exception => e.printStackTrace; Empty
  }

  /**
   * Get a database connection from the connection pool
   * @param name Connection identifier
   * @return DB connection
   */
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {

    logger.info("New DB connection requested")

    synchronized {
      pool match {
        case Nil if poolSize < maxPoolSize => {
          val ret = createOne
          poolSize = poolSize + 1
          ret.foreach(c => pool = c :: pool)
          ret
        }
        case Nil => wait(1000L); newConnection(name)
        case x :: xs => try {
          x.setAutoCommit(false)
          Full(x)
        } catch {
          case e => try {
            pool = xs
            poolSize = poolSize - 1
            x.close
            newConnection(name)
          } catch {
            case e => newConnection(name)
          }
        }
      }
    }
  }

  def releaseConnection(conn: Connection): Unit = synchronized {
    pool = conn :: pool
    notify
  }

}

class DBConnection { }
