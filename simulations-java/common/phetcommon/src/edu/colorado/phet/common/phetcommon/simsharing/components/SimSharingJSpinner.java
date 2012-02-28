// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.changed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.spinner;

/**
 * Swing spinner that sends sim-sharing events.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingJSpinner extends JSpinner {

    private final IUserComponent userComponent;
    private boolean userInteraction;

    public SimSharingJSpinner( IUserComponent userComponent, SpinnerModel model ) {
        super( model );
        this.userComponent = userComponent;
        init();
    }

    public SimSharingJSpinner( IUserComponent userComponent ) {
        this.userComponent = userComponent;
        init();
    }

    private void init() {
        initListeners();
        enableMouseEvents();
    }

    /*
     * Pressing one of the spinner buttons results in a call to fireStateChanged.
     * In order to differentiate between user interactions and programmatic calls
     * to setValue, we need to know if a button has been pressed. This initialization
     * method adds a listener that sets buttonPressed=true when one of the buttons
     * is pressed. A message will be sent only if buttonPressed=true in fireStateChanged.
     */
    private void initListeners() {
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                userInteraction = true;
            }
        };
        for ( Component child : getComponents() ) {
            if ( child instanceof JButton) {
                ( (JButton) child ).addActionListener( listener );
            }
        }
        //TODO how to handle (1) button press-and-hold, and (2) text field commit?
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
        if ( userInteraction ) {
            sendUserMessage( parameterSet( value, getValue().toString() ) );
        }
        userInteraction = false;
        super.fireStateChanged();
    }

    private void sendUserMessage( ParameterSet parameterSet ) {
        SimSharingManager.sendUserMessage( userComponent, spinner, changed, parameterSet );
    }

    public static void main( String[] args ) {

        // integer spinner
        final SimSharingJSpinner spinner = new SimSharingJSpinner( new UserComponent( "testSpinner" ) ) {{
            setModel( new SpinnerNumberModel( 0, 0, 100, 1 ) );
            setEditor( new NumberEditor( this ) );
        }};

        // Test programmatically setting spinner. We don't want this to send a message.
        final JButton button = new JButton( "set 2" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    spinner.setValue( new Integer(2) );
                }
            } );
        }};

        // frame
        JFrame frame = new JFrame() {{
            setContentPane( new JPanel() {{
                add( spinner );
                add( button );
            }} );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
