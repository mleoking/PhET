// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor

import com.mongodb._
import javax.swing.table.DefaultTableModel
import javax.swing._
import java.awt.event.{ActionEvent, ActionListener}
import java.lang.String
import collection.mutable.ArrayBuffer
import collection.JavaConversions.asScalaBuffer
import collection.JavaConversions.asScalaSet
import swing.{ScrollPane, Table, BorderPanel, Frame}

/**
 * @author Sam Reid
 */

object SimEventDataCollectionMonitorScala {
  def main(args: Array[String]) {
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        new SimEventDataCollectionMonitorScala().start()
      }
    })
  }
}

class SimEventDataCollectionMonitorScala {
  val mongo = new Mongo
  final val columnNames: Array[AnyRef] = Array("Machine ID", "Session ID", "study", "User ID", "last event received", "event count")
  val table = new Table
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
    new Timer(10000, new ActionListener {
      def actionPerformed(e: ActionEvent) {
        process()
      }
    }) {
      setInitialDelay(0)
    }.start()
  }

  private def process() {
    val rows = new ArrayBuffer[Array[AnyRef]]
    for ( machineID: String <- asScalaBuffer(mongo.getDatabaseNames) if machineID != "admin" ) {
      val database = mongo.getDB(machineID)
      for ( session: String <- asScalaSet(database.getCollectionNames) if session != "system.indexes" ) {
        val collection: DBCollection = database.getCollection(session)

        val obj = collection.findOne()
        val parameters: DBObject = obj.get("parameters").asInstanceOf[DBObject]
        val study = parameters.get("study").toString

        val row = Array(machineID, session, study, "?", "?", collection.getCount.toString.asInstanceOf[AnyRef])
        rows += row
      }
    }
    val data: Array[Array[AnyRef]] = rows.toArray
    val selectedRows: Array[Int] = table.peer.getSelectedRows
    val selectedSessionIDs = selectedRows.map(i => data(i)(1).toString).toList
    table.model = new DefaultTableModel(data, columnNames)
    if ( selectedRows.length > 0 ) {
      table.peer.setSelectionModel(new DefaultListSelectionModel {
        addSelectionInterval(indexForUserID(selectedSessionIDs(0), data), indexForUserID(selectedSessionIDs(selectedSessionIDs.length - 1), data));
      })
    }
    table.peer setAutoCreateRowSorter true
  }

  def indexForUserID(sessionID: String, data: Array[Array[AnyRef]]): Int = {
    for ( i <- 0 until data.length if data(i)(1).toString == sessionID ) {
      return i
    }
    throw new RuntimeException("SSID not found")
  }
}