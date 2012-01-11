// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor

// Copyright 2002-2011, University of Colorado

import javax.swing._
import swing._
import table.DefaultTableModel

/**
 * @author Sam Reid
 */
class SimpleTable(model: DefaultTableModel) extends Component {
  override lazy val peer: JTable = new JTable(model) {
    def tableWrapper = SimpleTable.this
  }
}