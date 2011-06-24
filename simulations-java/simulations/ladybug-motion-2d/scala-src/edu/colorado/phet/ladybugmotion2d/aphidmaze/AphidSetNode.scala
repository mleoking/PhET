package edu.colorado.phet.ladybugmotion2d.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.ladybugmotion2d.LadybugMotion2DResources
import edu.colorado.phet.ladybugmotion2d.canvas.BugNode
import edu.umd.cs.piccolo.PNode

class AphidSetNode(model: AphidMazeModel, transform: ModelViewTransform2D) extends PNode {
  //  def updateMe() = {
  //    removeAllChildren
  //    model.aphids.foreach((aphid: Aphid) => addChild(new BugNode(aphid, transform, LadybugMotion2DResources.getImage("valessiobrito_Bug_Buddy_Vec.png"))))
  //  }
  //  updateMe()
  //  model.addObserver(new SimpleObserver(){
  //    def update() = updateMe()
  //  })


  val update = defineInvokeAndPass(model.addListenerByName) {
                                                              removeAllChildren
                                                              model.aphids.foreach((aphid: Aphid) => addChild(new BugNode(aphid, transform, LadybugMotion2DResources.getImage("valessiobrito_Bug_Buddy_Vec.png"))))
                                                            }
}