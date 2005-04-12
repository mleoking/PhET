/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * LightBulbGraphic is the graphical representation of a lightbulb.
 * The bulb's relative intensity can be set.
 * Registration point is at bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightbulbGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // These parameters affect drawing order.
    private static final double RAYS_LAYER = 0;
    private static final double BULB_LAYER = 1;
    
    // These parameters affect an individual ray.
    private static final Color RAY_COLOR = Color.YELLOW;
    private static final Stroke RAY_STROKE_BIG = new BasicStroke( 3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Stroke RAY_STROKE_MEDIUM = new BasicStroke( 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Stroke RAY_STROKE_SMALL = new BasicStroke( 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final int MAX_RAY_LENGTH = 500;
    private static final int MIN_RAY_LENGTH = 0;
    private static final double MAX_RAY_WIDTH = 4.0;
    private static final double MIN_RAY_WIDTH = 2.0;

    // These parameters affect the collection of rays.
    private static final int MAX_RAYS = 60;
    private static final int MIN_RAYS = 8;
    private static final double RAYS_START_ANGLE = Math.toRadians( 135 );
    private static final double RAYS_ARC_ANGLE = Math.toRadians( 270 );
    private static final Point2D RAYS_ORIGIN = new Point2D.Double( 0, -90 );
    private static final double BULB_RADIUS = 30.0;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Lightbulb _lightBulbModel;
    private double _previousIntensity;
    private CompositePhetGraphic _raysGraphic;
    private Color _rayColor;
    
    // Reusable objects, to reduce memory allocation
    private Vector2D _someVector1, _someVector2;
    private Point2D _somePoint1, _somePoint2;
    private ArrayList _rays; // array of Ray
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param lightBulbModel
     */
    public LightbulbGraphic( Component component, Lightbulb lightBulbModel ) {
        super( component );
        
        assert( component != null );
        assert( lightBulbModel !=  null );
        
        _lightBulbModel = lightBulbModel;
        _lightBulbModel.addObserver( this );

        // Lightbulb
        {
            PhetImageGraphic lightBulbGraphic = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
            super.addGraphic( lightBulbGraphic, BULB_LAYER );
            int rx = lightBulbGraphic.getImage().getWidth() / 2;
            int ry = lightBulbGraphic.getImage().getHeight();
            lightBulbGraphic.setRegistrationPoint( rx, ry );
        }
        
        // Rays
        {
            _raysGraphic = new CompositePhetGraphic();
            addGraphic( _raysGraphic, RAYS_LAYER );
        }
        
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Reusable objects
        _someVector1 = new Vector2D();
        _someVector2 = new Vector2D();
        _somePoint1 = new Point2D.Double();
        _somePoint2 = new Point2D.Double();
        
        // Prepopulate a set of reusable rays.
        _rays = new ArrayList();
        for ( int i = 0; i < MAX_RAYS; i++ ) {
            _rays.add( new Ray( component ) );
        }
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _lightBulbModel.removeObserver( this );
        _lightBulbModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Synchronize the view with the model.
     * <p>
     * The algorithm for rendering the light rays was adapted from 
     * edu.colorado.phet.cck3.circuit.components.LightBulbGraphic.setIntensity.
     * 
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        setVisible( _lightBulbModel.isEnabled() );
        
        if ( isVisible() ) {
            
            // Get the light intensity, a value in the range 0...+1.
            double intensity = _lightBulbModel.getIntensity();

            // If the intensity hasn't changed, do nothing.
            if ( _previousIntensity == intensity ) {
                return;
            }
            _previousIntensity = intensity;

            // Remove existing rays.
            _raysGraphic.clear();

            // If intensity is zero, we're done.
            if ( intensity == 0 ) {
                repaint();
                return;
            }

            // Number of rays is a function of intensity.
            final int numberOfRays = MIN_RAYS + (int)( intensity * (MAX_RAYS - MIN_RAYS) );

            // Ray length is a function of intensity.
            final double rayLength = MIN_RAY_LENGTH + ( intensity * (MAX_RAY_LENGTH - MIN_RAY_LENGTH) );
            
            // Pick one of 3 pre-allocated ray widths.
            Stroke rayStroke = RAY_STROKE_SMALL;
            if ( rayLength > ( MAX_RAY_LENGTH * 0.6 ) ) {
                rayStroke = RAY_STROKE_BIG;
            }
            else if ( rayLength > ( MAX_RAY_LENGTH * 0.3 ) ) {
                rayStroke = RAY_STROKE_MEDIUM;
            }

            // Rays fill part of a circle, incrementing clockwise.
            double angle = RAYS_START_ANGLE;
            final double deltaAngle = RAYS_ARC_ANGLE / ( numberOfRays - 1 );

            // Create the rays.
            for ( int i = 0; i < numberOfRays; i++ ) {
                
                // Determine the end points of the ray.
                _someVector1.setMagnitudeAngle( BULB_RADIUS, angle );
                _someVector2.setMagnitudeAngle( rayLength + BULB_RADIUS, angle );
                _someVector1.getTransformedPoint( RAYS_ORIGIN , _somePoint1 /* output */ );
                _someVector2.getTransformedPoint( RAYS_ORIGIN , _somePoint2 /* output */ );
                
                // Get a Ray from the list of reusable rays.
                Ray ray = null;
                if ( i < _rays.size() ) {
                    ray = (Ray) _rays.get( i );
                }
                else {
                    // If we don't have enough rays, then make one.
                    ray = new Ray( getComponent() );
                    _rays.add( ray );
                }
                ray.setLine( _somePoint1, _somePoint2, rayStroke );
                
                // Add the ray to the composite graphic.
                _raysGraphic.addGraphic( ray, RAYS_LAYER );

                // Increment the angle.
                angle += deltaAngle;
            }

            repaint();
        }
        
    } // update
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * Ray is the graphical representation of a light ray.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class Ray extends PhetShapeGraphic {
        
        private Line2D _line; // reusable line
        
        /**
         * Sole constructor.
         * 
         * @param component
         */
        public Ray( Component component ) {
            super( component );
            _line = new Line2D.Double();
            setShape( _line );
            setPaint( RAY_COLOR );
        }
        
        /**
         * Sets the end points and stroke that describe the ray's line.
         * 
         * @param p1
         * @param p2
         * @param stroke
         */
        public void setLine( Point2D p1, Point2D p2, Stroke stroke ) {
            _line.setLine( p1.getX(), p1.getY(), p2.getX(), p2.getY() );
            setStroke( stroke );
        }
        
        /**
         * Optimized paint method, uses Graphics.drawLine instead of Graphics2D.draw.
         */
        public void paint( Graphics2D g2 ) {
            if ( isVisible() ) {
                super.saveGraphicsState( g2 );
                g2.transform( getNetTransform() );
                g2.setPaint( getFill() );
                g2.setStroke( getStroke() );
                g2.drawLine( (int)_line.getX1(), (int)_line.getY1(), (int)_line.getX2(), (int)_line.getY2() );
                super.restoreGraphicsState();
            }
        }
    }
}
