package edu.colorado.phet.scalacommon

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton
import edu.colorado.phet.scalacommon.math.Vector2D
import scala.swing.Component
import java.awt.geom.Point2D
import edu.umd.cs.piccolo.util.PDimension
import view.MyButton

object Predef {
  implicit def pdimensonToVector2D(dim: PDimension) = new Vector2D(dim.width, dim.height)

  implicit def vector2DToPoint(vector: Vector2D) = new Point2D.Double(vector.x, vector.y)

  implicit def pointToVector2D(point: Point2D) = new Vector2D(point.getX, point.getY)

  implicit def scalaSwingToAWT(component: Component) = component.peer

  implicit def fnToActionListener(fn: () => Unit) = new ActionListener() {
    def actionPerformed(e: ActionEvent) = {fn()}
  }

  implicit def buttonToMyButton(b: JButton) = new MyButton(b)

  /**See docs in usage*/
  def defineInvokeAndPass(m: (=> Unit) => Unit)(block: => Unit): () => Unit = {
    block
    m(block)
    block _
  }

  /* This allows to put debug statements in-line with calls instead of separately
  Sample usage:
  model.setValue(debug eval computeModelValue());
  //
   */
  object debug {
    def eval[T](a: => T) = {
      val result = a
      println(a)
      result
    }

    def evals[T](block: => Tuple2[String, T]) = {
      val blockResult = block
      val name = blockResult._1
      val value = blockResult._2
      println(name + " = " + value)
      value
    }
  }
}