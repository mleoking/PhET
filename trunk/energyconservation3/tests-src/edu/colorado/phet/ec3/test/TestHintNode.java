/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.test;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 2:00:09 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 */

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.activities.PPositionPathActivity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

public class TestHintNode {
    private JFrame frame;

    public TestHintNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PCanvas pCanvas = new PCanvas();
        pCanvas.setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        frame.setContentPane( pCanvas );

        /**Set up the HintNode*/
        final HintNode hintNode = new HintNode( "<html>This is<br>a Hint!!!</html>" );
        hintNode.setBackgroundPaint( Color.yellow );
        hintNode.setBackgroundStrokePaint( Color.black );
        hintNode.setBackgroundStroke( new BasicStroke( 3 ) );

        hintNode.setArrow( Math.PI / 2, 100.0 );
        hintNode.setFont( new Font( "Lucida Sans", Font.BOLD, 24 ) );
        hintNode.setOffset( -hintNode.getFullBounds().getWidth() / 2, 300 );
        hintNode.animateToLocation( 100, 100 );

//        testSuperActivity( hintNode );

        /**Use the HintNode*/
        pCanvas.getLayer().addChild( hintNode );
        pCanvas.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                hintNode.setVisible( false );
            }
        } );
    }

    private void testSuperActivity( final HintNode hintNode ) {
        // create animation path
        GeneralPath path = new GeneralPath();
        path.moveTo( 100, 100 );
        path.lineTo( 300, 300 );
        path.lineTo( 100, 300 );
        path.closePath();

        // create activity to run animation.
        PPositionPathActivity positionPathActivity = new PPositionPathActivity( 3000, 0, new PPositionPathActivity.Target() {
            public void setPosition( double x, double y ) {
                hintNode.setOffset( x, y );
            }
        } );
        positionPathActivity.setPositions( path );
        positionPathActivity.setLoopCount( Integer.MAX_VALUE );

        hintNode.setActivity( positionPathActivity );
    }

    public static void main( String[] args ) {
        new TestHintNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}

