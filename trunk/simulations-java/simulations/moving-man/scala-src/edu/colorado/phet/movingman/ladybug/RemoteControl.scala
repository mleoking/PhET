package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import _root_.edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import _root_.edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{MouseEvent, MouseAdapter}
import java.awt.geom.{Rectangle2D, Point2D, Dimension2D}
import java.awt.{Rectangle, Dimension}
import javax.swing.event.MouseInputAdapter
import javax.swing.{JButton, JRadioButton, JPanel, JLabel}
import umd.cs.piccolo.util.PDimension
import LadybugUtil._

class RemoteControl(model: LadybugModel) extends VerticalLayoutPanel {
  class RemoteControlCanvas extends PhetPCanvas(new PDimension(120, 120)) {
    val transform = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(120, 120), false)
    val arrowNode = new ArrowNode(transform.modelToView(new Point2D.Double(0, 0)), transform.modelToView(new Point2D.Double(5, 5)), 10, 10, 5, 2, true)
    arrowNode.setPaint(LadybugColorSet.position)
    addWorldChild(arrowNode)
    def setDestination(pt: Point2D) = {
      model.ladybug.setPosition(pt)
      arrowNode.setTipAndTailLocations(transform.modelToView(pt), transform.modelToView(new Point2D.Double(0, 0)))
    }
    addMouseListener(new MouseInputAdapter() {
      override def mousePressed(e: MouseEvent) = setDestination(transform.viewToModel(e.getX, e.getY))
    })
    addMouseMotionListener(new MouseInputAdapter() {
      override def mouseDragged(e: MouseEvent) = setDestination(transform.viewToModel(e.getX, e.getY))
    })
  }

  add(new JLabel("Remote Control"))
  val canvas = new RemoteControlCanvas
  canvas.setPreferredSize(new Dimension(120, 120))
  add(canvas)
  object state extends ObservableS
  add(new MyRadioButton("Position", () => {}, true, state))
  add(new MyRadioButton("Velocity", () => {}, true, state))
  add(new JRadioButton("Acceleration"))
  add(new JButton("Go"))
}