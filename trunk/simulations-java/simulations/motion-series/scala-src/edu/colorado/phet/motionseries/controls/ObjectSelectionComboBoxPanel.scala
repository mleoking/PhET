package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.motionseries.graphics.ObjectModel
import java.util.Vector
import javax.swing._
import java.awt.event.{ItemListener, ItemEvent}
import edu.colorado.phet.motionseries.model.MotionSeriesObjectType
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.umd.cs.piccolox.pswing.{PSwingCanvas, PComboBox, PSwing}
import java.awt.Color

class ObjectSelectionComboBox(objectModel: ObjectModel) extends PComboBox {
  if (objectModel == null) throw new RuntimeException("Null object model")
  val vec = new Vector[ObjectItem]

  val itemList = for (o <- MotionSeriesDefaults.objectTypes) yield {
    o match {
      case m: MotionSeriesObjectType => new ObjectItem(o)
    }
  }
  class ObjectItem(val rampObject: MotionSeriesObjectType) {
    override def toString = rampObject.getDisplayText
  }
  for (elm <- itemList) vec.add(elm)
  super.setModel(new DefaultComboBoxModel(vec))

  objectModel.addListener(() => setSelectedIndex(MotionSeriesDefaults.objectTypes.indexOf(objectModel.selectedObject)))
  addItemListener(new ItemListener() {
    def itemStateChanged(e: ItemEvent) = {
      if (itemList.indices.contains(getSelectedIndex))
        objectModel.selectedObject = itemList(getSelectedIndex).rampObject
      else {
        println("could not select item : " + getSelectedIndex)
      }
    }
  })
  setRenderer(new JLabel with ListCellRenderer {
    setOpaque(true)
    setBorder(BorderFactory.createLineBorder(Color.gray))

    def getListCellRendererComponent(list: JList, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean) = {
      if (value != null) {
        setIcon(new ImageIcon(BufferedImageUtils.multiScaleToHeight(value.asInstanceOf[ObjectItem].rampObject.iconImage, 30)))
        setText(value.asInstanceOf[ObjectItem].rampObject.getDisplayTextHTML)
        setBackground(if (isSelected) list.getSelectionBackground else list.getBackground)
        setForeground(if (isSelected) list.getSelectionForeground else list.getForeground)
      }
      this
    }
  })
}