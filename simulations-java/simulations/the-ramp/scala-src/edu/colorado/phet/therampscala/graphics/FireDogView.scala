package edu.colorado.phet.therampscala.graphics

import model.RampModel

class FireDogView(rampModel: RampModel, canvas: AbstractRampCanvas) {
  rampModel.fireDogAddedListeners += ((added: rampModel.MyFireDog) => {
    val node = new BeadNode(added.dogbead, canvas.transform, "firedog.gif")
    canvas.addWorldChild(node)

    rampModel.fireDogRemovedListeners += (removed => canvas.removeWorldChild(node)) //eleganter than ever
  })
}

class RaindropView(rampModel: RampModel, canvas: AbstractRampCanvas) {
  rampModel.fireDogAddedListeners += ((added: rampModel.MyFireDog) => {
    val node = new BeadNode(added.dogbead, canvas.transform, "firedog.gif")
    canvas.addWorldChild(node)

    rampModel.fireDogRemovedListeners += (removed => canvas.removeWorldChild(node)) //eleganter than ever
  })
}