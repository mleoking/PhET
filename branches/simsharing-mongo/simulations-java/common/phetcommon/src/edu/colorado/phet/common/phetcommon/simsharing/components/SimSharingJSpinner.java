// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes.spinner;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.value;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.changed;

/**
 * Swing spinner that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJSpinner extends JSpinner {

    private final IUserComponent userComponent;

    public SimSharingJSpinner( IUserComponent userComponent, SpinnerModel model ) {
        super( model );
        this.userComponent = userComponent;
    }

    public SimSharingJSpinner( IUserComponent userComponent ) {
        this.userComponent = userComponent;
    }

    @Override protected void fireStateChanged() {
        SimSharingManager.sendUserMessage( userComponent, changed,
                                           componentType( spinner ).param( value, getValue().toString() ) );
        super.fireStateChanged();
    }
}
