// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import edu.umd.cs.piccolo.util.PPaintContext
import java.util.Date
import edu.umd.cs.piccolo.{PCamera, PNode, PCanvas}
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import java.awt.geom.Line2D
import edu.colorado.phet.simsharinganalysis._
import javax.swing._
import scripts.HowMuchTimeSpentInTabs
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath, ControlPanelNode}
import java.awt.{Rectangle, BorderLayout, Dimension, Color}
import edu.colorado.phet.common.piccolophet.nodes.layout.{HBox, VBox}
import java.awt.event.{ComponentEvent, ComponentAdapter}

class StudentActivityCanvas(all: List[Log]) extends PCanvas {

  val simTabs = HowMuchTimeSpentInTabs.simTabs
  val sims = all.map(_.simName).distinct

  setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING)
  setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING)
  setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING)
  val sessionLayer = new VBox(20, VBox.LEFT_ALIGNED)
  getLayer addChild sessionLayer

  val colorMap = Map("Molecule Polarity" -> Color.red,
                     "Balancing Chemical Equations" -> Color.green,
                     "Molecule Shapes" -> new Color(156, 205, 255),
                     "Acid-Base Solutions" -> Color.magenta,
                     "some name!?" -> Color.blue)

  //one plot section for each session
  //  for ( session: Session <- studySessionsNov2011.utahStudyMonday :: Nil; sessionLogs = all; if sessionLogs.length > 0 )
  //  {
  val sessionLogs = all

  val machines = sessionLogs.map(_.machine).distinct.sorted
  println("machines.length=" + machines.length)

  //Find the first event from any computer in the session, use it as t=0
  val sessionStartTime = sessionLogs.map(_.startTime).min
  val sessionEndTime = sessionLogs.map(_.endTime).max

  sessionLayer.addChild(new PNode {
    addChild(new PText("Session started at " + new Date(sessionStartTime)))
    addChild(new PhetPPath(new Line2D.Double(getFullBounds.getWidth + 10, getFullBounds.getHeight / 2, 10000, getFullBounds.getHeight / 2)))
  })

  //Coloring for different event types
  def getColor(e: Entry) = {
    e match {
      case x: Entry if x.component == "system" => Color.yellow
      case _ => Color.black
    }
  }

  sessionLayer.addChild(new TimelineNode(sessionStartTime, sessionStartTime, sessionEndTime))

  //One row per computer
  for ( machine <- machines ) {

    val machineNode = new PNode {
      //show the text and anchor at x=0
      //        addChild(new PText(machine + ": " + sessionLogs.filter(_.machine == machine).map(_.user).distinct.sortBy(phet.numerical).mkString(", ")))
      //      addChild(new PText("Student " + " " + sessionLogs.filter(_.machine == machine).map(_.user).distinct.sortBy(phet.numerical).mkString(", ")))
      //      addChild(new PText("Student " + " " + sessionLogs.filter(_.machine == machine).map(_.user).distinct.sortBy(phet.numerical).mkString(", ")))

      //      def trim(s: String) = s.substring(0, s.indexOf("2012") - 1)
      def trim(s: String) = s

      addChild(new PText("Source: " + sessionLogs.filter(_.machine == machine).map(log => trim(log.file.getName)).mkString(", ")))

      var y = 0
      val stripeHeight = 20

      //Stripe for the entire session
      for ( log <- sessionLogs.filter(_.machine == machine) ) {
        val logNode = new LogNode(log, PlotStudentActivity.toX, PlotStudentActivity.toDeltaX, stripeHeight, sessionStartTime, colorMap, getColor, getCamera) {
          setOffset(0, y)
        }
        addChild(logNode)

        y = y + stripeHeight + 1
      }
    }
    sessionLayer.addChild(machineNode)
  }
  //    ""
  //  }

  val legend = new Legend(colorMap)

  //Hid legend for acid base studies
  //  getCamera addChild legend

  def updateLegendLocation() {
    legend.setOffset(getWidth - legend.getFullBounds.getWidth - 5, 5)
  }

  updateLegendLocation()
  addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) {
      updateLegendLocation()
    }
  })
}

class Legend(colorMap: Map[String, Color]) extends ControlPanelNode(
  new VBox(colorMap.map(entry => new HBox(new PhetPPath(new Rectangle(0, 0, 10, 10), entry._2), new PText(entry._1))).toArray: _*)
  , Color.white)

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

class LogTextWindow(log: Log) extends JFrame("Student " + log.user) {
  setContentPane(new JPanel(new BorderLayout()) {
    add(new JScrollPane(new JTextArea(log.entries.mkString("\n"))), BorderLayout.CENTER)
    setPreferredSize(new Dimension(800, 600))
  })
  pack()
}