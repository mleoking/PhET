// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.PolygonUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Tests utilities for finding the centroid (geometric center) of a closed polygon.
 * Draws a parallelogram and its diagonals, then draws the centroid.
 * You should visually verify that the centroid is at the intersection of the diagonals.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCentroid extends JFrame {
    
    public TestCentroid() {
        super( "TestCentroid" );
        
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        setContentPane( canvas );
        
        // points that define a polygon (in this case, a parallelogram)
        Point2D p0 = new Point2D.Double( 0, 0 );
        Point2D p1 = new Point2D.Double( 20, 40 );
        Point2D p2 = new Point2D.Double( 80, 40 );
        Point2D p3 = new Point2D.Double( 60, 0 );
        Point2D[] points = new Point2D[] { p0, p1, p2, p3 };
        
        // draw the polygon
        GeneralPath polygonPath = new GeneralPath();
        polygonPath.moveTo( (float) p0.getX(), (float) p0.getY() );
        polygonPath.lineTo( (float) p1.getX(), (float) p1.getY() );
        polygonPath.lineTo( (float) p2.getX(), (float) p2.getY() );
        polygonPath.lineTo( (float) p3.getX(), (float) p3.getY() );
        polygonPath.closePath();
        PPath polygonNode = new PPath( polygonPath );
        polygonNode.setStrokePaint( Color.BLACK );
        
        // draw the diagonals of the parallelogram
        Line2D diagonalPath1 = new Line2D.Double( p0, p2 );
        PPath diagonalNode1 = new PPath( diagonalPath1 );
        diagonalNode1.setStrokePaint( Color.RED );
        Line2D diagonalPath2 = new Line2D.Double( p1, p3 );
        PPath diagonalNode2 = new PPath( diagonalPath2 );
        diagonalNode2.setStrokePaint( Color.RED );
        
        // draw a circle at the centroid (geometric center)
        double diameter = 4;
        Point2D centroid = PolygonUtils.getCentroid( points );
        double x = centroid.getX() - ( diameter / 2 );
        double y = centroid.getY() - ( diameter / 2 );
        Ellipse2D centroidPath = new Ellipse2D.Double( x, y, diameter, diameter );
        PPath centroidNode = new PPath( centroidPath );
        centroidNode.setStrokePaint( Color.GREEN );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( polygonNode );
        parentNode.addChild( diagonalNode1 );
        parentNode.addChild( diagonalNode2 );
        parentNode.addChild( centroidNode );
        
        canvas.getLayer().addChild( parentNode );
        parentNode.scale( 5 );
        parentNode.setOffset( 100, 100 );
        
        pack();
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestCentroid();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
