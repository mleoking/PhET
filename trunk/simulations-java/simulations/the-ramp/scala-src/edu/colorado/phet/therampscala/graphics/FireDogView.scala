package edu.colorado.phet.therampscala.graphics

import model.RampModel
import umd.cs.piccolo.PNode
import RampResources._

class FireDogView(rampModel: RampModel, canvas: AbstractRampCanvas) extends PNode{
  rampModel.fireDogAddedListeners += ((added: rampModel.MyFireDog) => {
    val node = new BeadNode(added.dogbead, canvas.transform, "firedog.gif".literal)
    addChild(node)

    //todo: add a listener to this exact model object, not the composite
    added.removedListeners += (() => removeChild(node)) //eleganter than ever
  })
}

class RaindropView(rampModel: RampModel, canvas: AbstractRampCanvas) extends PNode{
  rampModel.raindropAddedListeners += ((added: rampModel.Raindrop) => {
    val node = new BeadNode(added.rainbead, canvas.transform, "drop3.gif".literal)
    addChild(node)

    //todo: add a listener to this exact drop
    added.removedListeners += (() => removeChild(node)) //eleganter than ever
  })
}