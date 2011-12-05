// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingObjects;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingParameters;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Clicking on this icon (label) sends a sim-sharing event and performs a function.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingIcon extends JLabel {

    private final String description; // description parameter value, for sim-sharing event
    private final VoidFunction0 function;

    public SimSharingIcon( Icon icon, final String description, final VoidFunction0 function ) {
        super( icon );
        this.description = description;
        this.function = function;
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent event ) {
                handleMousePressed();
            }
        } );
    }

    protected void handleMousePressed() {
        SimSharingEvents.sendEvent( SimSharingObjects.ICON, SimSharingActions.PRESSED,
                                    Parameter.param( SimSharingParameters.DESCRIPTION, description ) );
        function.apply();
    }
}
