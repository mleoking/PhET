// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants.ComponentTypes.icon;

/**
 * Clicking on this icon (label) sends a sim-sharing event and performs a function.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingIcon extends JLabel {

    private final SimSharingConstants.User.UserComponent object;
    private final VoidFunction0 function;

    public SimSharingIcon( SimSharingConstants.User.UserComponent object, Icon icon, final VoidFunction0 function ) {
        super( icon );
        this.object = object;
        this.function = function;
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent event ) {
                handleMousePressed();
            }
        } );
    }

    protected void handleMousePressed() {
        SimSharingManager.sendUserEvent( object, SimSharingConstants.User.UserActions.pressed, componentType( icon ) );
        function.apply();
    }
}
