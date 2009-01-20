/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Component;
import java.awt.Insets;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;

/**
 * ABSResetAllButton is the "Reset All" button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSResetAllButton extends ResetAllButton {
    public ABSResetAllButton( Resettable resettable, Component parent ) {
        super( resettable, parent );
        setFont( ABSConstants.CONTROL_FONT );
        setMargin( new Insets( 10, 20, 10, 20 ) ); // top, left, bottom, right
    }
}
