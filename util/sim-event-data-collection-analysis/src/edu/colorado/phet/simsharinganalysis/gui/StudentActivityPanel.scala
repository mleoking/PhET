// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import javax.swing.{JTextArea, JScrollPane, JPanel}
import java.io.File
import java.awt.{Dimension, BorderLayout}
import edu.colorado.phet.simsharinganalysis.scripts.AcidBaseSolutionSpring2012AnalysisReport


/**
 * Combines play area and text output pane.
 * @author Sam Reid
 */
class StudentActivityPanel(dir: File) extends JPanel(new BorderLayout) {
  add(new StudentActivityCanvas(dir.getAbsolutePath), BorderLayout.CENTER)

  class MyStringBuffer {
    var buffer = ""

    def println(s: String) {
      buffer = buffer + s + "\n"
    }
  }

  val myBuffer = new MyStringBuffer
  val abs = AcidBaseSolutionSpring2012AnalysisReport.report(dir, myBuffer.println)

  println("my buffer = " + myBuffer.buffer)
  add(new JScrollPane(new JTextArea(myBuffer.buffer) {
    setEditable(false)
  }) {
    setPreferredSize(new Dimension(800, 300))
  }, BorderLayout.SOUTH)
}