package edu.colorado.phet.tests.piccolo.help;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 2:00:09 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 */

import edu.colorado.phet.piccolo.help.HintNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestHintNode {
    private JFrame frame;
    private HintNode hintNode;

    public TestHintNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PCanvas pCanvas = new PCanvas();
        pCanvas.setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        pCanvas.setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        pCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        frame.setContentPane( pCanvas );

        /**Set up the HintNode*/
        hintNode = new HintNode( "<html>This is<br>a Hint!!!</html>" );
        hintNode.setBackgroundPaint( Color.yellow );
        hintNode.setBackgroundStrokePaint( Color.black );
        hintNode.setBackgroundStroke( new BasicStroke( 3 ) );
        hintNode.setBackgroundInsets( 5, 5 );

        hintNode.setArrow( Math.PI / 2, 100.0 );
        hintNode.setFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
        hintNode.setOffset( -hintNode.getFullBounds().getWidth() / 2, 300 );
        hintNode.animateToLocation( 100, 100 );

        /**Use the HintNode*/
        pCanvas.getLayer().addChild( hintNode );
        pCanvas.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                hintNode.setVisible( false );
            }
        } );
    }

    public void start() {
        frame.setVisible( true );
    }

    public HintNode getHintNode() {
        return hintNode;
    }

    public static void main( String[] args ) {
        new TestHintNode().start();
    }

}

