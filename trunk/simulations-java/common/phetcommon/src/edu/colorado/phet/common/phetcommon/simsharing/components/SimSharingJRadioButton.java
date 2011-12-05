// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 * Radio button used in phetcommon for transmitting sim-sharing data.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJRadioButton extends JRadioButton {

    public SimSharingJRadioButton() {
    }

    public SimSharingJRadioButton( Icon icon ) {
        super( icon );
    }

    public SimSharingJRadioButton( Action a ) {
        super( a );
    }

    public SimSharingJRadioButton( Icon icon, boolean selected ) {
        super( icon, selected );
    }

    public SimSharingJRadioButton( String text ) {
        super( text );
    }

    public SimSharingJRadioButton( String text, boolean selected ) {
        super( text, selected );
    }

    public SimSharingJRadioButton( String text, Icon icon ) {
        super( text, icon );
    }

    public SimSharingJRadioButton( String text, Icon icon, boolean selected ) {
        super( text, selected );
    }
}
