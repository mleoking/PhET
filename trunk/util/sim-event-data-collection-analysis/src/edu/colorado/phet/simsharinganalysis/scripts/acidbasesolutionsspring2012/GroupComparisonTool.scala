// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import javax.swing.JFrame.EXIT_ON_CLOSE
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import edu.colorado.phet.simsharinganalysis.monitor.SimpleTable
import javax.swing.table.DefaultTableModel
import swing._

/**
 * Utility for showing different groups and plotting data
 * @author Sam Reid
 */
object GroupComparisonTool extends SimpleSwingApplication {
  lazy val top = new Frame {
    peer setDefaultCloseOperation EXIT_ON_CLOSE
    SwingUtils centerWindowOnScreen peer
    contents = new BorderPanel {
      add(new SimpleTable(new DefaultTableModel), BorderPanel.Position.Center)
      add(new FlowPanel {
        contents += Button("Add Data Collection") {}
      }, BorderPanel.Position.South)
    }
  }
}