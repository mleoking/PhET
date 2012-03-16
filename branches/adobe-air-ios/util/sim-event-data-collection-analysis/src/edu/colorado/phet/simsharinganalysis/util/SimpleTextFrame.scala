// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.util

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.Dimension
import swing._

/**
 * For showing logged text, this class takes care of scroll bars and everything.
 * @author Sam Reid
 */
class SimpleTextFrame extends Frame {
  def text = textArea.text

  def text_=(t: String) {
    textArea.text = t
  }

  val textArea = new TextArea {
    font = new PhetFont(16)
  }

  contents = new BorderPanel {
    add(new ScrollPane(textArea), BorderPanel.Position.Center)
    add(new FlowPanel {
      contents += Button("Font +") {textArea.font = new PhetFont(textArea.font.getSize + 1)}
      contents += Button("Font -") {textArea.font = new PhetFont(textArea.font.getSize - 1)}
    }, BorderPanel.Position.South)
  }
  pack()
  size = new Dimension(800, 600)
}