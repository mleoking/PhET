package edu.colorado.phet.forcelawlab

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.geom.{Ellipse2D, Line2D, Rectangle2D}
import umd.cs.piccolo.nodes.PImage
import common.phetcommon.view.util.BufferedImageUtils
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import java.awt.{Color, BasicStroke, TexturePaint}
import scalacommon.math.Vector2D
import scalacommon.Predef._

class CharacterNode(mass: Mass, mass2: Mass, transform: ModelViewTransform2D, leftOfObject: Boolean, gravityForce: () => Double) extends PNode {
  val shadowNode = new PhetPPath(Color.gray)
  addChild(shadowNode)

  val ropeNode = new PhetPPath(new BasicStroke(5), Color.black)
  addChild(ropeNode)
  mass2.addListener(update)
  mass.addListener(update)

  def update() = {
    updateRopeNode()
    updateCharacterNode()
    val b = characterImageNode.getFullBounds
    val shadowHeight = b.getHeight / 14.0
    val expandX = 5
    shadowNode.setPathTo(new Ellipse2D.Double(b.x - expandX, b.getMaxY - shadowHeight, b.width+ expandX * 2, shadowHeight))
  }

  def ropeStart = transform.modelToView(mass.position)

  lazy val sign = if (leftOfObject) -1 else 1

  def ropeEnd = {
    val length = transform.modelToViewDifferentialX(mass.radius) + 100
    ropeStart + new Vector2D(length * sign, 0)
  }

  def updateRopeNode() = {
    //    ropeNode.setPathTo(new Line2D.Double(ropeStart, ropeEnd))

    ropeNode.setStroke(null)
    ropeNode.setStrokePaint(null)
    ropeNode.setPathTo(new BasicStroke(5).createStrokedShape(new Line2D.Double(ropeStart, ropeEnd)))
    val im = BufferedImageUtils.multiScaleToHeight(ForceLawLabResources.getImage("rope-pattern.png"), 5)

    val texturePaint = new TexturePaint(im, new Rectangle2D.Double(ropeEnd.x, 0, im.getWidth, im.getHeight))
    ropeNode.setPaint(texturePaint)
  }

  def forceAmount = {
    val minForceToShow = 0.00000000064
    val maxForceToShow = 0.00000000989
    val a = new common.phetcommon.math.Function.LinearFunction(minForceToShow, maxForceToShow, 0, 14).evaluate(gravityForce()).toInt
    (a max 0) min 14
  }

  val scale = 0.5

  def getImage = {
    val im = ForceLawLabResources.getImage("pull-figure/pull_figure_" + forceAmount + ".png")
    val flipIm = if (leftOfObject) BufferedImageUtils.flipX(im) else im
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
    if (leftOfObject) characterImageNode.translate(-characterImageNode.getFullBounds.getWidth, 0)
    characterImageNode.translate(-40 * sign * scale, 0) //move closer to rope, since graphic offset increases as force increases
    characterImageNode.translate(-forceAmount * sign * 1.5 * scale, 0) //step closer to rope, to keep rope constant length
  }

}