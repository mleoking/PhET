package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes._
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.MotionSeriesDefaults

import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesConfig
import java.awt.Color
import edu.colorado.phet.motionseries.model._
import MotionSeriesConfig._
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}

/**
 * The VectorNode is the PNode that draws the Vector (e.g. a force vector) either in the free body diagram or directly on the object itself.
 *
 * todo: could improve performance by passing isContainerVisible:()=>Boolean and addContainerVisibleListener:(()=>Unit)=>Unit
 * @author Sam Reid
 */
class VectorNode(val transform: ModelViewTransform2D, val vector: Vector, val tailLocation: Vector2DModel, maxLabelDistance: Double, vectorLengthScale: Double) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 1), VectorHeadWidth(), VectorHeadWidth(), VectorTailWidth(), 0.5, true) {
    setPaint(vector.getPaint)
  }
  MotionSeriesConfig.VectorTailWidth.addListener(() => {arrowNode.setTailWidth(MotionSeriesConfig.VectorTailWidth.value)})
  MotionSeriesConfig.VectorHeadWidth.addListener(() => {
    arrowNode.setHeadWidth(MotionSeriesConfig.VectorHeadWidth.value)
    arrowNode.setHeadHeight(MotionSeriesConfig.VectorHeadWidth.value)
  })

  addChild(arrowNode)
  private val abbreviatonTextNode = {
    val html = new ShadowHTMLNode(vector.html) {
      setShadowColor(vector.color)
      setColor(Color.black)
      setShadowOffset(2, 2)
      setFont(new PhetFont(22, false))
    }
    //for performance, buffer these outlines; htmlnodes are very processor intensive, each outline is 5 htmlnodes and there are many per sim
    new PImage(html.toImage)
  }
  addChild(abbreviatonTextNode)

  /**
   * Method object that updates the graphics for the arrow node and the corresponding textual label.
   * Note that we use val instead of def since eta-expansion makes == and array -= impossible, and we need to be able
   * to remove this callback to avoid memory leaks.
   */

  //This update mechanism is expensive, and there are many vectors to update.
  //Since there are 2 dependencies that this class listens for, we bunch up the notifications
  //so that update is only called when necessary. 
  case class UpdateState(visible: Boolean, tip: Vector2D, tail: Vector2D)
  private var lastUpdateState = new UpdateState(true, new Vector2D, new Vector2D(123, 456)) //create with dummy data to ensure changed on first update()

  val update = () => {
    val viewTip = transform.modelToViewDouble(vector.vector2DModel() * vectorLengthScale + tailLocation.value)
    val viewTail = transform.modelToViewDouble(tailLocation.value)
    val updateState = new UpdateState(vector.visible.booleanValue, viewTail, viewTip)
    val stayedInvisible = !updateState.visible && !lastUpdateState.visible
    if (updateState != lastUpdateState && !stayedInvisible) { //skip expensive updates if no change
      //      println("Updating "+vector.abbreviation)
      setVisible(vector.visible.booleanValue)
      //Update the arrow node itself
      arrowNode.setTipAndTailLocations(viewTip, viewTail)

      //Update the location of the text label
      val textLocation = {
        val proposedLabelLocation = vector.vector2DModel() * 0.6 * vectorLengthScale
        val minLabelDistance = maxLabelDistance / 2.0 //todo: improve heuristics for min label distance, or make it settable in the constructor
        val labelVector = if (proposedLabelLocation.magnitude > maxLabelDistance)
          new Vector2D(vector.angle) * maxLabelDistance
        else if (proposedLabelLocation.magnitude < minLabelDistance && proposedLabelLocation.magnitude > 1E-2)
          new Vector2D(vector.angle) * minLabelDistance
        else
          proposedLabelLocation

        val viewPt = transform.modelToViewDouble(labelVector + tailLocation.value)

        //vector.angle is negative since the coordinate frame is flipped going from model to view
        val deltaArrow = new Vector2D(-vector.angle - vector.labelAngle) * abbreviatonTextNode.getFullBounds.getHeight * 0.56 //move orthogonal to the vector itself
        deltaArrow + viewPt
      }
      abbreviatonTextNode.setOffset(textLocation.x - abbreviatonTextNode.getFullBounds.getWidth / 2, textLocation.y - abbreviatonTextNode.getFullBounds.getHeight / 2 +
              (if (vector.labelAngle == 0) -abbreviatonTextNode.getFullBounds.getHeight / 2 else 0)) //Net force vector label should always be above, see MotionSeriesObject
      abbreviatonTextNode.setVisible(viewTail.distance(viewTip) > 1)
      lastUpdateState = updateState
    } else {
      //      println("Skipping "+vector.abbreviation)
    }
  }
  update()
  vector.vector2DModel.addListener(update)
  tailLocation.addListener(update)
  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = update()
  })

  setPickable(false)
  setChildrenPickable(false)

  def deleting() {
    tailLocation.removeListener(update)
  }
}

/**
 * This VectorNode subclass is located directly on a MotionSeriesObject
 */
class BodyVectorNode(transform: ModelViewTransform2D,
                     vector: Vector,
                     offset: Vector2DModel,
                     motionSeriesObject: MotionSeriesObject,
                     vectorLengthScale: Double)
        extends VectorNode(transform, vector, offset, MotionSeriesDefaults.BODY_LABEL_MAX_OFFSET, vectorLengthScale) {
  def doUpdate() = {
    setOffset(motionSeriesObject.position2D)
    update()
  }

  //TODO: only listen to position of motion series object
  motionSeriesObject.addListenerByName {
    doUpdate()
  }
  doUpdate()
}