package edu.colorado.phet.simsharinganalysis

// Copyright 2002-2011, University of Colorado

import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import swing._
import edu.colorado.phet.simsharinganalysis.util.SimpleTextFrame
import javax.swing.{JFrame, Timer}

/**
 * Utility to show logs from a file as it is being generated.
 * This is to help in testing that parsing is working properly.
 * @author Sam Reid
 */
class RealTimeAnalysis(reporter: Log => String) extends SimpleSwingApplication {

  new Timer(1000, new ActionListener {
    def actionPerformed(e: ActionEvent) {
      val logDir = new File(System.getProperty("user.home"), "phet-logs")
      val mostRecentFile = logDir.listFiles().toList.sortBy(_.lastModified).last
      println("most recent file: " + mostRecentFile)

      try {
        val log = new Parser().parse(mostRecentFile)
        val text = reporter(log)
        //Only set the new text on the text area if different, because it scrolls the scroll pane back to default
        if ( top.text != text ) {
          top.text = text
        }
      }
      catch {
        case e: Exception => e.printStackTrace()
      }
    }
  }).start()

  lazy val top = new SimpleTextFrame {
    visible = true
    peer setDefaultCloseOperation JFrame.EXIT_ON_CLOSE
  }
}