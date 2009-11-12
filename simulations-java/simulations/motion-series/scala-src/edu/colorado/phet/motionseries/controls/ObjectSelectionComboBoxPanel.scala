package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.motionseries.graphics.ObjectModel
import java.util.Vector
import javax.swing._
import java.awt.event.{ItemListener, ItemEvent}
import edu.colorado.phet.motionseries.model.MotionSeriesObject
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.umd.cs.piccolox.pswing.{PSwingCanvas, PComboBox, PSwing}
import java.awt.Color

class ObjectSelectionComboBoxPanel(objectModel: ObjectModel) extends SubControlPanel("controls.choose-object".translate) {
  val comboBox = new ObjectSelectionComboBox(objectModel)
  add(comboBox)

  def setEnvironment(pswing: PSwing, pswingCanvas: PSwingCanvas) = comboBox.setEnvironment(pswing, pswingCanvas)
}

class ObjectSelectionComboBox(objectModel: ObjectModel) extends PComboBox {
  val vec = new Vector[ObjectItem]

  val itemList = for (o <- MotionSeriesDefaults.objects) yield {
    o match {
      case m: MotionSeriesObject => new ObjectItem(o)
    }
  }
  class ObjectItem(val rampObject: MotionSeriesObject) {
    override def toString = rampObject.getDisplayText
  }
  for (elm <- itemList) vec.add(elm)
  super.setModel(new DefaultComboBoxModel(vec))

  objectModel.addListener(() => {setSelectedIndex(MotionSeriesDefaults.objects.indexOf(objectModel.selectedObject))})
  addItemListener(new ItemListener() {
    def itemStateChanged(e: ItemEvent) = objectModel.selectedObject = itemList(getSelectedIndex).rampObject
  })
  setRenderer(new JLabel with ListCellRenderer {
    setOpaque(true)
    setBorder(BorderFactory.createLineBorder(Color.gray))

    def getListCellRendererComponent(list: JList, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean) = {
      setIcon(new ImageIcon(BufferedImageUtils.multiScaleToHeight(value.asInstanceOf[ObjectItem].rampObject.iconImage, 30)))
      setText(value.asInstanceOf[ObjectItem].rampObject.getDisplayTextHTML)
      setBackground(if (isSelected) list.getSelectionBackground else list.getBackground)
      setForeground(if (isSelected) list.getSelectionForeground else list.getForeground)
      this
    }
  })
}