package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.umd.cs.piccolo.PNode
import java.awt.geom.AffineTransform
import edu.umd.cs.piccolo.nodes.PImage
import java.awt.image.BufferedImage
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.model.{ForcesAndMotionObject, ForceMotionSeriesObject, MotionSeriesObject}
import edu.colorado.phet.motionseries.javastage.stage.PlayArea

class ForceDragMotionSeriesObjectNode(motionSeriesObject: ForceMotionSeriesObject,
                                      transform: ModelViewTransform2D,
                                      imageName: String,
                                      crashImageName: String,
                                      dragListener: () => Unit)
        extends MotionSeriesObjectNode(motionSeriesObject, transform, imageName, crashImageName) {
  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      val delta = event.getCanvasDelta
      val modelDelta = transform.viewToModelDifferential(delta.width, delta.height)
      val sign = modelDelta dot motionSeriesObject.rampUnitVector
      dragListener() //it makes more sense to call the drag listener last after setting the parallel applied force.  However, in GoButton's visibility model,
      //the go button is relying on the sim starting before the applied force gets set as part of the logic to decide whether to show the go button.  The go button
      //should not appear when the user drags the object (which was happening when dragListener() was called after setting the applied force.
      motionSeriesObject.parallelAppliedForce = motionSeriesObject.parallelAppliedForce + sign / MotionSeriesDefaults.PLAY_AREA_FORCE_VECTOR_SCALE
    }

    override def mouseReleased(event: PInputEvent) = {
      motionSeriesObject.parallelAppliedForce = 0.0
    }
  })
}

class PositionDragMotionSeriesObjectNode(motionSeriesObject: ForcesAndMotionObject,
                                         transform: ModelViewTransform2D,
                                         imageName: String,
                                         leftImageName: String,
                                         dragListener: () => Unit,
                                         canvas: PlayArea)
        extends MotionSeriesObjectNode(motionSeriesObject, transform, imageName, imageName) {
  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      motionSeriesObject.setPositionMode()
      val delta = event.getCanvasDelta
      //todo: make it so we can get this information (a) more easily and (b) without a reference to the canvas:MyCanvas
      val screenDelta = canvas.canvasToStageDelta(delta.getWidth, delta.getHeight)
      val modelDelta = canvas.getModelStageTransform.viewToModelDifferential(screenDelta.getWidth(), screenDelta.getHeight())
      motionSeriesObject.setDesiredPosition(motionSeriesObject.desiredPosition + modelDelta.x)
      dragListener()
    }

    override def mouseReleased(event: PInputEvent) = {
    }

    override def mousePressed(event: PInputEvent) = {
      motionSeriesObject.setDesiredPosition(motionSeriesObject.position)
    }
  })
  update()

  override def update() = {
    updateImage() //update image first in case superclass uses its size to do layout things
    super.update()
  }

  def updateImage() = {
    val image = if (motionSeriesObject.velocity < -1E-8) MotionSeriesResources.getImage(leftImageName)
    else if (motionSeriesObject.velocity > 1E-8) BufferedImageUtils.flipX(MotionSeriesResources.getImage(leftImageName))
    else MotionSeriesResources.getImage(imageName)
    imageNode.setImage(image)
  }
}

class MotionSeriesObjectNode(motionSeriesObject: MotionSeriesObject,
                             transform: ModelViewTransform2D,
                             private var image: BufferedImage,
                             private var crashImage: BufferedImage)
        extends PNode {
  def this(motionSeriesObject: MotionSeriesObject, transform: ModelViewTransform2D, imageName: String, crashImageName: String) = this (motionSeriesObject, transform, MotionSeriesResources.getImage(imageName), MotionSeriesResources.getImage(crashImageName))

  def this(motionSeriesObject: MotionSeriesObject, transform: ModelViewTransform2D, imageName: String) = this (motionSeriesObject, transform, MotionSeriesResources.getImage(imageName), MotionSeriesResources.getImage(imageName))

  val imageNode = new PImage(image)

  //This is to support showing crash images during game mode
  //todo: refactor game mode to use the motionSeriesObject.isCrashed model value
  motionSeriesObject.crashListeners += (() => {
    imageNode.setImage(crashImage)
  })

  //todo: images were specified in constructor; why can they be overidden like this?
  def setImages(im: BufferedImage, crashIm: BufferedImage) = {
    image = im
    crashImage = crashIm
    val imageToSet = if (motionSeriesObject.isCrashed) crashImage else image
    if (!(imageNode.getImage eq imageToSet)) //avoid redraw if possible
      imageNode.setImage(imageToSet)
    update()
  }
  addChild(imageNode)

  def update() = {
    //TODO: consolidate/refactor with BugNode, similar graphics transform code
    imageNode.setTransform(new AffineTransform)

    val modelPosition = motionSeriesObject.position2D
    val viewPosition = transform.modelToView(modelPosition)
    val delta = new Vector2D(imageNode.getImage.getWidth(null), imageNode.getImage.getHeight(null))

    val scale = -transform.modelToViewDifferentialYDouble(motionSeriesObject.height) / imageNode.getImage.getHeight(null)

    imageNode.translate(viewPosition.x - delta.x / 2 * scale, viewPosition.y - delta.y * scale)
    imageNode.scale(scale)

    val angle = if (motionSeriesObject.isCrashed) 0.0 else motionSeriesObject.getAngle
    val vec = new Vector2D(angle)
    val flipY = new Vector2D(vec.x, -vec.y)

    imageNode.rotateAboutPoint(flipY.angle,
      imageNode.getFullBounds.getCenter2D.getX - (viewPosition.x - delta.x / 2),
      imageNode.getFullBounds.getMaxY - (viewPosition.y - delta.y))

    if (imageNode.getImage == crashImage && !motionSeriesObject.isCrashed) {
      imageNode.setImage(image)
    }
  }
  motionSeriesObject.addListener(update)
  update()
}
