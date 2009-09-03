package edu.colorado.phet.motionseries.graphics

import model.{FireDog, Raindrop, MotionSeriesModel}
import umd.cs.piccolo.PNode
import motionseries.MotionSeriesResources._

//todo: factor out common code
class FireDogView(rampModel: MotionSeriesModel, canvas: MotionSeriesCanvas) extends PNode {
  rampModel.fireDogAddedListeners += ((added: FireDog) => {
    val node = new BeadNode(added.dogbead, canvas.transform, "firedog.gif".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node)) //eleganter than ever
  })
}

class RaindropView(rampModel: MotionSeriesModel, canvas: MotionSeriesCanvas) extends PNode {
  rampModel.raindropAddedListeners += ((added: Raindrop) => {
    val node = new BeadNode(added.rainbead, canvas.transform, "raindrop.png".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node))
  })
}