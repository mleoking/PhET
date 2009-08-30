package edu.colorado.phet.therampscala.graphics

import model.RampModel
import umd.cs.piccolo.PNode
import RampResources._

class FireDogView(rampModel: RampModel, canvas: AbstractRampCanvas) extends PNode {
  rampModel.fireDogAddedListeners += ((added: rampModel.FireDog) => {
    val node = new BeadNode(added.dogbead, canvas.transform, "firedog.gif".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node)) //eleganter than ever
  })
}

class RaindropView(rampModel: RampModel, canvas: AbstractRampCanvas) extends PNode {
  rampModel.raindropAddedListeners += ((added: rampModel.Raindrop) => {
    val node = new BeadNode(added.rainbead, canvas.transform, "raindrop.png".literal)
    addChild(node)

    added.removedListeners += (() => removeChild(node))
  })
}