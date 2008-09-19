/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * LightRaysGraphic is the graphical representation of a set of light rays.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LightRaysGraphic extends PhetGraphic {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Number of rays
    private static final int MAX_RAYS = 60;
    private static final int MIN_RAYS = 8;
    
    // Physical dimensions, in pixels
    private static final int MAX_RAY_LENGTH = 500;
    private static final int MIN_RAY_LENGTH = 0;
    private static final double MAX_RAY_WIDTH = 4.0;
    private static final double MIN_RAY_WIDTH = 2.0;
    
    // Angles
    private static final double RAYS_START_ANGLE = Math.toRadians( 135 );
    private static final double RAYS_ARC_ANGLE = Math.toRadians( 270 );
    
    // Color and strokes
    private static final Color RAY_COLOR = Color.YELLOW;
    private static final BasicStroke RAY_STROKE_BIG = new BasicStroke( 3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final BasicStroke RAY_STROKE_MEDIUM = new BasicStroke( 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final BasicStroke RAY_STROKE_SMALL = new BasicStroke( 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _bulbRadius;
    private ArrayList _cacheLines; // array of Line2D that will be reused
    private ArrayList _drawLines; // array of Line2D that will be drawn
    private Rectangle _bounds;
    private RenderingHints _hints;
    private BasicStroke _stroke;
    private Vector2D _someVector1, _someVector2;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param bulbRadius the radius of the lightbulb
     */
    public LightRaysGraphic( Component component, double bulbRadius ) {
        super( component );

        _bulbRadius = bulbRadius;
        
        // Pre-populate a set of reusable lines.
        _cacheLines = new ArrayList();
        for ( int i = 0; i < MAX_RAYS; i++ ) {
            _cacheLines.add( new Line2D.Double() );
        }

        _drawLines = new ArrayList();
        _bounds = new Rectangle();
        _hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        _stroke = RAY_STROKE_SMALL;
        
        // Reusable objects
        _someVector1 = new Vector2D();
        _someVector2 = new Vector2D();    
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the intensity.
     * This generates the stroke and lines needed to represent the intensity.
     * The algorithm was adapted from 
     * edu.colorado.phet.cck3.circuit.components.LightBulbGraphic.setIntensity.
     * 
     * @param intensity
     */
    public void setIntensity( double intensity ) {

        // Clear the "draw" list.
        _drawLines.clear();

        // If intensity is zero, we're done.
        if ( intensity == 0 ) {
            _bounds.setBounds( 1, 1, 1, 1 );
            return;
        }

        // Number of rays is a function of intensity.
        final int numberOfRays = MIN_RAYS + (int) ( intensity * ( MAX_RAYS - MIN_RAYS ) );

        // Ray length is a function of intensity.
        final double rayLength = MIN_RAY_LENGTH + ( intensity * ( MAX_RAY_LENGTH - MIN_RAY_LENGTH ) );

        // Pick one of 3 pre-allocated ray widths.
        _stroke = RAY_STROKE_SMALL;
        if ( rayLength > ( MAX_RAY_LENGTH * 0.6 ) ) {
            _stroke = RAY_STROKE_BIG;
        }
        else if ( rayLength > ( MAX_RAY_LENGTH * 0.3 ) ) {
            _stroke = RAY_STROKE_MEDIUM;
        }

        // Rays fill part of a circle, incrementing clockwise.
        double angle = RAYS_START_ANGLE;
        final double deltaAngle = RAYS_ARC_ANGLE / ( numberOfRays - 1 );

        // Create the rays.
        for ( int i = 0; i < numberOfRays; i++ ) {

            // Determine the end points of the ray.
            _someVector1.setMagnitudeAngle( _bulbRadius, angle );
            double x1 = _someVector1.getX();
            double y1 = _someVector1.getY();
            _someVector2.setMagnitudeAngle( rayLength + _bulbRadius, angle );
            double x2 = _someVector2.getX();
            double y2 = _someVector2.getY();
            
            // Get a line from the cache.
            Line2D line = null;
            if ( i < _cacheLines.size() ) {
                line = (Line2D) _cacheLines.get( i );
            }
            else {
                // If we don't have enough lines, then allocate one.
                line = new Line2D.Double();
                _cacheLines.add( line );
            }
            line.setLine( x1, y1, x2, y2 );

            // Add the line to the "draw" list.
            _drawLines.add( line );

            // Increment the angle.
            angle += deltaAngle;
        }
        
        // Pre-compute the bounds.
        int radius = (int) ( rayLength + _bulbRadius + _stroke.getLineWidth() /* line caps */ );
        _bounds.setBounds( -radius, -radius, 2 * radius, 2 * radius );
        setBoundsDirty();
    }
    
    //----------------------------------------------------------------------------
    // PhetGraphic implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws the light rays.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        int numberOfRays = _drawLines.size();
        if ( isVisible() && numberOfRays > 0 ) {
            saveGraphicsState( g2 );
            
            g2.setRenderingHints( _hints );
            g2.setStroke( _stroke );
            g2.setPaint( RAY_COLOR );
            g2.transform( getNetTransform() );

            // Draw each of the ray lines.
            Line2D line;
            for ( int i = 0; i < numberOfRays; i++ ) {
                line = (Line2D) _drawLines.get( i );
                g2.drawLine( (int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2() );
            }
            
            restoreGraphicsState();
        }
    }

    /**
     * Determines the bounds of the light rays.
     * 
     * @return the bounds
     */
    protected Rectangle determineBounds() {
        return getNetTransform().createTransformedShape( _bounds ).getBounds();
    }
}
