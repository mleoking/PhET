/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 1:31:56 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class JButton3D extends ApparatusPanel {
    private SquareButton3D squareButton3D;

    public JButton3D( String str ) {
        Font font = getFont();

        squareButton3D = new SquareButton3D( this, font, str );
        Dimension preferredSize = new Dimension( squareButton3D.getWidth(), squareButton3D.getHeight() );
        preferredSize.width += 4;
        preferredSize.height += 4;
        setPreferredSize( preferredSize );
        setDisplayBorder( false );

        squareButton3D.setLocation( 3, 3 );
        addGraphic( squareButton3D );
    }

    public void addActionListener( ActionListener actionListener ) {
        squareButton3D.addActionListener( actionListener );
    }

    public PhetGraphic getButton3D() {
        return squareButton3D;
    }
}
