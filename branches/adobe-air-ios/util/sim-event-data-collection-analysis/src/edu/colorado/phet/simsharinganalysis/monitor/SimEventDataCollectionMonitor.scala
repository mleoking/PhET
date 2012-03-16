package edu.colorado.phet.simsharinganalysis.monitor

// Copyright 2002-2011, University of Colorado

import collection.JavaConversions.asScalaSet
import swing._
import java.awt.event.{MouseEvent, MouseAdapter, ActionListener, ActionEvent}
import javax.swing._
import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager
import com.mongodb._
import java.util.Date
import table.{DefaultTableCellRenderer, DefaultTableModel, TableRowSorter}
import java.text.SimpleDateFormat
import org.bson.types.ObjectId
import java.lang.{Thread, String}
import edu.colorado.phet.simsharinganalysis.util.SimpleTextFrame
import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys

/**
 * Shows a spreadsheet with data on the live mongodb server.
 * @author Sam Reid
 */
object SimEventDataCollectionMonitor extends App {
  SwingUtilities.invokeLater(new Runnable {
    def run() {
      new SimEventDataCollectionMonitor().start()
    }
  })
}

object SimEventDataTableModel {
  val columns = List("Machine ID" -> classOf[String], "Session ID" -> classOf[String], "study" -> classOf[String], "User ID" -> classOf[String], "last event received" -> classOf[java.util.Date], "Event Count" -> classOf[java.lang.Long])
  val columnNames: Array[Object] = columns.map(_._1).toArray
}

//See http://stackoverflow.com/questions/996948/live-sorting-of-jtable
class SimEventDataTableModel extends DefaultTableModel(SimEventDataTableModel.columnNames, 0) {
  def containsSession(s: String): Boolean = getRow(s) >= 0

  def getRow(session: String): Int = {
    for ( i <- 0 until getRowCount ) {
      if ( getValueAt(i, 1) == session ) {
        return i
      }
    }
    -1
  }

  def setLastEventReceived(session: String, date: Date) {
    setValueAt(date, getRow(session), 4)
  }

  def setEventCount(session: String, count: java.lang.Long) {
    setValueAt(count.asInstanceOf[Object], getRow(session), 5)
  }

  override def getColumnClass(columnIndex: Int) = SimEventDataTableModel.columns(columnIndex)._2

  def getMachineID(rowIndex: Int) = getValueAt(rowIndex, 0).toString

  def getSessionID(rowIndex: Int) = getValueAt(rowIndex, 1).toString
}

class SimEventDataCollectionMonitor {
  var HOST_IP_ADDRESS: String = System.getProperty("sim-event-data-collection-server-host-ip-address", "128.138.145.107")
  var PORT: Int = Integer.parseInt(System.getProperty("sim-event-data-collection-server-host-port", "44100"))

  //Connect to the remote database
  val mongo = new Mongo(HOST_IP_ADDRESS, PORT)

  //Connect to the local database
  //  val mongo = new Mongo

  //Connect to database in a read-only connection
  val database = mongo.getDB("sessions")
  val auth = database.authenticate("monitor", "phetservermonitor778734".toCharArray)
  println("authenticated: " + auth)

  val tableModel = new SimEventDataTableModel
  val table = new SimpleTable(tableModel)

  val cellRenderer = new DefaultTableCellRenderer {
    override def getTableCellRendererComponent(table: JTable, value: AnyRef, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int) = {
      val v = if ( value.isInstanceOf[Date] ) {
        new SimpleDateFormat("EEE, MMM d, yyyy h:mm:ss a z").format(value)
      }
      else {
        value
      }
      super.getTableCellRendererComponent(table, v, isSelected, hasFocus, row, column)
    }
  }
  table.peer.getColumnModel.getColumn(4).setCellRenderer(cellRenderer)
  table.peer.addMouseListener(new MouseAdapter {
    override def mouseReleased(e: MouseEvent) {
      if ( e.isPopupTrigger ) {
        val menu = new JPopupMenu {
          add(new JMenuItem("Show Log") {
            addActionListener(new ActionListener {
              def actionPerformed(e: ActionEvent) {
                println(selectedRowToText)
                new SimpleTextFrame {
                  text = selectedRowToText
                }.visible = true
              }
            })
          })
        }
        menu.show(e.getComponent, e.getX, e.getY)
      }
    }
  })
  val sorter = new TableRowSorter[DefaultTableModel](tableModel)
  table.peer.setRowSorter(sorter)
  sorter.setSortsOnUpdates(true)

  def selectedRowToText = {

    //Convert view row to model row to account for sorting in the view: http://stackoverflow.com/questions/2075396/correctly-getting-data-from-a-sorted-jtable
    val viewSelectedRow = table.peer.getSelectedRow
    val row = table.peer.convertRowIndexToModel(viewSelectedRow)
    val sessionID = tableModel.getSessionID(row)

    val collection: DBCollection = database.getCollection(sessionID)

    val cursor = collection.find
    val arrayBuffer = new ArrayBuffer[String]()
    while ( cursor.hasNext ) {
      val obj = cursor.next()

      //attempt to read timestamp from existing entries
      //      //See http://stackoverflow.com/questions/3338999/get-id-of-last-inserted-document-in-a-mongodb-w-java-driver
      //      val id = obj.get("_id").asInstanceOf[ObjectId];
      //      println("objectID timestamp= "+id.getTime)

      val time = obj.get(MongoLog.TIME)
      val messageType = obj.get(MongoLog.MESSAGE_TYPE)
      val component = obj.get(MongoLog.COMPONENT)
      val componentType = obj.get(MongoLog.COMPONENT_TYPE)
      val action = obj.get(MongoLog.ACTION)
      val parameters: DBObject = obj.get(MongoLog.PARAMETERS).asInstanceOf[DBObject]
      val tab = SimSharingManager.DELIMITER
      val paramString = asScalaSet(parameters.keySet).map(s => s + " = " + parameters.get(s)).mkString(tab)

      arrayBuffer += ( time + tab + messageType + tab + component + tab + componentType + tab + action + tab + paramString )
    }
    arrayBuffer.mkString("\n")
  }

  val frame = new Frame {
    peer setDefaultCloseOperation JFrame.EXIT_ON_CLOSE
    title = "MongoDB Monitor"
    contents = new BorderPanel() {
      add(new ScrollPane() {
        contents = table
      }, BorderPanel.Position.Center)
    }
  }
  frame.pack()

  def start() {
    frame.visible = true
    new Thread(new Runnable {
      def run() {
        while ( true ) {
          readDataFromMongoServer()
          //          Thread.sleep(5000)
        }
      }
    }).start()
  }

  private def readDataFromMongoServer() {
    val start = System.currentTimeMillis
    val collectionNames = asScalaSet(database.getCollectionNames)
    val end = System.currentTimeMillis()
    println("Read collection names: " + ( end - start ) + " ms")
    for ( session: String <- collectionNames if session != "system.indexes" if session != "system.users" ) {
      val collection: DBCollection = database.getCollection(session)

      val start = System.currentTimeMillis
      val startMessage = collection.findOne()
      val parameters: DBObject = startMessage.get("parameters").asInstanceOf[DBObject]
      val getStudy = parameters.get("study")
      val getMachineID = parameters.get(ParameterKeys.machineCookie.toString)
      val study = if ( getStudy == null ) "null" else getStudy.toString

      val getUserID = parameters.get("id")
      val userID = if ( getUserID == null ) "null" else getUserID.toString
      val numberMessages = collection.getCount
      val cursor = collection.find().skip(numberMessages.toInt - 1)

      //Read the server time of the last message instead of relying on unsynchronized client clocks
      val endMessage = cursor.next()
      val endMessageTime = new Date(endMessage.get("_id").asInstanceOf[ObjectId].getTime)

      val row = Array(getMachineID, session, study, userID, endMessageTime, numberMessages.asInstanceOf[Object])
      val end = System.currentTimeMillis
      println("Read row: " + ( end - start ) + " ms")

      //Run update in Swing thread
      SwingUtilities.invokeLater(new Runnable {
        def run() {
          //If the tableModel already has this session, then update the updateable fields
          if ( tableModel.containsSession(session) ) {
            tableModel.setEventCount(session, collection.getCount)
            tableModel.setLastEventReceived(session, endMessageTime)
          }

          //Otherwise add it as a new row
          else {
            tableModel.addRow(row)
          }
        }
      })
    }
    val done = System.currentTimeMillis
    println("Time to update all = " + ( done - start ) + " ms")
  }

  def indexForUserID(sessionID: String, data: Array[Array[AnyRef]]): Int = {
    for ( i <- 0 until data.length if data(i)(1).toString == sessionID ) {
      return i
    }
    throw new RuntimeException("SSID not found")
  }
}