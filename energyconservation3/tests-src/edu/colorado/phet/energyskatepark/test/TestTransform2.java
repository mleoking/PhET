/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.test;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 27, 2006
 * Time: 11:29:08 AM
 * Copyright (c) May 27, 2006 by Sam Reid
 */

public class TestTransform2 {
    private JFrame frame;

    public TestTransform2() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PCanvas pCanvas = new PCanvas();
        final WorldNode world = new WorldNode( pCanvas, 10, 10 );
        pCanvas.getLayer().addChild( world );
        for( int i = 0; i <= 10; i++ ) {
            for( int j = 0; j <= 10; j++ ) {
                PPath child = new PPath( new Rectangle2D.Double( 0, 0, 0.1, 0.1 ) );
                child.setOffset( i, j );
                child.setStroke( null );
                child.setPaint( Color.blue );
                world.addChild( child );

                PText text = new PText( "" + i + ", " + j );
                ModelNode modelNode = new ModelNode( text, 0.5 );
                modelNode.setOffset( i, j );
                world.addChild( modelNode );
            }
        }
        PPath path = new PPath( new Rectangle2D.Double( 1, 2, 3, 4 ) );
        path.setStroke( new BasicStroke( 0.02f ) );
        world.addChild( path );

        PText screenLabel = new PText( "Screen Label" );
        pCanvas.getLayer().addChild( screenLabel );
        pCanvas.setPanEventHandler( null );
        pCanvas.setZoomEventHandler( null );

        pCanvas.addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                Dimension2D d2 = world.getMinDimension();
                world.setMinDimension( d2.getWidth() / 2, d2.getHeight() / 2 );
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );
        frame.setContentPane( pCanvas );
    }

    public static void main( String[] args ) {
        new TestTransform2().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
