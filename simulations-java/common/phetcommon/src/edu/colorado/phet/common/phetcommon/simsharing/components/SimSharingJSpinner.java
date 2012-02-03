// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.changed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.spinner;

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
        enableMouseEvents();
    }

    public SimSharingJSpinner( IUserComponent userComponent ) {
        this.userComponent = userComponent;
        enableMouseEvents();
    }

    //Make sure processMouseEvent gets called even if no listeners registered.  See http://www.dickbaldwin.com/java/Java102.htm#essential_ingredients_for_extending_exis
    private void enableMouseEvents() {
        enableEvents( AWTEvent.MOUSE_EVENT_MASK );
    }

    //When mouse is pressed, send a simsharing event if the component is disabled.  Safer to override than add listener, since the listener could be removed with removeAllListeners().
    //Only works if enableEvents has been called.  See #3218
    //TODO: this might not work right since spinner is composite
    @Override protected void processMouseEvent( MouseEvent e ) {
        if ( e.getID() == MouseEvent.MOUSE_PRESSED && !isEnabled() ) {
            sendUserMessage( parameterSet( value, getValue().toString() ).add( enabled, isEnabled() ).add( interactive, isEnabled() ) );
        }
        super.processMouseEvent( e );
    }

    @Override protected void fireStateChanged() {
        sendUserMessage( parameterSet( value, getValue().toString() ) );
        super.fireStateChanged();
    }

    private void sendUserMessage( ParameterSet parameterSet ) {
        SimSharingManager.sendUserMessage( userComponent, spinner, changed, parameterSet );
    }
}
