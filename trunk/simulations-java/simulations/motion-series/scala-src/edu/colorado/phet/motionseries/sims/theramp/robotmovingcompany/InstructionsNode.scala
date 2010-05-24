package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.umd.cs.piccolo.nodes.{PText}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._

class InstructionsNode extends PNode {
  val iconSet = new KeyboardButtonIcons {
    scale(0.4)
  }
  addChild(iconSet)
  val textNode = new PText("game.instructions.press-arrow-keys".translate) {
    setOffset(iconSet.getFullBounds.getCenterX - getFullBounds.getWidth / 2, iconSet.getFullBounds.getMaxY)
  }
  addChild(textNode)
}