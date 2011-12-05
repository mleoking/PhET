// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingObjects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingParameters;

/**
 * Swing check box that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJCheckBox extends JCheckBox {

    public SimSharingJCheckBox() {
    }

    public SimSharingJCheckBox( Icon icon ) {
        super( icon );
    }

    public SimSharingJCheckBox( Icon icon, boolean selected ) {
        super( icon, selected );
    }

    public SimSharingJCheckBox( String text ) {
        super( text );
    }

    public SimSharingJCheckBox( Action a ) {
        super( a );
    }

    public SimSharingJCheckBox( String text, boolean selected ) {
        super( text, selected );
    }

    public SimSharingJCheckBox( String text, Icon icon ) {
        super( text, icon );
    }

    public SimSharingJCheckBox( String text, Icon icon, boolean selected ) {
        super( text, icon, selected );
    }

    @Override protected void fireActionPerformed( ActionEvent event ) {
        SimSharingEvents.sendEvent( SimSharingObjects.CHECK_BOX,
                                    SimSharingActions.PRESSED,
                                    Parameter.param( SimSharingParameters.TEXT, getText() ),
                                    Parameter.param( SimSharingParameters.IS_SELECTED, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
