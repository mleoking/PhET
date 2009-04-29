package edu.colorado.phet.therampscala.graphics


import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 28, 2009
 * Time: 10:01:33 PM
 * To change this template use File | Settings | File Templates.
 */

class SurfaceChooser(surfaceModel: SurfaceModel) extends PNode {
  addChild(new PText("Choose Surface"))
}