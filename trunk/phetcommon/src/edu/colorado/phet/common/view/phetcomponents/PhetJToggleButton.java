/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 9, 2005
 * Time: 3:56:23 PM
 * Copyright (c) Mar 9, 2005 by Sam Reid
 */

public class PhetJToggleButton extends PhetJComponent {
    /**
     * Clients should use PhetJComponent.newInstance().
     * If the client is making this, assume it's top level.
     */
    public PhetJToggleButton( Component apparatusPanel, JToggleButton jb ) {
        this( apparatusPanel, jb, true );
    }

    public PhetJToggleButton( Component apparatusPanel, JToggleButton jb, boolean topLevel ) {
        super( apparatusPanel, jb, topLevel );
        if( jb instanceof JToggleButton ) {//good candidate for factory method on PhetJComponent.
            JToggleButton jtb = (JToggleButton)jb;
            jtb.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    repaint();
                }
            } );
        }
    }
}
