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

public class JAnimatedButton3D extends ApparatusPanel {
    private AnimatedButton3D button3D;

    public JAnimatedButton3D( String text ) {
        button3D = new AnimatedButton3D( this, text, 12, -0.35 );
        Rectangle bounds = button3D.getBounds();
        setPreferredSize( new Dimension( bounds.width + 4, bounds.height + 4 ) );
        addGraphic( button3D );
        addGraphicsSetup( new BasicGraphicsSetup() );
        button3D.setLocation( 2, 2 );
        setBorder( null );
        super.setDisplayBorder( false );
//        addMouseListener( new MouseAdapter() {
//            public void mouseExited( MouseEvent e ) {
//                button3D.fireMouseExited( e );
//            }
//        } );
    }

    public AnimatedButton3D getButton3D() {
        return button3D;
    }

    public void addActionListener( ActionListener actionListener ) {
        button3D.addActionListener( actionListener );
    }
}
