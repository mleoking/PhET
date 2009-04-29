package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import eatingandexercise.view.SliderNode
import java.awt.Dimension
import java.util.{Hashtable, Dictionary}
import javax.swing.{SwingConstants, ImageIcon, JLabel, JSlider}
import swing.ScalaValueControl
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import umd.cs.piccolox.pswing.PSwing

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 28, 2009
 * Time: 10:01:33 PM
 * To change this template use File | Settings | File Templates.
 */

class SurfaceChooser(surfaceModel: SurfaceModel) extends PNode {
  addChild(new PText("Choose Surface"))
  val control = new ScalaValueControl(0, 6, "friction", "0.00", "", surfaceModel.friction, surfaceModel.friction = _, surfaceModel.addListener)
  control.addTickLabel(0.0, toLabel("Ice", "ice.gif"))
  control.addTickLabel(3.0, toLabel("Concrete", "concrete.gif"))
  control.addTickLabel(6.0, toLabel("Carpet", "carpet.gif"))
  control.getSlider.setPreferredSize(new Dimension(control.getSlider.getPreferredSize.width * 2, control.getSlider.getPreferredSize.height))
  addChild(new PSwing(control))

  def toLabel(s: String, filename: String) = new JLabel(s, new ImageIcon(RampResources.getImage("robotmovingcompany/" + filename)), SwingConstants.LEADING)
}