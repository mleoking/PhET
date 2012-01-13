// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor

import com.mongodb._
import java.lang.String
import collection.mutable.ArrayBuffer
import collection.JavaConversions.asScalaBuffer
import collection.JavaConversions.asScalaSet
import swing._
import java.awt.event.{MouseEvent, MouseAdapter, ActionListener, ActionEvent}
import javax.swing._
import table.{DefaultTableModel, TableRowSorter}
import edu.colorado.phet.common.phetcommon.simsharing.{SimSharingMongoClient, SimSharingManager}

/**
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
  val columns = List("Machine ID" -> classOf[String], "Session ID" -> classOf[String], "study" -> classOf[String], "User ID" -> classOf[String], "last event received" -> classOf[String], "Event Count" -> classOf[java.lang.Long])
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

  def setEventCount(session: String, count: java.lang.Long) {
    setValueAt(count.asInstanceOf[Object], getRow(session), 5)
  }

  override def getColumnClass(columnIndex: Int) = SimEventDataTableModel.columns(columnIndex)._2

  def getMachineID(rowIndex: Int) = getValueAt(rowIndex, 0).toString

  def getSessionID(rowIndex: Int) = getValueAt(rowIndex, 1).toString
}

class SimEventDataCollectionMonitor {
  //  val mongo = new Mongo("phet-server.colorado.edu")
  val mongo = new Mongo
  val tableModel = new SimEventDataTableModel
  val table = new SimpleTable(tableModel)
  table.peer.addMouseListener(new MouseAdapter {
    override def mouseReleased(e: MouseEvent) {
      if ( e.isPopupTrigger ) {
        val menu = new JPopupMenu {
          add(new JMenuItem("Show Log") {
            addActionListener(new ActionListener {
              def actionPerformed(e: ActionEvent) {
                printSelectedRow()
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

  def printSelectedRow() {
    val row = table.peer.getSelectedRow
    val machineID = tableModel.getMachineID(row)
    val sessionID = tableModel.getSessionID(row)

    val database = mongo.getDB(machineID)
    val collection: DBCollection = database.getCollection(sessionID)

    val cursor = collection.find
    while ( cursor.hasNext ) {
      val obj = cursor.next()
      val time = obj.get(SimSharingMongoClient.TIME)
      val messageType = obj.get(SimSharingMongoClient.MESSAGE_TYPE)
      val component = obj.get(SimSharingMongoClient.COMPONENT)
      val action = obj.get(SimSharingMongoClient.ACTION)
      val parameters: DBObject = obj.get(SimSharingMongoClient.PARAMETERS).asInstanceOf[DBObject]
      val tab = SimSharingManager.DELIMITER
      val paramString = asScalaSet(parameters.keySet).map(s => s + " = " + parameters.get(s)).mkString(tab)

      println(time + tab + messageType + tab + component + tab + action + tab + paramString)
    }
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
    new Timer(5000, new ActionListener {
      def actionPerformed(e: ActionEvent) {
        process()
      }
    }) {
      setInitialDelay(0)
    }.start()
  }

  private def process() {
    val rows = new ArrayBuffer[Array[Object]]
    for ( machineID: String <- asScalaBuffer(mongo.getDatabaseNames) if machineID != "admin" ) {
      val database = mongo.getDB(machineID)
      for ( session: String <- asScalaSet(database.getCollectionNames) if session != "system.indexes" ) {
        val collection: DBCollection = database.getCollection(session)

        val obj = collection.findOne()
        val parameters: DBObject = obj.get("parameters").asInstanceOf[DBObject]
        val study = parameters.get("study").toString

        val row = Array(machineID, session, study, "?", "?", collection.getCount.asInstanceOf[Object])

        //If the tableModel already has this session, then update the updatable fields
        if ( tableModel.containsSession(session) ) {
          tableModel.setEventCount(session, collection.getCount)
        }
        else {
          tableModel.addRow(row)
        }

        //Otherwise add it as a new row
        rows += row
      }
    }
  }

  def indexForUserID(sessionID: String, data: Array[Array[AnyRef]]): Int = {
    for ( i <- 0 until data.length if data(i)(1).toString == sessionID ) {
      return i
    }
    throw new RuntimeException("SSID not found")
  }
}