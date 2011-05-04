// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.nuclearphysics.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.ButtonNode2;

/**
 * This class extends ButtonNode in order to add the capability of
 * doing an "auto-press", which means that it is told to look like it just
 * got pressed for a bit and then released.
 *
 * @author John Blanco
 */
public class AutoPressButtonNode extends ButtonNode2 {

    private static final int HIGHLIGHT_TIME = 250; // In milliseconds.
    private static final int PRESS_TIME = 500; // In milliseconds.

    // Timers
    private final Timer PRE_PRESS_HIGHLIGHT_TIMER = new Timer( HIGHLIGHT_TIME, null );
    private final Timer PRESS_TIMER = new Timer( PRESS_TIME, null );
    private final Timer POST_PRESS_HIGHLIGHT_TIMER = new Timer( HIGHLIGHT_TIME, null );

    private boolean animationInProgress = false;

    /**
     * Constructor.
     */
    public AutoPressButtonNode( String label, int fontSize, Color buttonColor ) {
        super( label, fontSize, buttonColor );

        PRE_PRESS_HIGHLIGHT_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // Make the button look like it is pressed.
                setArmed( true );
                PRE_PRESS_HIGHLIGHT_TIMER.stop();
                PRESS_TIMER.start();
            }
        } );

        PRESS_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // Make the button look like it is highlighted but not pressed.
                setArmed( false );
                PRESS_TIMER.stop();
                POST_PRESS_HIGHLIGHT_TIMER.start();
            }
        } );

        POST_PRESS_HIGHLIGHT_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // Make the button look like it is neither pressed nor highlighted.
                setFocus( false );
                POST_PRESS_HIGHLIGHT_TIMER.stop();
                animationInProgress = false;
            }
        } );
    }

    public void autoPress() {
        if ( !animationInProgress ) {
            // Make the button look like it is highlighted.
            setFocus( true );

            // Start the timer for the next step.
            animationInProgress = true;
            PRE_PRESS_HIGHLIGHT_TIMER.start();

        }
    }
}
