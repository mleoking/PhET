// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;

/**
 * Swing spinner that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJSpinner extends JSpinner {

    private final String object;

    public SimSharingJSpinner( String object, SpinnerModel model ) {
        super( model );
        this.object = object;
    }

    public SimSharingJSpinner( String object ) {
        this.object = object;
    }

    @Override protected void fireStateChanged() {
        SimSharingManager.sendEvent( object, Actions.CHANGED,
                                     new Parameter( Parameters.COMPONENT_TYPE, ParameterValues.SPINNER ),
                                     new Parameter( Parameters.VALUE, getValue().toString() ) );
        super.fireStateChanged();
    }
}
