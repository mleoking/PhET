/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 12:56:03 AM
 * Copyright (c) Feb 4, 2005 by Sam Reid
 */

public class JButton3D extends ApparatusPanel {
    private Button3D button3D;

    public JButton3D( String text ) {
        button3D = new Button3D( this, text );
        Rectangle bounds = button3D.getBounds();
        setPreferredSize( new Dimension( bounds.width + 2, bounds.height + 2 ) );
        addGraphic( button3D );
        addGraphicsSetup( new BasicGraphicsSetup() );
        button3D.setLocation( 1, 1 );
        setBorder( null );
        super.setDisplayBorder( false );
//        addMouseListener( new MouseAdapter() {
//            public void mouseExited( MouseEvent e ) {
//                button3D.fireMouseExited( e );
//            }
//        } );
    }

    public Button3D getButton3D() {
        return button3D;
    }

    public void addActionListener( ActionListener actionListener ) {
        button3D.addActionListener( actionListener );
    }
}
