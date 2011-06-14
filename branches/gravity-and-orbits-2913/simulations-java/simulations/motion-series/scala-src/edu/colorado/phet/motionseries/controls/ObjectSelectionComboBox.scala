package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.motionseries.graphics.ObjectSelectionModel
import java.util.Vector
import javax.swing._
import java.awt.event.{ItemListener, ItemEvent}
import edu.colorado.phet.motionseries.model.MotionSeriesObjectType
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.umd.cs.piccolox.pswing.PComboBox
import java.awt.Color

class ObjectSelectionComboBox(objectSelectionModel: ObjectSelectionModel) extends PComboBox {
  if (objectSelectionModel == null) throw new RuntimeException("Null object model")

  val itemList = for (o <- MotionSeriesDefaults.objectTypes) yield {
    o match {
      case m: MotionSeriesObjectType => new ObjectItem(o)
    }
  }
  //The ObjectItem class facilitates interoperability with swings combo box model
  class ObjectItem(val motionSeriesObjectType: MotionSeriesObjectType) {
    override def toString = motionSeriesObjectType.getDisplayText
  }
  super.setModel(new DefaultComboBoxModel(new Vector[ObjectItem] {
    for (elm <- itemList) add(elm)
  }))

  objectSelectionModel.addListener(() => setSelectedIndex(MotionSeriesDefaults.objectTypes.indexOf(objectSelectionModel.selectedObject)))
  addItemListener(new ItemListener() {
    def itemStateChanged(e: ItemEvent) = {
      if (itemList.indices.contains(getSelectedIndex))
        objectSelectionModel.selectedObject = itemList(getSelectedIndex).motionSeriesObjectType
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
        setIcon(new ImageIcon(BufferedImageUtils.multiScaleToHeight(value.asInstanceOf[ObjectItem].motionSeriesObjectType.iconImage, 30)))
        setText(value.asInstanceOf[ObjectItem].motionSeriesObjectType.getDisplayTextHTML)
        setBackground(if (isSelected) list.getSelectionBackground else list.getBackground)
        setForeground(if (isSelected) list.getSelectionForeground else list.getForeground)
      }
      this
    }
  })
}