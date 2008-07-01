/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Component;
import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.phscale.PHScaleConstants;

/**
 * PHScaleResetAllButton is the "Reset All" button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleResetAllButton extends ResetAllButton {
    public PHScaleResetAllButton( Resettable resettable, Component parent ) {
        super( resettable, parent );
        setFont( PHScaleConstants.CONTROL_FONT );
        setMargin( new Insets( 10, 20, 10, 20 ) ); // top, left, bottom, right
    }
}
