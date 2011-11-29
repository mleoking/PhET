// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import edu.umd.cs.piccolo.util.PPaintContext
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox
import java.util.Date
import edu.umd.cs.piccolo.{PCamera, PNode, PCanvas}
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import java.awt.geom.Line2D
import edu.colorado.phet.simsharinganalysis._
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._
import java.awt.{BorderLayout, Dimension, Color}
import scripts.HowMuchTimeSpentInTabs
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath

class StudentActivityCanvas(path: String) extends PCanvas {
  val all = phet load path
  val simTabs = HowMuchTimeSpentInTabs.simTabs
  val sims = all.map(_.simName).distinct

  setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING)
  setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING)
  setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING)
  val sessionLayer = new VBox(20, true)
  getLayer.addChild(sessionLayer)

  //one plot section for each session
  for ( session <- studySessionsNov2011.coloradoStudyMonday :: Nil; sessionLogs = all.filter(session); if sessionLogs.length > 0 ) {

    val machines = sessionLogs.map(_.machine).distinct.sorted
    println("machines.length=" + machines.length)

    //Find the first event from any computer in the session, use it as t=0
    val sessionStartTime = sessionLogs.map(_.epoch).min
    val sessionEndTime = sessionLogs.map(_.endEpoch).max

    sessionLayer.addChild(new PNode {
      addChild(new PText(session.study + " session started at " + new Date(sessionStartTime)))
      addChild(new PhetPPath(new Line2D.Double(getFullBounds.getWidth + 10, getFullBounds.getHeight / 2, 10000, getFullBounds.getHeight / 2)))
    })

    //Coloring for different event types
    def getColor(e: Entry) = {
      e match {
        case x: Entry if x.actor == "system" => Color.yellow
        case _ => Color.black
      }
    }

    sessionLayer.addChild(new TimelineNode(sessionStartTime, sessionStartTime, sessionEndTime))

    val colorMap = Map("Molecule Polarity" -> Color.red,
                       "Balancing Chemical Equations" -> Color.green,
                       "Molecule Shapes" -> new Color(156, 205, 255))
    //One row per computer
    for ( machine <- machines ) {

      val machineNode = new PNode {
        //show the text and anchor at x=0
        //        addChild(new PText(machine + ": " + sessionLogs.filter(_.machine == machine).map(_.user).distinct.sortBy(phet.numerical).mkString(", ")))
        addChild(new PText("Student " + " " + sessionLogs.filter(_.machine == machine).map(_.user).distinct.sortBy(phet.numerical).mkString(", ")))

        var y = 0
        val stripeHeight = 20

        //Stripe for the entire session
        for ( log <- sessionLogs.filter(_.machine == machine) ) {
          val logNode = new LogNode(log, PlotStudentActivity.toX, PlotStudentActivity.toDeltaX, stripeHeight, sessionStartTime, colorMap, getColor) {
            setOffset(0, y)
          }
          addChild(logNode)

          y = y + stripeHeight + 1
        }
      }
      sessionLayer.addChild(machineNode)
    }
  }
}

class MyPText(node: PNode, camera: PCamera, text: String) extends PText(text) {
  camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, new PropertyChangeListener {
    def propertyChange(evt: PropertyChangeEvent) {
      //      println("cs = "+camera.getViewScale)
      setVisible(camera.getScale > 30)
      val globalBounds = node.getGlobalFullBounds

      setOffset(camera.globalToLocal(globalBounds).getBounds2D.getX, camera.globalToLocal(globalBounds).getBounds2D.getY)
    }
  })
  //  protected override def paint(paintContext: PPaintContext) {
  ////    super.paint(paintContext)
  //
  //    val screenFontSize: Float = getFont.getSize * paintContext.getScale.asInstanceOf[Float]
  ////    System.out.println("screenFontSize = " + screenFontSize)
  //
  //    if ( screenFontSize < 5 ) {
  //      paintGreek(paintContext)
  //    }
  //    else {
  //      paintText(paintContext)
  //    }
  //  }
}

class MyMenuItem(text: String, action: () => Unit) extends JMenuItem(text) {
  addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) {
      action()
    }
  })
}

class LogTextWindow(log: Log) extends JFrame("Student " + log.user) {
  setContentPane(new JPanel(new BorderLayout()) {
    add(new JScrollPane(new JTextArea(log.entries.mkString("\n"))), BorderLayout.CENTER)
    setPreferredSize(new Dimension(800, 600))
  })
  pack()
}