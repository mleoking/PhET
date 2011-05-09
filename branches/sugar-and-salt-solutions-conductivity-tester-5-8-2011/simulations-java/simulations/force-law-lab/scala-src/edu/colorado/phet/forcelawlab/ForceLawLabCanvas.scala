package edu.colorado.phet.forcelawlab

import collection.mutable.ArrayBuffer
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.{Font, Color}
import edu.colorado.phet.common.piccolophet.nodes._
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import java.text.NumberFormat

/**
 * Canvas for Force Law Lab
 */
class ForceLawLabCanvas(model: ForceLawLabModel, modelWidth: Double, mass1Color: Color, mass2Color: Color, backgroundColor: Color,
                        rulerLength: Long, numTicks: Long, rulerLabel: String, tickToString: Long => String,
                        forceLabelScale: Double, forceArrowNumberFormat: NumberFormat, magnification: Magnification, units: UnitsContainer) extends DefaultCanvas(modelWidth, modelWidth) {
  setBackground(backgroundColor)

  val tickIncrement = rulerLength / numTicks

  val ticks = new ArrayBuffer[Long]
  private var _x = 0L
  while ( _x <= rulerLength ) {
    //no high level support for 1L to 5L => Range[Long]
    ticks += _x
    _x += tickIncrement
  }

  def maj = for ( i <- 0 until ticks.size ) yield {
    if ( i == 1 && tickToString(ticks(i)).length > 1 ) {
      ""
    } //todo improve heuristic for overlapping text
    else if ( i == ticks.size - 1 && tickToString(ticks(i)).length > 5 ) {
      ""
    } //skip last tick label in km if it is expected to go off the ruler
    else {
      tickToString(ticks(i))
    }
  }

  val rulerNode = {
    val dx = transform.modelToViewDeltaX(rulerLength)
    new RulerNode(dx, 14, 40, maj.toArray, new PhetFont(Font.BOLD, 16), rulerLabel, new PhetFont(Font.BOLD, 16), 4, 10, 6);
  }
  units.addListenerByName {
                            rulerNode.setUnits(units.units.name)
                            rulerNode.setMajorTickLabels(maj.toArray)
                          }

  def resetRulerLocation() {
    rulerNode.setOffset(150, 500)
  }

  resetRulerLocation()

  def updateRulerVisible() {}

  //rulerNode.setVisible(!magnification.magnified)
  magnification.addListenerByName(updateRulerVisible())
  updateRulerVisible()

  def opposite(c: Color) = new Color(255 - c.getRed, 255 - c.getGreen, 255 - c.getBlue)

  val minDragX = () => transform.viewToModelX(getVisibleModelBounds.getMinX)
  val maxDragX = () => transform.viewToModelX(getVisibleModelBounds.getMaxX)

  addNode(new CharacterNode(model.m1, model.m2, transform, true, () => model.getGravityForce.magnitude, minDragX, model.mass1MaxX))
  addNode(new CharacterNode(model.m2, model.m1, transform, false, () => model.getGravityForce.magnitude, model.mass2MinX, maxDragX))
  addNode(new DraggableMassNode(model.m1, transform, mass1Color, minDragX, model.mass1MaxX, magnification, () => 10))
  addNode(new DraggableMassNode(model.m2, transform, mass2Color, model.mass2MinX, maxDragX, magnification, () => -10))
  addNode(new ForceLabelNode(model.m1, model.m2, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat, 100, true))
  addNode(new ForceLabelNode(model.m2, model.m1, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat, 200, false))
  rulerNode.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) {
      rulerNode.translate(event.getDeltaRelativeTo(rulerNode.getParent).width, event.getDeltaRelativeTo(rulerNode.getParent).height)
    }
  })
  rulerNode.addInputEventListener(new CursorHandler)
  addNode(rulerNode)
}
