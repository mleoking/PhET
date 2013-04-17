// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.Line2D
import java.awt.{Color, BasicStroke}

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
        setOffset(x, textLabel.getFullBounds.getHeight) //one second per pixel
      }
      addChild(tick)

      textLabel.setOffset(tick.getFullBounds.getCenterX - textLabel.getFullBounds.getWidth / 2, 0)
      addChild(textLabel)
    })
  }
}