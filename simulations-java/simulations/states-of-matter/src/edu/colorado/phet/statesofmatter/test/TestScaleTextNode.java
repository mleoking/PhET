/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Demonstrates how to scale text to fit inside the bounds of some node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestScaleTextNode extends JFrame {
    
    public TestScaleTextNode() {
        super( "TestScaleTextNode" );
        
        PCanvas canvas = new PCanvas();
        getContentPane().add( canvas );
        
        // we want the text to fit inside this rectangle
        PPath rectangleNode = new PPath( new Rectangle2D.Double( 0, 0, 200, 100 ) );
        rectangleNode.setPaint( Color.RED );
        rectangleNode.setOffset( 100, 100 );
        canvas.getLayer().addChild( rectangleNode );
        
        // the infamous text
        PText textNode = new PText( "hello world" );
        canvas.getLayer().addChild( textNode );
        
        // calculate the scale value that fits the text inside the rectangle
        double margin = 10;
        PBounds rb = rectangleNode.getFullBoundsReference();
        PBounds tb = textNode.getFullBoundsReference();
        PDimension maxSize = new PDimension( rb.getWidth() - ( 2 * margin ), rb.getHeight() - ( 2 * margin ) );
        double xScale = maxSize.getWidth() / tb.getWidth();
        double yScale = maxSize.getHeight() / tb.getHeight();
        double scale = Math.min( xScale, yScale ); // maintain aspect ratio
        textNode.scale( scale );
        
        // center text in rectangle
        tb = textNode.getFullBoundsReference(); // refresh bounds after scaling
        textNode.setOffset( rb.getX() + ( rb.getWidth() - tb.getWidth() ) / 2 , rb.getY() + ( rb.getHeight() - tb.getHeight() ) / 2 );
    }

    public static void main( String args[] ) {
        TestScaleTextNode frame = new TestScaleTextNode();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 400, 400 ) );
        frame.setVisible( true );
    }
}
