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
    preferredSize = new Dimension(800, 600)
    font = new PhetFont(16)
  }

  new Timer(1000, new ActionListener {
    def actionPerformed(e: ActionEvent) {
      val logDir = new File(System.getProperty("user.home"), "phet-logs")
      val mostRecentFile = logDir.listFiles().toList.sortBy(_.lastModified).last
      println("most recent file: " + mostRecentFile)

      val myBuffer = new MyStringBuffer
      AcidBaseSolutionSpring2012AnalysisReport.writeSingleLogReport(new Parser().parse(mostRecentFile), myBuffer.println)
      textArea.text = myBuffer.toString
    }
  }).start()

  lazy val top = new Frame {
    contents = new ScrollPane(textArea)
    pack()
    visible = true
    peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  }
}