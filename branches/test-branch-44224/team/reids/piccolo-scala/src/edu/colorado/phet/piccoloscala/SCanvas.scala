package edu.colorado.phet.piccoloscala


import collection.mutable.ArrayBuffer
import java.beans.{PropertyChangeListener, PropertyChangeEvent}
import javax.swing.{SwingUtilities, JFrame}
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.umd.cs.piccolo.{PCanvas, PNode}
import java.awt.Container

class SCanvas extends PCanvas {
  setPanEventHandler(null)
  def addNode(node: PNode) = {
    getLayer.addChild(node)
  }

  def nodes = new ArrayBuffer[PNode]

  def add[T <: PNode](noder: => T) = {
    val createdNode = noder
    addNode(createdNode)
    createdNode
  }
}
class MyJFrame extends JFrame {
  def contentPane_=(c: Container) = super.setContentPane(c)

  def contentPane = super.getContentPane

  def _width = super.getWidth

  def _width_=(w: Int) = super.setSize(w, getHeight)

  def _height = super.getHeight

  def _height_=(h: Int) = super.setSize(getWidth, h)
}

object Predef {
  class MyPNode(node: PNode) {
    def centered_below(other: PNode) = {
      def updateLocation() = node.setOffset(other.getFullBounds.getCenterX - node.getFullBounds.getWidth / 2, other.getFullBounds.getMaxY)
      other.addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
        def propertyChange(evt: PropertyChangeEvent) =
          updateLocation()
      })
      node.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
        def propertyChange(evt: PropertyChangeEvent) = updateLocation()
      })
      updateLocation()
    }

    def addDragListener(listener: (Double, Double) => Unit) = {
      node.addInputEventListener(new PBasicInputEventHandler() {
        override def mouseDragged(event: PInputEvent) = listener(event.getDelta.getWidth, event.getDelta.getHeight)
      })
    }

    def addMousePressListener(listener: () => Unit) = {
      node.addInputEventListener(new PBasicInputEventHandler() {
        override def mousePressed(event: PInputEvent) = listener()
      })
    }
  }
  implicit def nodeToMyNode(node: PNode) = new MyPNode(node)

  def runInSwingThread(runnable: => Unit) = {
    SwingUtilities.invokeLater(new Runnable() {
      def run = runnable
    })
  }
}