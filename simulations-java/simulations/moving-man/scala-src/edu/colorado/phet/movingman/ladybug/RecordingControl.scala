package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.Dimension
import javax.swing.JPanel
import umd.cs.piccolo.nodes.PText
import LadybugUtil._

class RecordingControl extends PhetPCanvas {
  val text = new PText("0.00")
  addScreenChild(text)
  setPreferredSize(new Dimension(200, 100))
}