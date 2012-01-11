// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor

import com.mongodb._
import javax.swing._
import java.lang.String
import collection.mutable.ArrayBuffer
import collection.JavaConversions.asScalaBuffer
import collection.JavaConversions.asScalaSet
import swing._
import java.awt.event.{ActionListener, ActionEvent}
import table.{DefaultTableModel, TableRowSorter}

/**
 * @author Sam Reid
 */
object SimEventDataCollectionMonitorScala extends App {
  SwingUtilities.invokeLater(new Runnable {
    def run() {
      new SimEventDataCollectionMonitorScala().start()
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
}

class SimEventDataCollectionMonitorScala {
  val mongo = new Mongo
  val tableModel = new SimEventDataTableModel
  val table = new SimpleTable(tableModel)
  val sorter = new TableRowSorter[DefaultTableModel](tableModel)
  table.peer.setRowSorter(sorter)
  sorter.setSortsOnUpdates(true)

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