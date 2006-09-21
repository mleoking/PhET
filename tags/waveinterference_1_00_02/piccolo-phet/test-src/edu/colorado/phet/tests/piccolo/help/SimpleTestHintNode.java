/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.help;

import edu.colorado.phet.piccolo.help.HintNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimpleTestHintNode {
    private JFrame frame;
    private HintNode hintNode;

    public SimpleTestHintNode() {
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
        new SimpleTestHintNode().start();
    }

}
