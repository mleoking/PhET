// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import edu.colorado.phet.simsharinganalysis.util.MyStringBuffer
import edu.colorado.phet.simsharinganalysis.Parser
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import swing._
import javax.swing.{JFrame, Timer}

//Utility to show logs from a file as it is being generated.
//This is to help in testing that parsing is working properly.
object RealTimeAnalysis extends SimpleSwingApplication {

  val textArea = new TextArea {
    font = new PhetFont(16)
  }

  new Timer(1000, new ActionListener {
    def actionPerformed(e: ActionEvent) {
      val logDir = new File(System.getProperty("user.home"), "phet-logs")
      val mostRecentFile = logDir.listFiles().toList.sortBy(_.lastModified).last
      println("most recent file: " + mostRecentFile)

      val myBuffer = new MyStringBuffer
      try {
        AcidBaseSolutionSpring2012AnalysisReport.writeSingleLogReport(new Parser().parse(mostRecentFile), myBuffer.println)
      }
      catch {
        case e: Exception => e.printStackTrace()
      }

      //Only set the new text on the text area if different, because it scrolls the scroll pane back to default
      if ( textArea.text != myBuffer.toString ) {
        textArea.text = myBuffer.toString
      }
    }
  }).start()

  lazy val top = new Frame {
    contents = new ScrollPane(textArea)
    pack()
    size = new Dimension(800, 600)
    visible = true
    peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }
}