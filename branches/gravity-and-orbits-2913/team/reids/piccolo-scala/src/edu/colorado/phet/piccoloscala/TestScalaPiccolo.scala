package edu.colorado.phet.piccoloscala

import java.awt.geom.Ellipse2D
import java.awt.{BasicStroke, Color, Dimension}
import edu.colorado.phet.piccoloscala.Predef._
import Color._
import javax.swing.JFrame._
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath

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
            var numTimes = 0
            this.addMousePressListener( ()=>{numTimes = numTimes + 1; setText("Mouse Pressed "+numTimes+" times")})
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