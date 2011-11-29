// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import edu.umd.cs.piccolo.util.PPaintContext
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox
import edu.umd.cs.piccolo.nodes.PText
import java.util.Date
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.umd.cs.piccolo.{PCamera, PNode, PCanvas}
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import java.awt.geom.{Rectangle2D, Line2D}
import edu.colorado.phet.simsharinganalysis._
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import java.awt.event.{InputEvent, ActionEvent, ActionListener, MouseEvent}
import javax.swing._
import java.awt.{BorderLayout, Dimension, BasicStroke, Color}
import edu.colorado.phet.simsharinganalysis.phet._
import scripts.{DoProcessEvents, HowMuchTimeSpentInTabs}

class StudentActivityCanvas(path: String) extends PCanvas {
  val all = phet load path
  val simTabs = HowMuchTimeSpentInTabs.simTabs
  val sims = all.map(_.simName).distinct

  setInteractingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING)
  setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING)
  setAnimatingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING)
  val sessionLayer = new VBox(20, true)
  getLayer.addChild(sessionLayer)

  //one plot section for each session
  for ( session <- studySessionsNov2011.all; sessionLogs = all.filter(session); if sessionLogs.length > 0 ) {

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

//Show a timeline that starts at the first event and has tick marks and labels every 15 minutes
class TimelineNode(sessionStartTime: Long, start: Long, end: Long) extends PNode {
  //anchor at (0,0)
  addChild(new PText("timeline"))

  for ( t <- start until end by 1000 * 60 * 10 ) {
    addChild(new PNode {

      val elapsedTime = t - start
      val totalSeconds = elapsedTime / 1000
      val minutes = totalSeconds / 60

      val textLabel = new PText(minutes + ":00")

      val tick = new PhetPPath(new Line2D.Double(0, 0, 0, 10), new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1, Array(10f, 10f), 0), Color.lightGray) {
        val x = PlotStudentActivity.toX(t - sessionStartTime)
        println("x = " + x)
        setOffset(x, textLabel.getFullBounds.getHeight) //one second per pixel
      }
      this addChild tick

      textLabel.setOffset(tick.getFullBounds.getCenterX - textLabel.getFullBounds.getWidth / 2, 0)
      addChild(textLabel)
    })
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

class LogNode(log: Log, toX: Long => Double, toDeltaX: Long => Double, stripeHeight: Double, sessionStartTime: Long, colorMap: String => Color, getColor: Entry => Color) extends PNode {

  lazy val logTextWindow = new LogTextWindow(log)
  //  lazy val coverageWindow = new CoverageWindow(log)

  val popup = new JPopupMenu {
    add(new MyMenuItem("Show text log", () => logTextWindow setVisible true))
    add(new MyMenuItem("Plot events", () => xyplot("Events vs time", "Time (minutes)", "Events", log.eventCountData :: Nil)))
    add(new MyMenuItem("Plot unique events", () => {
      //Find which events are important in this sim
      xyplot("Filtered events vs time", "Time (minutes)", "Events", log.countEvents(DoProcessEvents.simEventMap(log.simName)) :: Nil)
    }))
  }

  addInputEventListener(new PBasicInputEventHandler {
    override def mouseReleased(event: PInputEvent) {
      maybeShowPopup(event.getSourceSwingEvent)
    }

    override def mousePressed(event: PInputEvent) {
      maybeShowPopup(event.getSourceSwingEvent)
    }

    def maybeShowPopup(event: InputEvent) {
      event match {
        case e: MouseEvent if e.getButton == MouseEvent.BUTTON3 || e.getButton == MouseEvent.BUTTON2 => popup.show(event.getComponent, e.getX, e.getY)
        case _ => {}
      }
    }
  })
  //Show the entire sim usage with a colored border
  addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(log.endEpoch - log.epoch), stripeHeight), new BasicStroke(2), colorMap(log.simName)) {
    val dt = log.epoch - sessionStartTime
    setOffset(toX(dt), 0) //one second per pixel
  })

  //Show when the window is active with a filled in region
  val sessions = log.getEntryRanges(Rule("window", "activated"), new Or(Rule("window", "deactivated"), LastEntryRule(log)))

  for ( windowSession <- sessions ) {
    val start = log.entries(windowSession._1)
    val end = log.entries(windowSession._2)

    //Show the active sim usage with a colored border
    addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(end.time - start.time), stripeHeight), colorMap(log.simName).darker) {
      val dt = log.epoch + start.time - sessionStartTime
      setOffset(toX(dt), 0) //one second per pixel
    })
  }

  //Show events within the stripe to indicate user activity
  val switchEntriesOnly = log.entries.filter(_.actor == "tab").filter(_.event == "pressed").toList
  val switchEntries = log.entries(0) :: switchEntriesOnly ::: log.entries.last :: Nil

  for ( index <- 0 until switchEntries.length - 1 ) {
    val original = switchEntries(index)
    val newOne = switchEntries(index + 1)

    addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(newOne.time - original.time), stripeHeight / 2)) {
      val dt = log.epoch + original.time - sessionStartTime
      setOffset(toX(dt), 0)
    })
  }

  //Show events within the stripe to indicate user activity
  for ( entry <- log.entries ) {
    val entryTime = entry.time + log.epoch
    val x = toX(entryTime - sessionStartTime)

    //Color based on user/system
    //              val line = new PhetPPath(new Line2D.Double(x, y, x, y + stripeHeight), new PFixedWidthStroke(1f), getColor(entry))
    val line = new PhetPPath(new Line2D.Double(x, 0, x, stripeHeight), new BasicStroke(0.1f), getColor(entry))
    addChild(line)

    val simTabs = Map("Balancing Chemical Equations" -> List("Introduction", "Balancing Game"),
                      "Molecule Polarity" -> List("Two Atoms", "Three Atoms", "Real Molecules"),
                      "Molecule Shapes" -> Nil)

    if ( entry.actor == "tab" && entry.event == "pressed" ) {
      val tabName = entry("text")
      val st = simTabs(log.simName)
      val index = st.indexOf(tabName)
      addChild(new PText("" + index) {
        setOffset(line.getFullBounds.getCenterX - getFullBounds.getWidth / 2, line.getFullBounds.getMaxY)
      })
    }

    //              val text = new MyPText(line, canvas.getCamera, entry.toString) {
    ////                setOffset(line.getFullBounds.getCenterX - getFullBounds.getWidth / 2, line.getFullBounds.getY)
    //                setScale(1E-6)
    //              }
    //              canvas.getCamera.addChild(text)
  }
}