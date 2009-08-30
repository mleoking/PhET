package edu.colorado.phet.therampscala.graphics

import model.{FireDog, Raindrop, RampModel}
import umd.cs.piccolo.PNode
import RampResources._

//todo: factor out common code
class FireDogView(rampModel: RampModel, canvas: AbstractRampCanvas) extends PNode {
  rampModel.fireDogAddedListeners += ((added: FireDog) => {
    val node = new BeadNode(added.dogbead, canvas.transform, "firedog.gif".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node)) //eleganter than ever
  })
}

class RaindropView(rampModel: RampModel, canvas: AbstractRampCanvas) extends PNode {
  rampModel.raindropAddedListeners += ((added: Raindrop) => {
    val node = new BeadNode(added.rainbead, canvas.transform, "raindrop.png".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node))
  })
}