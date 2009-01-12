package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.movingman.ladybug.Ladybug
import edu.umd.cs.piccolo.event.PBasicInputEventHandler
import edu.umd.cs.piccolo.event.PInputEvent
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.nodes.PPath
import java.awt.Color

class LadybugNode(ladybug: Ladybug) extends PNode {
  val x = new PPath()
  x.setPathToRectangle(0, 0, 100, 100)
  x.setPaint(Color.blue)

  ladybug.addListener((r: Ladybug) => x.setOffset(r.getPosition.x, r.getPosition.y))
  addChild(x)

  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = ladybug.translate(event.getCanvasDelta.width, event.getCanvasDelta.height)
  })
}