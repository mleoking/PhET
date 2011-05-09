// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * TestZoomIndicator tests drawing a "zoom indicator".
 * A small box and a large box are connected with dashed lines to indicate
 * that the large box is a zoomed-in view of the small box.
 * <p>
 * I wrote this test because I was having some problems getting the 
 * mapping between coordinate systems correct.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestZoomIndicator extends JFrame {

    private static final Stroke DASHED_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5,5}, 0 );
    
    public TestZoomIndicator() {
        super( "TestZoomIndicator" );

        setSize( 800, 600 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        Dimension canvasSize = new Dimension( 750, 750 );
        PhetPCanvas canvas = new PhetPCanvas( canvasSize );
        setContentPane( canvas );
        
        PNode rootNode = new PNode();
        canvas.addWorldChild( rootNode );
        
        // Green square with a small black square in it's interior
        PPath smallSquare = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        smallSquare.setPaint( Color.BLACK );
        smallSquare.setStroke( new BasicStroke( 2f ) );
        smallSquare.setStrokePaint( Color.RED );
        PPath mediumSquare = new PPath( new Rectangle2D.Double( 0, 0, 200, 200 ) );
        mediumSquare.setPaint( Color.GREEN );
        PNode parentNode = new PhetPNode();
        parentNode.addChild( mediumSquare );
        parentNode.addChild( smallSquare );
        smallSquare.setOffset( 25, 25 );
        rootNode.addChild( parentNode );
        parentNode.setOffset( 50, 150 );

        // Big black square, our zoomed-in view of the small black square
        PPath bigSquare = new PPath( new Rectangle2D.Double( 0, 0, 400, 400 ) );
        bigSquare.setPaint( Color.BLACK );
        rootNode.addChild( bigSquare );
        bigSquare.setOffset( 300, 50 );
        
        // Line connecting the top-left corners of the big and small squares
        PPath topLine = new PPath();
        topLine.setStrokePaint( Color.RED );
        topLine.setStroke( DASHED_STROKE );
        
        // Line connecting the bottom-left corners of the big and small squares
        PPath bottomLine = new PPath();
        bottomLine.setStrokePaint( Color.RED );
        bottomLine.setStroke( DASHED_STROKE );

        // Parent of the lines
        PNode linesParent = new PhetPNode();
        linesParent.addChild( topLine );
        topLine.setOffset( 0, 0 );
        linesParent.addChild( bottomLine );
        bottomLine.setOffset( 0, 0 );
        rootNode.addChild( linesParent );
        linesParent.setOffset( 55, 77 ); // should be able to use any values here
        
        // Bounds of the small and big squares in the line's coordinate system
        Rectangle2D smallBoxBounds = topLine.globalToLocal( smallSquare.getGlobalFullBounds() );
        Rectangle2D bigBoxBounds = topLine.globalToLocal( bigSquare.getGlobalFullBounds() );
        
        // Create the top line
        double x1 = smallBoxBounds.getX();
        double y1 = smallBoxBounds.getY();
        double x2 = bigBoxBounds.getX();
        double y2 = bigBoxBounds.getY();
        topLine.setPathTo( new Line2D.Double( x1, y1, x2, y2 ) );
        
        // Create the bottom line
        double x3 = x1;
        double y3 = smallBoxBounds.getMaxY();
        double x4 = x2;
        double y4 = bigBoxBounds.getMaxY();
        bottomLine.setPathTo( new Line2D.Double( x3, y3, x4, y4 ) );
    }
    
    public static void main( String[] args ) {
        TestZoomIndicator test = new TestZoomIndicator();
        test.show();
    }
}
