package testing

import edu.umd.cs.piccolo.nodes.PPath
import edu.umd.cs.piccolo.PNode
import java.awt.Color

class RampObjectNode(rampObject: RampObject) extends PNode {
  val x = new PPath()
  x.setPathToRectangle(0, 0, 100, 100)
  x.setPaint(Color.blue)

  rampObject.addListener((r: RampObject) => x.setOffset(r.getPosition.x, r.getPosition.y))
  addChild(x)
}