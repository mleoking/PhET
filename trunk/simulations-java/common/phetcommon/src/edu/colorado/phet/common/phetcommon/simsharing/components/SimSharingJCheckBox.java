// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Objects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

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
        SimSharingManager.sendEvent( Objects.CHECK_BOX,
                                     Actions.PRESSED,
                                     Parameter.param( Parameters.TEXT, getText() ),
                                     Parameter.param( Parameters.IS_SELECTED, isSelected() ) );
        super.fireActionPerformed( event );
    }
}
