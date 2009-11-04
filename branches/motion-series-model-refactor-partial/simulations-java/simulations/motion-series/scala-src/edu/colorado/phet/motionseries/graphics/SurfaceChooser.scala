package edu.colorado.phet.motionseries.graphics


import java.awt.{Color}
import javax.swing._
import edu.colorado.phet.motionseries.model.SurfaceModel
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.MotionSeriesDefaults

import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolox.pswing.PSwing

class SurfaceChooser(surfaceModel: SurfaceModel) extends PNode {
  val panel = new JPanel
  panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
  for (surfaceType <- surfaceModel.surfaceTypes) {
    val myRadioButton: MyRadioButton = new MyRadioButton(surfaceType.name, surfaceModel.surfaceType = surfaceType, surfaceModel.surfaceType == surfaceType, surfaceModel.addListener)
    val component = new JPanel
    component.add(myRadioButton.peer)
    myRadioButton.peer.setBackground(MotionSeriesDefaults.EARTH_COLOR)
    val jLabel = new JLabel(new ImageIcon(MotionSeriesResources.getImage(surfaceType.imageFilename)))
    jLabel.setBackground(MotionSeriesDefaults.EARTH_COLOR)
    component.add(jLabel)
    component.setBackground(MotionSeriesDefaults.EARTH_COLOR)
    panel.add(component)
  }
  panel.setBorder(BorderFactory.createLineBorder(Color.blue))
  panel.setBackground(MotionSeriesDefaults.EARTH_COLOR)

  addChild(new PSwing(panel))
}