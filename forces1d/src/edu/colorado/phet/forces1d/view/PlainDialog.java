/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: Sam Reid
 * Date: Jan 25, 2005
 * Time: 12:47:17 PM
 * Copyright (c) Jan 25, 2005 by Sam Reid
 */

public class PlainDialog extends JDialog {

    public PlainDialog( String name, Frame parent, JPanel contentPanel ) {
        super( parent, name, false );

        // Make the window plain, with no way to close it
        this.setUndecorated( true );
        this.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );

        addWindowListener( new WindowAdapter() {
            public void windowClosed( WindowEvent e ) {
            }
        } );

        this.setResizable( false );
        this.setContentPane( contentPanel );
        this.pack();
    }

}
