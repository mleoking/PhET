// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.tests;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.PolygonUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Tests utilities for finding the centroid (geometric center) of a closed polygon.
 * Draws a parallelogram and its diagonals, then draws the centroid.
 * You should visually verify that the centroid is at the intersection of the diagonals.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCentroid extends JFrame {

    @Override
    public void paint( Graphics g ) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke( new BasicStroke( 2f ) );

        // points that define a polygon (in this case, a parallelogram), clockwise from upper left
        final double xOffset = 50;
        final double yOffset = 50;
        Point2D p0 = new Point2D.Double( xOffset, yOffset );
        Point2D p1 = new Point2D.Double( xOffset + 240, yOffset );
        Point2D p2 = new Point2D.Double( xOffset + 320, yOffset + 160 );
        Point2D p3 = new Point2D.Double( xOffset + 80, yOffset + 160 );
        Point2D[] points = new Point2D[] { p0, p1, p2, p3 };

        // draw the polygon
        GeneralPath polygonPath = new GeneralPath();
        polygonPath.moveTo( (float) p0.getX(), (float) p0.getY() );
        polygonPath.lineTo( (float) p1.getX(), (float) p1.getY() );
        polygonPath.lineTo( (float) p2.getX(), (float) p2.getY() );
        polygonPath.lineTo( (float) p3.getX(), (float) p3.getY() );
        polygonPath.closePath();
        g2.setPaint( Color.BLACK );
        g2.draw( polygonPath );

        // draw the diagonals of the parallelogram
        Line2D diagonalPath1 = new Line2D.Double( p0, p2 );
        Line2D diagonalPath2 = new Line2D.Double( p1, p3 );
        g2.setPaint( Color.RED );
        g2.draw( diagonalPath1 );
        g2.draw( diagonalPath2 );

        // draw a circle at the centroid (geometric center)
        double diameter = 12;
        Point2D centroid = PolygonUtils.getCentroid( points );
        double x = centroid.getX() - ( diameter / 2 );
        double y = centroid.getY() - ( diameter / 2 );
        Ellipse2D circlePath = new Ellipse2D.Double( x, y, diameter, diameter );
        g2.setPaint( Color.GREEN );
        g2.draw( circlePath );
    }

    public static void main( String[] args ) {
        JFrame frame = new TestCentroid();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setSize( 500, 300 );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
