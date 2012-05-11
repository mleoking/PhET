// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.gui

import javax.swing.{JTextArea, JScrollPane, JPanel}
import java.io.File
import java.awt.{Dimension, BorderLayout}
import edu.colorado.phet.simsharinganalysis.util.MyStringBuffer
import edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012.AcidBaseSolutionSpring2012AnalysisReport
import edu.colorado.phet.simsharinganalysis.phet


/**
 * Combines play area and text output pane.
 * @author Sam Reid
 */
class StudentActivityPanel(dir: File) extends JPanel(new BorderLayout) {
  add(new StudentActivityCanvas(phet load dir.getAbsolutePath), BorderLayout.CENTER)

  val myBuffer = new MyStringBuffer
  val abs = AcidBaseSolutionSpring2012AnalysisReport.report(dir, myBuffer.println)

  println("my buffer = " + myBuffer)
  add(new JScrollPane(new JTextArea(myBuffer.toString) {
    setEditable(false)
  }) {
    setPreferredSize(new Dimension(800, 300))
  }, BorderLayout.SOUTH)
}