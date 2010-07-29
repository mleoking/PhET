package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.model.{FireDog, Raindrop, MotionSeriesModel}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._

//todo: factor out common code
class FireDogView(rampModel: MotionSeriesModel, canvas: MotionSeriesCanvas) extends PNode {
  rampModel.fireDogAddedListeners += ((added: FireDog) => {
    val node = new MotionSeriesObjectNode(added.dog, canvas.transform, "firedog.gif".literal)
    addChild(node)

    added.removalListeners += (() => removeChild(node)) //eleganter than ever
  })
}

class RaindropView(rampModel: MotionSeriesModel, canvas: MotionSeriesCanvas) extends PNode {
  rampModel.raindropAddedListeners += ((added: Raindrop) => {
    val node = new MotionSeriesObjectNode(added.motionSeriesObject, canvas.transform, "raindrop.png".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node))
  })
}