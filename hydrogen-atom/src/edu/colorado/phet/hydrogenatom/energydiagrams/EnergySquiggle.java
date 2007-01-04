/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


public class EnergySquiggle extends PComposite {

    private static final double MIN_SQUIGGLE_LENGTH = 5; // pixels
    private static final double SQUIGGLE_PERIOD = 10; // pixels
    private static final double SQUIGGLE_AMPLITUDE = 4; // pixels
    protected static final Stroke SQUIGGLE_STROKE = new BasicStroke( 2f );
    private static final Dimension ARROW_HEAD_SIZE = new Dimension( 20, 10 );
    
    public EnergySquiggle(  Point2D p1, Point2D p2, double wavelength ) {
        this( p1.getX(), p1.getY(), p2.getX(), p2.getY(), wavelength );
    }
    
    public EnergySquiggle( double x1, double y1, double x2, double y2, double wavelength ) {
        super();
        
        // Distance between the 2 points
        double distance = Point2D.distance( x1, y1, x2, y2 );
        
        Color color = ColorUtils.wavelengthToColor( wavelength );
        
        /*
         * Draw all the geometry with this orientation:
         * 
         *      ---------------->
         */
        
        // Arrow head, if distance between points permits
        boolean hasArrow = ( distance > ARROW_HEAD_SIZE.getHeight() + MIN_SQUIGGLE_LENGTH );
        if ( hasArrow ) {
            PPath arrowHeadNode = new PPath();
            arrowHeadNode.setPaint( color );
            arrowHeadNode.setStroke( null );
            GeneralPath arrowHeadPath = new GeneralPath();
            arrowHeadPath.moveTo( (float)distance, 0f );
            arrowHeadPath.lineTo( (float)( distance - ARROW_HEAD_SIZE.height ), ARROW_HEAD_SIZE.width / 2  );
            arrowHeadPath.lineTo( (float)( distance - ARROW_HEAD_SIZE.height ), -ARROW_HEAD_SIZE.width / 2  );
            arrowHeadPath.closePath();
            arrowHeadNode.setPathTo( arrowHeadPath );
            addChild( arrowHeadNode );
        }
        
        // Squiggly line
        PPath lineNode = new PPath();
        lineNode.setStroke( SQUIGGLE_STROKE );
        lineNode.setStrokePaint( color );
        addChild( lineNode );
        if ( distance >= MIN_SQUIGGLE_LENGTH ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            double maxX = ( hasArrow ) ? ( distance - ARROW_HEAD_SIZE.getHeight() ) : distance;
            for ( int x = 0; x < maxX; x++ ) {
                double angle =( x % SQUIGGLE_PERIOD ) * ( 2* Math.PI / SQUIGGLE_PERIOD );
                double y = SQUIGGLE_AMPLITUDE * Math.sin( angle );
                path.lineTo( x, (float)y );
            }
            lineNode.setPathTo( path );
        }
        else {
            // use a straight line if the points are too close together
            lineNode.setPathTo( new Line2D.Double( x1, y1, x2, y2 ) );
        }
        
        // Transform so that the geometry lines up with the specified points.
        double phi = Math.atan2( y2 - y1, x2 - x1 );
        AffineTransform xform = new AffineTransform();
        xform.translate( x1, y1 );
        xform.rotate( phi );
        setTransform( xform );
    }
}
