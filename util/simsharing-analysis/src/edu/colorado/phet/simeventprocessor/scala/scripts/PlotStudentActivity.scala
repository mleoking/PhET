// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import javax.swing.JFrame
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.simeventprocessor.scala.{phet, studySessionsNov2011}
import java.util.Date
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox
import edu.umd.cs.piccolo.{PNode, PCanvas}
import java.awt.Color
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Line2D, Rectangle2D}

/**
 * Show a 2d plot of student activity as a function of time.  Row = student machine, x-axis is time and color coding is activity
 * @author Sam Reid
 */
object PlotStudentActivity extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"
  val simTabs = HowMuchTimeSpentInTabs.simTabs
  val sims = all.map(_.simName).distinct

  val canvas = new PCanvas
  val panel = new VBox(2, true)
  canvas.getLayer.addChild(panel)

  //one plot section for each session
  for ( session <- studySessionsNov2011.all ) {
    val sessionLogs = all.filter(session)
    val machines = sessionLogs.map(_.machine).distinct.sorted
    println("machines.length=" + machines.length)

    //Find the first event from any computer in the session, use it as t=0
    val sessionStartTime = sessionLogs.map(_.epoch).min
    val sessionEndTime = sessionLogs.map(_.endEpoch).max

    panel.addChild(new PNode {
      this addChild new PText(session.study + " session started at " + new Date(sessionStartTime))
      this addChild new PhetPPath(new Line2D.Double(getFullBounds.getWidth + 10, getFullBounds.getHeight / 2, 10000, getFullBounds.getHeight / 2))
    })

    val colorMap = Map("Molecule Polarity" -> Color.red,
                       "Balancing Chemical Equations" -> Color.green,
                       "Molecule Shapes" -> Color.black)
    //One row per computer
    for ( machine <- machines ) {

      val machineNode = new PNode {
        addChild(new PText(machine))

        //Session start messages
        val machineLogs = sessionLogs.filter(_.machine == machine)

        for ( log <- machineLogs ) {
          val startTime = log.epoch
          addChild(new PhetPPath(new Rectangle2D.Double(0, 0, 6, 6), colorMap(log.simName)) {
            val dt = startTime - sessionStartTime
            setOffset(200 + dt / 1000 / 60 * 2 * 10, 0) //one second per pixel
          })
        }
      }

      panel.addChild(machineNode)
    }
  }

  new JFrame {
    setContentPane(canvas)
    setSize(1024, 768)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }.setVisible(true)
}