package edu.colorado.phet.therampscala.controls

import common.phetcommon.view.util.BufferedImageUtils
import graphics.ObjectModel
import java.util.Vector
import javax.swing._
import java.awt.event.{ItemListener, ItemEvent}
import RampResources._

class RampComboBox(objectModel: ObjectModel) extends SubControlPanel("controls.choose-object".translate) {
  val vec = new Vector[ObjectItem]

  val mylist = for (o <- RampDefaults.objects) yield {
    o match {
    //      case jc: MutableRampObject => new CustomObjectItem(jc)
      case m: ScalaRampObject => new ObjectItem(o)
    }
  }
  class ObjectItem(val rampObject: ScalaRampObject) {
    override def toString = rampObject.getDisplayText
  }
  for (elm <- mylist) vec.add(elm)

  val comboBox = new JComboBox(vec)
  comboBox.addItemListener(new ItemListener() {
    def itemStateChanged(e: ItemEvent) = objectModel.selectedObject = mylist(comboBox.getSelectedIndex).rampObject
  })
  add(comboBox)

  objectModel.addListener(() => {comboBox.setSelectedIndex(RampDefaults.objects.indexOf(objectModel.selectedObject))})
  comboBox.setRenderer(new JLabel with ListCellRenderer {
    setOpaque(true)

    def getListCellRendererComponent(list: JList, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean) = {
      setIcon(new ImageIcon(BufferedImageUtils.multiScaleToHeight(value.asInstanceOf[ObjectItem].rampObject.bufferedImage, 30)))
      setText(value.asInstanceOf[ObjectItem].rampObject.getDisplayText)
      setBackground(if (isSelected) list.getSelectionBackground else list.getBackground)
      setForeground(if (isSelected) list.getSelectionForeground else list.getForeground)
      this
    }
  });
}