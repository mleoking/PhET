/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * ABSResetAllButton is the "Reset All" button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSResetAllButton extends ResetAllButton {
    public ABSResetAllButton( Resettable resettable, Component parent ) {
        super( resettable, parent );
        setFont( new PhetFont( Font.PLAIN, 18 ) );
        setMargin( new Insets( 5, 15, 5, 15 ) ); // top, left, bottom, right
    }
}
