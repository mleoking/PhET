// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import javax.swing.JPopupMenu
import edu.colorado.phet.simsharinganalysis.phet._
import edu.colorado.phet.simsharinganalysis.scripts.DoProcessEvents
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import java.awt.event.{MouseEvent, InputEvent}
import java.awt.geom.Rectangle2D
import java.awt.{BasicStroke, Color}
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.simsharinganalysis._
import edu.umd.cs.piccolo.{PCamera, PNode}
import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath, PhetPText}
import edu.colorado.phet.common.phetcommon.view.util.{RectangleUtils, PhetFont}

class LogNode(log: Log,
              toX: Long => Double,
              toDeltaX: Long => Double,
              stripeHeight: Double,
              sessionStartTime: Long,
              colorMap: String => Color,
              getColor: Entry => Color,
              camera: PCamera) extends PNode {

  lazy val logTextWindow = new LogTextWindow(log)
  //  lazy val coverageWindow = new CoverageWindow(log)

  val popup = new JPopupMenu {
    add(new MyMenuItem("Show text log", () => logTextWindow setVisible true))
    add(new MyMenuItem("Plot events", () => xyplot("Events vs time", "Time (minutes)", "Events", log.eventCountData :: Nil)))
    add(new MyMenuItem("Plot unique events", () => {
      //Find which events are important in this sim
      xyplot("Filtered events vs time", "Time (minutes)", "Events", log.countEntries(DoProcessEvents.simEventMap(log.simName)) :: Nil)
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
  addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(log.endTime - log.startTime), stripeHeight), new BasicStroke(2), colorMap(log.simName)) {
    val dt = log.startTime - sessionStartTime
    setOffset(toX(dt), 0) //one second per pixel
  })

  //Show when the window is active with a filled in region
  val sessions = log.getEntryRanges(Rule("window", "activated"), new Or(Rule("window", "deactivated"), LastEntryRule(log)))

  for ( windowSession <- sessions ) {
    val start = log.entries(windowSession._1)
    val end = log.entries(windowSession._2)

    //Show the active sim usage with a colored border
    addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(end.time - start.time), stripeHeight), colorMap(log.simName).darker) {
      val dt = log.startTime + start.time - sessionStartTime
      setOffset(toX(dt), 0) //one second per pixel
    })
  }

  //Show events within the stripe to indicate user activity
  val switchEntriesOnly = log.entries.filter(_.component == "tab").filter(_.action == "pressed").toList
  val switchEntries = log.entries(0) :: switchEntriesOnly ::: log.entries.last :: Nil

  for ( index <- 0 until switchEntries.length - 1 ) {
    val original = switchEntries(index)
    val newOne = switchEntries(index + 1)

    addChild(new PhetPPath(new Rectangle2D.Double(0, 0, toDeltaX(newOne.time - original.time), stripeHeight / 2)) {
      val dt = log.startTime + original.time - sessionStartTime
      setOffset(toX(dt), 0)
    })
  }

  //Make system events wider
  def getBarWidth(e: Entry) = {
    e match {
      case x: Entry if x.component == "system" => 0.15
      case _ => 0.1
    }
  }

  val userLayer = new PNode
  val systemLayer = new PNode
  val labelLayer = new PNode
  addChild(systemLayer)
  addChild(userLayer)
  camera.addChild(labelLayer)

  //Show events within the stripe to indicate user activity
  for ( entry <- log.entries ) {
    val entryTime = entry.time
    val x = toX(entryTime - sessionStartTime)

    //Color based on user/system
    val system = entry.component == "system"

    val width = if ( system ) 0.15 else 0.1

    val line = new PhetPPath(new Rectangle2D.Double(x, 0, width, stripeHeight), getColor(entry))
    lazy val label = {
      val created = new PhetPText(entry.component + " " + entry.action + "\n" + entry.parameters.map(k => k._1 + " = " + k._2).mkString("\n"), new PhetFont(16))
      val background = new PhetPPath(RectangleUtils.expandRectangle2D(created.getFullBounds, 5, 5), new Color(0, 255, 0, 200))
      val node = new PNode {
        addChild(background)
        addChild(created)
      }

      camera addChild node

      node setPickable false
      node setChildrenPickable false
      node
    }

    if ( system ) {
      systemLayer addChild line
    }
    else {
      userLayer addChild line
    }

    line.addInputEventListener(new PBasicInputEventHandler {
      override def mouseEntered(event: PInputEvent) {
        label.centerFullBoundsOnPoint(event.getCanvasPosition.getX, event.getCanvasPosition.getY + label.getFullBounds.getHeight / 2 + 15)
        label setVisible true
      }

      override def mouseExited(event: PInputEvent) {
        label setVisible false
      }
    })

    val simTabs = Map("Balancing Chemical Equations" -> List("Introduction", "Balancing Game"),
                      "Molecule Polarity" -> List("Two Atoms", "Three Atoms", "Real Molecules"),
                      "Molecule Shapes" -> Nil)

    if ( entry.component == "tab" && entry.action == "pressed" ) {
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