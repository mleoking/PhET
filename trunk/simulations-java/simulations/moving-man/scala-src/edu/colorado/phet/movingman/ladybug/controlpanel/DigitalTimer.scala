package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.view.util.PhetFont
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.{BasicStroke, Color}
import java.text.DecimalFormat
import model.LadybugModel
import umd.cs.piccolo.nodes.{PPath, PText}
import umd.cs.piccolo.PNode

class DigitalTimer(m:LadybugModel) extends PNode {
  val text = new PText("123.27 sec")
  text.setFont(new PhetFont(42))

  val background = new PhetPPath(text.getFullBounds, Color.lightGray,new BasicStroke(1),Color.darkGray)
  addChild(background)
  addChild(text)

  m.addListenerByName(update())
  update
  def update()={
    text.setText(new DecimalFormat("0.00").format(m.getTime)+" sec")
  }
}