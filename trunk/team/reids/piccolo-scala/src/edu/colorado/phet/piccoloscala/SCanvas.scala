package edu.colorado.phet.piccoloscala


import collection.mutable.ArrayBuffer
import java.beans.{PropertyChangeListener, PropertyChangeEvent}
import javax.swing.{SwingUtilities, JFrame}
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEventListener, PInputEvent}
import umd.cs.piccolo.{PCanvas, PNode}
import java.awt.Container
class SCanvas extends PCanvas {
  setPanEventHandler(null)
  def addNode(node: PNode) = {
    getLayer.addChild(node)
  }

  def nodes = new ArrayBuffer[PNode]

  def add(noder: => PNode) = {
    val createdNode = noder
    addNode(createdNode)
    createdNode
  }
}
class MyJFrame extends JFrame {
  def contentPane_=(c: Container) = super.setContentPane(c)

  def contentPane = super.getContentPane
}

object Predef {
  class MyPNode(node: PNode) {
    def centered_below(other: PNode) = {
      def updateLocation() = node.setOffset(other.getFullBounds.getCenterX - node.getFullBounds.getWidth / 2, other.getFullBounds.getMaxY)
      other.addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
        def propertyChange(evt: PropertyChangeEvent) =
          updateLocation()
      })
      node.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS,new PropertyChangeListener(){
        def propertyChange(evt: PropertyChangeEvent) = updateLocation()
      })
      updateLocation()
}
    def addDragListener( listener: PInputEvent=>Unit) = {
      node.addInputEventListener(new PBasicInputEventHandler(){
        override def mouseDragged(event: PInputEvent) = listener(event)
      })
    }
  }
  implicit def nodeToMyNode(node: PNode) = new MyPNode(node)

  def runInSwingThread(runnable: =>Unit) = {
    SwingUtilities.invokeLater( new Runnable(){
      def run = runnable
    })
  }
}