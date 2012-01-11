package edu.colorado.phet.simeventdatacollectionmonitor

import java.awt.EventQueue
import javax.swing.{JTable, JScrollPane, JFrame}
import javax.swing.table.DefaultTableModel

object JTableTestScala {
  def main(args: Array[String]) {
    EventQueue.invokeLater(new Runnable {
      def run() {
        new JFrame {
          val xx = Array(1.asInstanceOf[Object], 2.asInstanceOf[Object])
          val xy = Array(3.asInstanceOf[Object], 4.asInstanceOf[Object])
          val columnNames = Array("a".asInstanceOf[Object], "bb".asInstanceOf[Object])

          val table = new JTable(new DefaultTableModel(Array(xx, xy), columnNames))
          //          table.setAutoCreateColumnsFromModel(true)
          //          val table = JTableTest.table
          table.setAutoCreateRowSorter(true)
          setContentPane(new JScrollPane(table))
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
          pack()
          setLocationRelativeTo(null)
        }.setVisible(true)
      }
    })
  }
}