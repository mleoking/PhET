// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.Dimension
import swing._
import javax.swing.JFileChooser

/**
 * @author Sam Reid
 */
object RPALRealTimeAnalysis extends App {
  val canvas = new PhetPCanvas()
  val chartFrame = new Frame {
    contents = new Component {
      override lazy val peer = canvas
    }
    size = new Dimension(1024, 100)
  }

  chartFrame.visible = true

  new RealTimeAnalysis(log => {
    val states = RPALAnalysis getStates log
    canvas.getLayer.removeAllChildren()
    canvas.getLayer.addChild(new TimelineNode(states, log.startTime, log.endTime))

    RPALAnalysis.toReport(log).toString + "\n" +
    "Tab " + states.last.end.tab + "\n" +
    "Tab 1 View " + states.last.end.tab1.view + "\n" +
    "Tab 0 sandwich: " + states.last.end.tab0.sandwich + "\n" +
    "Tab 1 reaction: " + states.last.end.tab1.reaction + "\n" +
    "\nLast 5 events (most recent at the bottom):\n" +
    states.map(_.entry).takeRight(5).mkString("\n")
  }) {
    top.menuBar = new MenuBar {
      contents += new Menu("File") {

        contents += new MenuItem(Action("Show Time Plots"
        ) {
            val chooser = new JFileChooser() {
              setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
            }
            val result = chooser.showOpenDialog(top.peer)
            result match {
              case JFileChooser.APPROVE_OPTION => RPALAnalysis.main(Array(chooser.getSelectedFile.getAbsolutePath))
              case _ => {}
            }
          })

        contents += new MenuItem(Action("Show Reports"
        ) {
            val chooser = new JFileChooser() {
              setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
            }
            val result = chooser.showOpenDialog(top.peer)
            result match {
              case JFileChooser.APPROVE_OPTION => RPALShowAllReports.main(Array(chooser.getSelectedFile.getAbsolutePath))
              case _ => {}
            }
          })


        contents += new MenuItem(Action("Game plots"
        ) {
            val chooser = new JFileChooser() {
              setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
            }
            val result = chooser.showOpenDialog(top.peer)
            result match {
              case JFileChooser.APPROVE_OPTION => PlotCharts.main(Array(chooser.getSelectedFile.getAbsolutePath))
              case _ => {}
            }
          })
      }
    }
  }.main(args)
}