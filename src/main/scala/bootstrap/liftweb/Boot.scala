
package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.http.js.JsCmds
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier}
import _root_.net.liftweb.mapper.{ConnectionIdentifier}
import _root_.net.liftweb.mapper.{DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}

import Helpers._

import org.sboles.vstory.lift.lib.rest.VagrantRest
import org.sboles.vstory.lift.lib.rest.VagrantJson
import org.sboles.vstory.lift.lib.db.DBConnection

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  private val logger = Logger(classOf[Boot])

  /**
   * Provides a DB connection manager
   * 
   * Testing with MVN Scala Console
   * > mvn scala:console
   * scala> new bootstrap.liftweb.Boot().boot
   * scala> org.sboles.vstory.lift.model.ArmorType.findAll
   * @author sboles
   */
  object MyDBVendor extends ConnectionManager {

    private val logger = Logger(classOf[MyDBVendor])

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

      logger.info("Create DB connection to: "+url)

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

  class MyDBVendor { }

  def boot {
    // DB connection manager
    // DB.defineConnectionManager(DefaultConnectionIdentifier, MyDBVendor)
    DB.defineConnectionManager(DefaultConnectionIdentifier, DBConnection)

    // where to search snippet
    LiftRules.addToPackages("org.sboles.vstory.lift")

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => JsCmds.seqJsToJs(List(
        LiftRules.jsArtifacts.hide("headerImg").cmd,
        LiftRules.jsArtifacts.show("ajaxLoader").cmd)))

    /*
     * Hide spinny image when AJAX call ends
     */
    LiftRules.ajaxEnd =
      Full(() => JsCmds.seqJsToJs(List(
        LiftRules.jsArtifacts.hide("ajaxLoader").cmd,
        LiftRules.jsArtifacts.show("headerImg").cmd)))

    // site map
    val apiMenu = Menu(Loc("API", "api" :: "index" :: Nil, "API", Hidden))
    val homeMenu = Menu(Loc("Home", "index" :: Nil, "", Hidden))
    val allMenus = homeMenu :: apiMenu :: Nil
    val mySiteMap = SiteMap(allMenus: _*)

    // LiftRules.setSiteMap(siteMap)
    LiftRules.setSiteMap(mySiteMap)

    LiftRules.early.append(makeUtf8)

    LiftRules.statelessDispatchTable.append(VagrantRest)

    LiftRules.statelessDispatchTable.append(VagrantJson)

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
