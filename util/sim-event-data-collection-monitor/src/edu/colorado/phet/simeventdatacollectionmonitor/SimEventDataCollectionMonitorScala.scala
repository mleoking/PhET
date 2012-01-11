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
import table.{DefaultTableModel, TableModel, TableRowSorter}

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

class SimEventDataCollectionMonitorScala {
  val mongo = new Mongo
  final val columns = List("Machine ID" -> classOf[String], "Session ID" -> classOf[String], "study" -> classOf[String], "User ID" -> classOf[String], "last event received" -> classOf[Integer], "Event Count" -> classOf[java.lang.Integer])
  final val columnNames: Array[Object] = columns.map(_._1).toArray
  val model1 = new DefaultTableModel()
  val table = new SimpleTable(model1)
  val sorter = new TableRowSorter[DefaultTableModel](model1)
  table.peer.setRowSorter(sorter)
  sorter.setSortsOnUpdates(true)

  val frame = new Frame {
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
        rows += row
      }
    }
    val data: Array[Array[Object]] = rows.toArray[Array[Object]]
    val selectedRows: Array[Int] = table.peer.getSelectedRows
    val selectedSessionIDs = selectedRows.map(i => data(i)(1).toString).toList
    val m = new DefaultTableModel(data, columnNames) {
      override def getColumnClass(columnIndex: Int) = columns(columnIndex)._2
    }
    table.peer.setModel(m)
    sorter.setModel(m)

    //See http://stackoverflow.com/questions/996948/live-sorting-of-jtable
    if ( selectedRows.length > 0 ) {
      table.peer.setSelectionModel(new DefaultListSelectionModel {
        addSelectionInterval(indexForUserID(selectedSessionIDs(0), data), indexForUserID(selectedSessionIDs(selectedSessionIDs.length - 1), data));
      })
    }
  }

  def indexForUserID(sessionID: String, data: Array[Array[AnyRef]]): Int = {
    for ( i <- 0 until data.length if data(i)(1).toString == sessionID ) {
      return i
    }
    throw new RuntimeException("SSID not found")
  }
}