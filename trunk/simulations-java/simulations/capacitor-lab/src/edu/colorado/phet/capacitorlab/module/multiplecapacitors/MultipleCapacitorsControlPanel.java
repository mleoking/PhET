// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import edu.colorado.phet.capacitorlab.module.CLControlPanel;

/**
 * Control panel for the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsControlPanel extends CLControlPanel {

    public MultipleCapacitorsControlPanel( MultipleCapacitorsModule module ) {
        addResetAllButton( module );
    }
}
