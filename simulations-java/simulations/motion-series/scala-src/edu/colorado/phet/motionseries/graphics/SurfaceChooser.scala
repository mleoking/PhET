package edu.colorado.phet.motionseries.graphics


import java.awt.{Color}
import javax.swing._
import model.SurfaceModel
import scalacommon.swing.MyRadioButton
import motionseries.MotionSeriesResources
import motionseries.MotionSeriesDefaults

import umd.cs.piccolo.PNode
import umd.cs.piccolox.pswing.PSwing

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