// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Radio button with correct threading between the LWJGL and Swing threads, so that we display a component in the Swing thread that deals with
 * a property that is only modified in the LWJGL thread.
 * <p/>
 * NOTE: not completely thread-safe. if concurrent modifications to the property happen approximately when the user clicks on this, there may be issues
 *
 * @param <T> Type that the radio buttons are selecting over
 * @author Jonathan Olson
 */
public class GLSimSharingPropertyRadioButton<T> extends SimSharingJRadioButton {

    public GLSimSharingPropertyRadioButton( IUserComponent userComponent, String text, final Property<T> lwjglProperty, final T value ) {
        super( userComponent, text );

        // during construction, assume both threads are synchronized
        setSelected( lwjglProperty.get() == value );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // set that we are selected in the Swing EDT
                setSelected( true );

                // and modify the underlying property in the LWJGL thread
                LWJGLUtils.invoke( new Runnable() {
                    public void run() {
                        lwjglProperty.set( value );
                    }
                } );
            }
        } );
        lwjglProperty.addObserver( new SimpleObserver() {
            public void update() {
                // access the property in the LWJGL thread
                final boolean set = lwjglProperty.get() == value;

                // and modify whether we are selected in the Swing EDT
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setSelected( set );
                    }
                } );
            }
        } );
    }
}
