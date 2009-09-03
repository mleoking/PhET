package edu.colorado.phet.motionseries.controls

import phet.common.phetcommon.view.util.BufferedImageUtils
import graphics.ObjectModel
import java.util.Vector
import javax.swing._
import java.awt.event.{ItemListener, ItemEvent}
import model.MotionSeriesObject
import motionseries.MotionSeriesResources._
import motionseries.MotionSeriesDefaults

class RampComboBox(objectModel: ObjectModel) extends SubControlPanel("controls.choose-object".translate) {
  val vec = new Vector[ObjectItem]

  val mylist = for (o <- MotionSeriesDefaults.objects) yield {
    o match {
      case m: MotionSeriesObject => new ObjectItem(o)
    }
  }
  class ObjectItem(val rampObject: MotionSeriesObject) {
    override def toString = rampObject.getDisplayText
  }
  for (elm <- mylist) vec.add(elm)

  val comboBox = new JComboBox(vec)
  comboBox.addItemListener(new ItemListener() {
    def itemStateChanged(e: ItemEvent) = objectModel.selectedObject = mylist(comboBox.getSelectedIndex).rampObject
  })
  add(comboBox)

  objectModel.addListener(() => {comboBox.setSelectedIndex(MotionSeriesDefaults.objects.indexOf(objectModel.selectedObject))})
  comboBox.setRenderer(new JLabel with ListCellRenderer {
    setOpaque(true)

    def getListCellRendererComponent(list: JList, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean) = {
      setIcon(new ImageIcon(BufferedImageUtils.multiScaleToHeight(value.asInstanceOf[ObjectItem].rampObject.bufferedImage, 30)))
      setText(value.asInstanceOf[ObjectItem].rampObject.getDisplayText)
      setBackground(if (isSelected) list.getSelectionBackground else list.getBackground)
      setForeground(if (isSelected) list.getSelectionForeground else list.getForeground)
      this
    }
  })
}