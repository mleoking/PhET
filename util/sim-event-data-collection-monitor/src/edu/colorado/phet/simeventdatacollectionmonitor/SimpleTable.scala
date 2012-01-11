// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor

// Copyright 2002-2011, University of Colorado

import javax.swing._
import swing._

/**
 * @author Sam Reid
 */
class SimpleTable extends Component {
  override lazy val peer: JTable = new JTable(JTableTest.tableModel) {
    setAutoCreateRowSorter(true)

    def tableWrapper = SimpleTable.this
  }
}



