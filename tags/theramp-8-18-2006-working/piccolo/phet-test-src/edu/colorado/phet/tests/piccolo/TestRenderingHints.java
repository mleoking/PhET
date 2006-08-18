/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 10:43:45 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class TestRenderingHints {
    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        final PNode pText = new PPath( new Ellipse2D.Double( 0, 0, 30, 30 ) );
        PNode parent1 = new PNode();
        parent1.addChild( pText );
        parent1.setAntialias( true );
        pCanvas.getLayer().addChild( parent1 );

        PNode pText2 = new PPath( new Ellipse2D.Double( 0, 0, 30, 30 ) );

        PNode parent2 = new PNode();
        parent2.addChild( pText2 );
        parent2.addChild( new PPath( new Ellipse2D.Double( 32, 0, 30, 30 ) ) );
        parent2.setOffset( 0, parent1.getFullBounds().getHeight() + 2 );

        parent2.setAntialias( false );
        pText2.setAntialias( true );

        pCanvas.getLayer().addChild( parent2 );

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 400, 600 );
        frame.setVisible( true );
    }
}
