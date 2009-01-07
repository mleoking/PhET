package testing

import edu.umd.cs.piccolo.nodes.PPath
import edu.umd.cs.piccolo.PNode
import java.awt.geom.Line2D

class RampTrackNode(track: RampTrack) extends PNode {
  val node = new PPath(new Line2D.Double(track.getStartLocation.x, track.getStartLocation.y, track.getEndLocation.x, track.getEndLocation.y))
  addChild(node)
}