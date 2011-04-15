package edu.colorado.phet.forcelawlab

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import java.awt.{TexturePaint, Color, BasicStroke}
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.nodes.PImage
import java.awt.geom.{Rectangle2D, Ellipse2D, Line2D}

class CharacterNode(mass: Mass, mass2: Mass, transformMV: ModelViewTransform2D, leftOfObject: Boolean, gravityForce: () => Double,
                    minDragX: () => Double, maxDragX: () => Double) extends PNode {
  val shadowNode = new PhetPPath(Color.gray)
  addChild(shadowNode)

  val ropeNode = new PhetPPath(null: BasicStroke, null)
  addChild(ropeNode)
  mass.addListener(update)
  mass2.addListener(update)

  addInputEventListener(new DragHandler(mass, transformMV, minDragX, maxDragX, this))
  addInputEventListener(new CursorHandler)

  def update() = {
    updateRopeNode()
    updateCharacterNode()
    val b = characterImageNode.getFullBounds
    val shadowHeight = b.getHeight / 14.0
    val expandX = 5
    shadowNode.setPathTo(new Ellipse2D.Double(b.x - expandX, b.getMaxY - shadowHeight, b.width + expandX * 2, shadowHeight))
  }

  def ropeStart = transformMV.modelToView(mass.position)

  lazy val sign = if ( leftOfObject ) {
    -1
  }
  else {
    1
  }

  def ropeLength = transformMV.modelToViewDifferentialX(mass.radius) + 100

  def ropeEnd = ropeStart + new Vector2D(ropeLength * sign, 0)

  def updateRopeNode() = {
    ropeNode.setPathTo(new BasicStroke(5).createStrokedShape(new Line2D.Double(ropeStart, ropeEnd)))
    val im = BufferedImageUtils.multiScaleToHeight(ForceLawLabResources.getImage("rope-pattern.png"), 5)

    val texturePaint = new TexturePaint(im, new Rectangle2D.Double(ropeEnd.x, 0, im.getWidth, im.getHeight))
    ropeNode.setPaint(texturePaint)
  }

  def forceAmount = {
    val minForceToShow = 0.00000000064
    val maxForceToShow = 0.00000000989 * 2
    val a = new edu.colorado.phet.common.phetcommon.math.Function.LinearFunction(minForceToShow, maxForceToShow, 0, 14).evaluate(gravityForce()).toInt
    ( a max 0 ) min 14
  }

  val scale = 0.5

  def getImage = {
    val im = ForceLawLabResources.getImage("pull-figure/pull_figure_" + forceAmount + ".png")
    val flipIm = if ( leftOfObject ) {
      BufferedImageUtils.flipX(im)
    }
    else {
      im
    }
    BufferedImageUtils.multiScale(flipIm, 0.5)
  }

  val ropeHeightFromImageBase = 103 * scale
  val characterImageNode = new PImage(getImage)
  addChild(characterImageNode)
  update()

  def updateCharacterNode() = {
    characterImageNode.setImage(getImage)
    characterImageNode.setOffset(ropeEnd)
    characterImageNode.translate(0, ropeHeightFromImageBase - characterImageNode.getFullBounds.getHeight)
    if ( leftOfObject ) {
      characterImageNode.translate(-characterImageNode.getFullBounds.getWidth, 0)
    }
    characterImageNode.translate(-40 * sign * scale, 0) //move closer to rope, since graphic offset increases as force increases
    characterImageNode.translate(-forceAmount * sign * 1.5 * scale, 0) //step closer to rope, to keep rope constant length
  }

}