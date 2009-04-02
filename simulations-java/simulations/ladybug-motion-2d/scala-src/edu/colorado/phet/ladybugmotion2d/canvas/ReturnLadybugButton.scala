package edu.colorado.phet.ladybug2d.canvas

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.PhetPNode
import scala.swing.Button
import java.awt.event.{ActionEvent, ComponentAdapter, ComponentEvent, ActionListener}
import java.awt.geom.Point2D
import java.awt.{Rectangle, Graphics2D, Graphics}
import javax.swing.JButton
import model.LadybugModel
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import umd.cs.piccolox.pswing.PSwing
import java.awt.Color._

class ReturnLadybugButton(model: LadybugModel, canvas: LadybugCanvas) extends PhetPNode {
  private val button = new JButton("Return Ladybug")
  button.addActionListener(() => model.returnLadybug)
  button.setFont(new PhetFont(20))
  addChild(new PSwing(button))
  model.addListener(updateVisible)
  updateVisible()
  updateLocation()
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = {
      updateLocation
      updateVisible
    }
  })

  def updateLocation() = setOffset(canvas.getWidth / 2 - getFullBounds.getWidth / 2, canvas.getHeight / 2 - getFullBounds.getHeight / 2)

  def updateVisible() = setVisible(shouldBeVisible())

  def shouldBeVisible() = {
    val globalPosition = canvas.ladybugNode.localToGlobal(canvas.ladybugNode.getLadybugCenter)
    !(new Rectangle(0, 0, canvas.getWidth, canvas.getHeight).contains(globalPosition))
  }
}