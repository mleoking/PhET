// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

/**
 * Swing button that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJButton extends JButton {

    private final String object;

    public SimSharingJButton( String object ) {
        this.object = object;
    }

    public SimSharingJButton( String object, Icon icon ) {
        super( icon );
        this.object = object;
    }

    public SimSharingJButton( String object, String text ) {
        super( text );
        this.object = object;
    }

    public SimSharingJButton( String object, Action a ) {
        super( a );
        this.object = object;
    }

    public SimSharingJButton( String object, String text, Icon icon ) {
        super( text, icon );
        this.object = object;
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingManager.sendUserEvent( object,
                                         Actions.PRESSED,
                                         Parameter.param( Parameters.COMPONENT_TYPE, ParameterValues.BUTTON ) );
        super.fireActionPerformed( event );
    }
}
