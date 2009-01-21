//package edu.colorado.phet.movingman.ladybug.controlpanel
//
//import edu.colorado.phet.common.phetcommon.view.util.PhetFont
//import edu.colorado.phet.common.piccolophet.event.CursorHandler
//import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
//import edu.colorado.phet.common.piccolophet.PhetPCanvas
//import java.awt.geom.Ellipse2D
//import java.awt.{Rectangle, Dimension, BasicStroke, Color}
//import java.text.DecimalFormat
//import javax.swing.JPanel
//import LadybugUtil._
//import model.LadybugModel
//import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
//import umd.cs.piccolo.nodes.{PPath, PText}
//import umd.cs.piccolo.PNode
//
//class RecordingControl(model: LadybugModel) extends PNode {
//  val text = new PText("0.00")
////  text.setFont(new PhetFont(30))
////  addChild(text)
//
//  val _width = 100
//  val _height = 60
////  setPreferredSize(new Dimension(_width, _height))
//  implicit def timeToString(time: Double) = new DecimalFormat("0.00").format(time) + " sec"
//  model.addListener((model: LadybugModel) => {
//    updateReadouts
//  })
//
//  val playbackIndicator = new PText("* Playback")
//  playbackIndicator.setFont(new PhetFont(16, true))
//  addChild(playbackIndicator)
//
//  val recordIndicator = new PText("* Record")
//  recordIndicator.setFont(new PhetFont(16, true))
//  addChild(recordIndicator)
//
//  playbackIndicator.setOffset(_width - playbackIndicator.getFullBounds.getWidth - 2, _height - playbackIndicator.getFullBounds.getHeight)
//  recordIndicator.setOffset(playbackIndicator.getFullBounds.getX, playbackIndicator.getFullBounds.getY - recordIndicator.getFullBounds.getHeight)
//
//  val timeline = new Timeline()
//  addChild(timeline)
//
//  updateReadouts
//  def updateReadouts = {
//    text.setText(model.getTime())
//    text.setOffset(2, _height - text.getFullBounds.getHeight)
//
//    playbackIndicator.setTextPaint(if (model.isPlayback) Color.red else Color.lightGray)
//    recordIndicator.setTextPaint(if (model.isRecord) Color.red else Color.lightGray)
//
//    timeline.setVisible(model.isPlayback)
//  }
//
//
//}