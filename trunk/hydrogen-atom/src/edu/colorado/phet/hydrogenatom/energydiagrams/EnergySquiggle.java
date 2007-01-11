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

/**
 * EnergySquiggle is the squiggle line shown in the energy diagrams.
 * A squiggle can be draw between any 2 points.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergySquiggle extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_SQUIGGLE_LENGTH = 5; // pixels
    private static final double SQUIGGLE_PERIOD = 10; // pixels
    private static final double SQUIGGLE_AMPLITUDE = 4; // pixels
    protected static final Stroke SQUIGGLE_STROKE = new BasicStroke( 2f );
    private static final Dimension ARROW_HEAD_SIZE = new Dimension( 20, 10 );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a squiggle between two points, p1 and p2.
     * The squiggle has an arrow head at the p2 end.
     * The color of the squiggle is determined by the specified wavelength.
     * 
     * @param p1
     * @param p2
     * @param wavelength
     */
    public EnergySquiggle( Point2D p1, Point2D p2, double wavelength ) {
        this( p1.getX(), p1.getY(), p2.getX(), p2.getY(), wavelength );
    }
    
    /**
     * Creates a squiggle between two points, (x1,y1) and (x2,y2).
     * The squiggle has an arrow head at the (x2,y2) end.
     * The color of the squiggle is determined by the specified wavelength.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param wavelength
     */
    public EnergySquiggle( double x1, double y1, double x2, double y2, double wavelength ) {
        super();
        
        // Distance between the 2 points
        double distance = Point2D.distance( x1, y1, x2, y2 );
        
        // Color that corresponds to the wavelength
        Color color = ColorUtils.wavelengthToColor( wavelength );
        
        /*
         * We'll start by drawing all the geometry with this orientation:
         * 
         *      ---------------->
         *      
         * The left end of the line is at (0,0), the tip of the arrow is at (distance,0).
         */
        
        /*
         * The arrow head is drawn only if the distance between the points is 
         * large enough to fit both the arrow head and a minimum amount of squiggle.
         * If the distance isn't sufficient, then our squiggle will have no arrow head.
         */
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
        
        /*
         * The squiggle is a sinusoidal line, with fixed period and amplitude.
         * If the 2 points are too close together, the sinusoidal nature of 
         * the line won't be intelligible, so we simply draw a straight line.
         */
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
        
        /* 
         * Since we drew our squiggle in the orientation described above, 
         * we now need to transform this node so that the squiggle lines 
         * up with the specified points. This transformation involves 
         * a rotation and a translation.
         */
        double phi = Math.atan2( y2 - y1, x2 - x1 ); // conversion to Polar coordinates
        AffineTransform xform = getTransform();
        if ( xform == null ) {
            xform = new AffineTransform();
        }
        xform.setToIdentity();
        xform.translate( x1, y1 );
        xform.rotate( phi );
        setTransform( xform );
    }
}
