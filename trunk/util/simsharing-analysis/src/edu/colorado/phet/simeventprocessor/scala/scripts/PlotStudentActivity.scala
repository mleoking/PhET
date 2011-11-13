// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import javax.swing.JFrame
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.simeventprocessor.scala.{phet, studySessionsNov2011}
import java.util.Date
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox
import java.awt.geom.{Line2D, Rectangle2D}
import java.awt.Color
import edu.umd.cs.piccolo.{PNode, PCanvas}
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath

/**
 * Show a 2d plot of student activity as a function of time.  Row = student machine, x-axis is time and color coding is activity
 * @author Sam Reid
 */
object PlotStudentActivity extends App {

  def toX(dt: Long) = 200.0 + dt.toDouble / 1000.0 / 60.0 * 2.0 * 10.0

  def toDeltaX(dt: Long) = toX(dt) - toX(0)

  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"
  val simTabs = HowMuchTimeSpentInTabs.simTabs
  val sims = all.map(_.simName).distinct

  val canvas = new PCanvas
  val panel = new VBox(20, true)
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
      addChild(new PText(session.study + " session started at " + new Date(sessionStartTime)))
      addChild(new PhetPPath(new Line2D.Double(getFullBounds.getWidth + 10, getFullBounds.getHeight / 2, 10000, getFullBounds.getHeight / 2)))
    })

    panel.addChild(new TimelineNode(sessionStartTime, sessionStartTime, sessionEndTime))

    val colorMap = Map("Molecule Polarity" -> Color.red,
                       "Balancing Chemical Equations" -> Color.green,
                       "Molecule Shapes" -> new Color(156, 205, 255))
    //One row per computer
    for ( machine <- machines ) {

      val machineNode = new PNode {
        //show the text and anchor at x=0
        addChild(new PText(machine))

        var y = 0
        val stripeHeight = 6

        //Stripe for the entire session
        for ( log <- sessionLogs.filter(_.machine == machine) ) {
          val logNode = new PNode {
            addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(log.endEpoch - log.epoch), stripeHeight), colorMap(log.simName)) {
              val dt = log.epoch - sessionStartTime
              setOffset(toX(dt), y) //one second per pixel
            })

            //Show events within the stripe to indicate user activity
            for ( entry <- log.entries ) {
              val entryTime = entry.time + log.epoch
              val x = toX(entryTime - sessionStartTime)
              addChild(new PhetPPath(new Line2D.Double(x, y, x, y + stripeHeight)))
            }
          }
          addChild(logNode)

          y = y + stripeHeight + 1
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

//Show a timeline that starts at the first event and has tick marks and labels every 15 minutes
class TimelineNode(sessionStartTime: Long, start: Long, end: Long) extends PNode {
  //anchor at (0,0)
  addChild(new PText("timeline"))

  for ( t <- start until end by 1000 * 60 * 15 ) {
    addChild(new PNode {
      val tick = new PhetPPath(new Line2D.Double(0, 0, 0, 10)) {
        val x = PlotStudentActivity.toX(t - sessionStartTime)
        println("x = " + x)
        setOffset(x, 0) //one second per pixel
      }
      this addChild tick
      addChild(new PText(new Date(t).toString) {
        setOffset(tick.getFullBounds.getCenterX - getFullBounds.getWidth / 2, tick.getFullBounds.getMaxY)
      })
    })
  }
}