// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * TestShrinkActivity tests animation of a node's transform.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestAnimateTransform extends JFrame {
    
    private PNode imageNode;
    
    public TestAnimateTransform() {
        super( "TestShrinkActivity" );
        
        PCanvas canvas = new PCanvas();
        getContentPane().add( canvas );
        
        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );

        imageNode = new PImage( GlaciersImages.BOREHOLE_DRILL );
        rootNode.addChild( imageNode );
        imageNode.setOffset( 200, 200 );
    }
    
    public void animate() {
        final double scale = 0.01;
        final long duration = 1000; // ms
        imageNode.animateToPositionScaleRotation( imageNode.getXOffset(), imageNode.getYOffset(), scale, imageNode.getRotation(), duration );
    }

    public static void main( String args[] ) {
        
        TestAnimateTransform frame = new TestAnimateTransform();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 640, 480 ) );
        frame.setVisible( true );
        
        frame.animate();
    }
}
