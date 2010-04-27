/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.module.CLControlPanel;

/**
 * Control panel for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricControlPanel extends CLControlPanel {

    public DielectricControlPanel( DielectricModule module ) {
        addResetAllButton( module );
    }
}
