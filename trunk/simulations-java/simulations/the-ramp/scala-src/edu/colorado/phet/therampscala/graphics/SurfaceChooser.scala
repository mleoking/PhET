package edu.colorado.phet.therampscala.graphics


import java.awt.{Color}
import javax.swing._
import model.SurfaceModel
import scalacommon.swing.MyRadioButton
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

  val panel = new JPanel
  panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
  for (surfaceType <- surfaceModel.surfaceTypes) {
    val myRadioButton: MyRadioButton = new MyRadioButton(surfaceType.name, surfaceModel.surfaceType = surfaceType, surfaceModel.surfaceType == surfaceType, surfaceModel.addListener)
    //    myRadioButton.peer.setIcon(new ImageIcon(RampResources.getImage(surfaceType.imageFilename)))
    val component = new JPanel
    component.add(myRadioButton.peer)
    myRadioButton.peer.setBackground(RampDefaults.EARTH_COLOR)
    val jLabel = new JLabel(new ImageIcon(RampResources.getImage(surfaceType.imageFilename)))
    jLabel.setBackground(RampDefaults.EARTH_COLOR)
    component.add(jLabel)
    component.setBackground(RampDefaults.EARTH_COLOR)
    panel.add(component)
  }
  panel.setBorder(BorderFactory.createLineBorder(Color.blue))
  panel.setBackground(RampDefaults.EARTH_COLOR)

  addChild(new PSwing(panel))
}