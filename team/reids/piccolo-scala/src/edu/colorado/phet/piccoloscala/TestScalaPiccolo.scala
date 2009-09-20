package edu.colorado.phet.piccoloscala

import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.Ellipse2D
import java.awt.{BasicStroke, Color, Dimension}
import umd.cs.piccolo.nodes.PText
import edu.colorado.phet.piccoloscala.Predef._
import Color._
import javax.swing.JFrame._

object TestScalaPiccolo {
  def main(args: Array[String]) = {
    runInSwingThread {
      new MyJFrame {
        contentPane = new SCanvas {
          val circle = this add new PhetPPath(new Ellipse2D.Double(0, 0, 100, 100), blue, new BasicStroke(6), yellow) {
            addInputEventListener(new CursorHandler)
            this.addDragListener((dx: Double, dy: Double) => translate(dx, dy))
            setOffset(200,200)
          }
          val text = this add new PText("Hello"){
            this.addMousePressListener( ()=>setText("Mouse Pressed"))
          }
          text centered_below circle
          this add new PText("Another text")
        }
        setSize(new Dimension(800, 600))
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        setVisible(true)
      }
    }
  }
}