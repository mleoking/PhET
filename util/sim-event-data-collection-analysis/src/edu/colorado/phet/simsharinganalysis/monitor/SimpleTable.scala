package edu.colorado.phet.simsharinganalysis.monitor

// Copyright 2002-2011, University of Colorado
// Copyright 2002-2011, University of Colorado

import javax.swing._
import swing._
import table.DefaultTableModel

/**
 * Simple wrapper for JTable so it can be embedded in Scala swing component tree
 * @author Sam Reid
 */
class SimpleTable(model: DefaultTableModel) extends Component {
  override lazy val peer: JTable = new JTable(model) {
    def tableWrapper = SimpleTable.this

  }
}