package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.Color
import java.awt.geom.Rectangle2D
import model.{BeadVector, Bead, RampModel}
import scalacommon.math.Vector2D
import scalacommon.Predef._
import umd.cs.piccolo.PNode
import java.lang.Math._

class RampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel) extends DefaultCanvas(22, 20) {
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

  val fbdWidth = RampDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable)
  fbdNode.setOffset(10, 10)
  addNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {fbdNode.setVisible(freeBodyDiagramModel.visible)}

  class VectorSetNode(transform: ModelViewTransform2D, bead: Bead) extends PNode {
    def addVector(a: Vector, offset: VectorValue) = {
      val node = new BodyVectorNode(transform, a, offset)
      addChild(node)
    }
  }

  class BodyVectorNode(transform: ModelViewTransform2D, vector: Vector, offset: VectorValue) extends VectorNode(transform, vector, offset) {
    model.beads(0).addListenerByName {
      setOffset(model.beads(0).position2D)
      update
    }
  }

  val vectorNode = new VectorSetNode(transform, model.beads(0))
  addNode(vectorNode)

  def addVector(a: Vector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double) = {
    fbdNode.addVector(a, offsetFBD)


    val tailLocationInPlayArea = new VectorValue() {
      def addListenerByName(listener: => Unit) = {
        model.beads(0).addListenerByName(listener)
        vectorViewModel.addListenerByName(listener)
      }

      def getValue = {
        val defaultCenter = model.beads(0).height / 2.0
        model.beads(0).position2D + new Vector2D(model.beads(0).getAngle + PI / 2) *
                (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else a.getPointOfOriginOffset(defaultCenter)))
      }
    }
    val playAreaAdapter = new Vector(a.color, a.name, a.abbreviation) {
      def getValue = a.getValue * RampDefaults.PLAY_AREA_VECTOR_SCALE
    }
    vectorNode.addVector(playAreaAdapter, tailLocationInPlayArea)
  }

  def addVector(a: BeadVector): Unit = addVector(a, new ConstantVectorValue, 0)
  addVector(model.beads(0).appliedForceVector)
  addVector(model.beads(0).gravityForceVector)
  addVector(model.beads(0).normalForceVector)
  addVector(model.beads(0).frictionForceVector)
  addVector(model.beads(0).wallForceVector)
  addVector(model.beads(0).totalForceVector, new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2)


}
trait PointOfOriginVector {
    def getPointOfOriginOffset(defaultCenter:Double):Double
  }