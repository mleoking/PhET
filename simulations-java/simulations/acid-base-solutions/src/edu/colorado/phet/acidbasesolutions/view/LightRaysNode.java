/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.faraday.util.Vector2D;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * LightRaysGraphic is the graphical representation of a set of light rays.
 * This is a Piccolo adaptation of faraday's LightBulbGraphic class.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class LightRaysNode extends PComposite {
    
    // Number of rays
    private static final int MAX_RAYS = 60;
    private static final int MIN_RAYS = 8;
    
    // Physical dimensions, in pixels
    private static final int MAX_RAY_LENGTH = 350;
    private static final int MIN_RAY_LENGTH = 0;
    
    // Angles
    private static final double RAYS_START_ANGLE = Math.toRadians( 135 );
    private static final double RAYS_ARC_ANGLE = Math.toRadians( 270 );
    
    // Color and strokes
    private static final Color RAY_COLOR = Color.YELLOW;
    private static final BasicStroke RAY_STROKE_BIG = new BasicStroke( 3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final BasicStroke RAY_STROKE_MEDIUM = new BasicStroke( 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final BasicStroke RAY_STROKE_SMALL = new BasicStroke( 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

    private final double bulbRadius;
    private double intensity;
    
    private final PPath[] cachePPaths; // reusable PPaths
    private final Line2D[] cacheLines; // reusable lines

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param bulbRadius the radius of the lightbulb
     */
    public LightRaysNode( double bulbRadius ) {

        this.bulbRadius = bulbRadius;
        this.intensity = 0;
        
        // Pre-populate reusable lists of reusable objects.
        cachePPaths = new PPath[ MAX_RAYS ];
        cacheLines = new Line2D[ MAX_RAYS ];
        for ( int i = 0; i < MAX_RAYS; i++ ) {
            cachePPaths[i] = new PPath();
            cacheLines[i] = new Line2D.Double();
        }
        
        update();
        
    }

    public void setIntensity( double intensity ) {
        if ( !( intensity >= 0 && intensity <= 1 ) ) {
            throw new IllegalArgumentException( "intensity range is 0-1: " + intensity );
        }
        if ( intensity != this.intensity ) {
            this.intensity = intensity;
            update();
        }
    }
    
    private void update() {
        
        // clear
        removeAllChildren();

        // If intensity is zero, we're done.
        if ( intensity == 0 ) {
            return;
        }

        // Number of rays is a function of intensity.
        final int numberOfRays = MIN_RAYS + (int) ( intensity * ( MAX_RAYS - MIN_RAYS ) );

        // Ray length is a function of intensity.
        final double rayLength = MIN_RAY_LENGTH + ( intensity * ( MAX_RAY_LENGTH - MIN_RAY_LENGTH ) );

        // Pick one of 3 pre-allocated ray widths.
        BasicStroke stroke = RAY_STROKE_SMALL;
        if ( rayLength > ( MAX_RAY_LENGTH * 0.6 ) ) {
            stroke = RAY_STROKE_BIG;
        }
        else if ( rayLength > ( MAX_RAY_LENGTH * 0.3 ) ) {
            stroke = RAY_STROKE_MEDIUM;
        }

        // Rays fill part of a circle, incrementing clockwise.
        double angle = RAYS_START_ANGLE;
        final double deltaAngle = RAYS_ARC_ANGLE / ( numberOfRays - 1 );

        // Create the rays.
        Vector2D v1 = new Vector2D();
        Vector2D v2 = new Vector2D(); 
        for ( int i = 0; i < numberOfRays; i++ ) {

            // Determine the end points of the ray.
            v1.setMagnitudeAngle( bulbRadius, angle );
            double x1 = v1.getX();
            double y1 = v1.getY();
            v2.setMagnitudeAngle( rayLength + bulbRadius, angle );
            double x2 = v2.getX();
            double y2 = v2.getY();
            
            // Get a line from the cache.
            Line2D line = cacheLines[i];
            line.setLine( x1, y1, x2, y2 );
            
            // Get a PPath fromn the cache.
            PPath pathNode = cachePPaths[i];
            pathNode.setPathTo( line );
            pathNode.setStroke( stroke );
            pathNode.setStrokePaint( RAY_COLOR );
            addChild( pathNode );

            // Increment the angle.
            angle += deltaAngle;
        }
    }
}
