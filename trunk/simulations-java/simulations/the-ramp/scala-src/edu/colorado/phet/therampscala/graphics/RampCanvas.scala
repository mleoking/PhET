package edu.colorado.phet.therampscala.graphics


import java.awt.Color
import model.RampModel
import scalacommon.math.Vector2D
import scalacommon.Predef._

class RampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel) extends DefaultCanvas(22, 20) {
  setBackground(new Color(200, 255, 240))

  addNode(new SkyNode(transform))
  addNode(new EarthNode(transform))

  addNode(new RampSegmentNode(model.rampSegments(0), transform))
  addNode(new RotatableSegmentNode(model.rampSegments(1), transform))

  addNode(new RampHeightIndicator(model.rampSegments(1), transform))
  addNode(new RampAngleIndicator(model.rampSegments(1), transform))

  addNode(new BeadNode(model.leftWall, transform, "barrier2.jpg"))
  addNode(new BeadNode(model.rightWall, transform, "barrier2.jpg"))
  addNode(new BeadNode(model.tree, transform, "tree.gif"))

  val cabinetNode = new DraggableBeadNode(model.beads(0), transform, "cabinet.gif")
  model.addListenerByName(cabinetNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(cabinetNode)

  addNode(new PusherNode(transform, model.beads(0), model.manBead))
  addNode(new AppliedForceSliderNode(model.beads(0), transform))

  addNode(new ObjectSelectionNode(transform, model))

  addNode(new CoordinateFrameNode(model, coordinateSystemModel, transform))

  val fbdNode = new FreeBodyDiagramNode(200, 200, 100, 100, model.coordinateFrameModel, coordinateSystemModel.adjustable)
  fbdNode.setOffset(10, 10)
  val appliedForceVector = new Vector(RampDefaults.appliedForceColor, "Applied Force", "<html>F<sub>a</sub></html>") {
    def getValue = model.beads(0).appliedForce
  }

  fbdNode.addVector(appliedForceVector)
  fbdNode.addVector(model.beads(0).gravityForceVector)
  model.beads(0).addListenerByName(appliedForceVector.notifyListeners())
  addNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {fbdNode.setVisible(freeBodyDiagramModel.visible)}


}