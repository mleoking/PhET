package edu.colorado.phet.motionseries.graphics


import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.Color
import java.awt.geom.{Rectangle2D, Point2D}
import java.text.DecimalFormat
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._
import java.beans.{PropertyChangeEvent, PropertyChangeListener}

class TickMarkSet(transform: ModelViewTransform2D, positionMapper: Double => Point2D, addListener: (() => Unit) => Unit) extends PNode {
  val tickLabels = for (x <- -10 to 10 by 2 if x != 0) yield {
    addTickLabel(x)
  }
  val zeroLabel = addTickLabel(0)
  val metersReadout = new PText("units.meters".translate) { //todo il8n
    setFont(new PhetFont(13, true))
  }
  addChild(metersReadout)

  val changeListener = new PropertyChangeListener() {
    def propertyChange(evt: PropertyChangeEvent) = {
      metersReadout.setOffset(zeroLabel.getFullBounds.getMaxX + 2, zeroLabel.getFullBounds.getMaxY - metersReadout.getFullBounds.getHeight)
    }
  }
  zeroLabel.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, changeListener)
  changeListener.propertyChange(null)

  setPickable(false)
  setChildrenPickable(false)
  def addTickLabel(x: Double) = {
    val path = new PhetPPath(Color.black)
    val label = new PText(new DecimalFormat("0".literal).format(x)) {
      setFont(new PhetFont(18, true))
    }

    addChild(path)
    addChild(label)
    addListener(update)
    def update() = {
      val d = transform.modelToView(positionMapper(x))
      path.setPathTo(new Rectangle2D.Double(d.x, d.y, 2, 2))
      label.setOffset(path.getFullBounds.getCenterX - label.getFullBounds.width / 2, path.getFullBounds.getMaxY)
    }
    update()
    label
  }
}