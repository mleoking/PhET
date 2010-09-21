/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.introduction;

import edu.colorado.phet.capacitorlab.module.CLControlPanel;

/**
 * Control panel for the "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionControlPanel extends CLControlPanel {

    public IntroductionControlPanel( IntroductionModule module ) {
        addResetAllButton( module );
    }
}
