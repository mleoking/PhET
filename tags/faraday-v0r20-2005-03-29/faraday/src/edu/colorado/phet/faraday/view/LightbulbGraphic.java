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
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.util.IRescaler;


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
    private IRescaler _rescaler;
    private double _previousIntensity;
    private ArrayList _rays; // array of PhetShapeGraphic
    private Color _rayColor;
    private Stroke _rayStroke;
    
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

        _rays = new ArrayList();
       
        // Light bulb
        {
            PhetImageGraphic lightBulbGraphic = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
            super.addGraphic( lightBulbGraphic, BULB_LAYER );
            int rx = lightBulbGraphic.getImage().getWidth() / 2;
            int ry = lightBulbGraphic.getImage().getHeight();
            lightBulbGraphic.setRegistrationPoint( rx, ry );
        }
        
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
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
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Set the rescaler, applied to the intensity.
     * 
     * @param rescaler
     */
    public void setRescaler( IRescaler rescaler ) {
        _rescaler = rescaler;
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
            
            // Rescale the intensity to improve the visual effect.
            if ( _rescaler != null ) {
                intensity = _rescaler.rescale( intensity );
                intensity = MathUtil.clamp( 0, intensity, 1 );
            }

            // If the intensity hasn't changed, do nothing.
            if ( _previousIntensity == intensity ) {
                return;
            }
            _previousIntensity = intensity;

            // Remove existing rays.
            for ( int i = 0; i < _rays.size(); i++ ) {
                removeGraphic( (PhetShapeGraphic) _rays.get(i) );
            }
            _rays.clear();

            // If intensity is zero, we're done.
            if ( intensity == 0 ) {
                repaint();
                return;
            }

            // Number of rays is a function of intensity.
            final int numberOfRays = MIN_RAYS + (int)( intensity * (MAX_RAYS - MIN_RAYS) );

            // Ray dimensions are a function of intensity.
            final double rayLength = MIN_RAY_LENGTH + ( intensity * (MAX_RAY_LENGTH - MIN_RAY_LENGTH) );
            final double rayWidth = MIN_RAY_WIDTH + ( intensity * (MAX_RAY_WIDTH - MIN_RAY_WIDTH) );
            _rayStroke = new BasicStroke( (float) rayWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

            // Rays fill part of a circle, incrementing clockwise.
            double angle = RAYS_START_ANGLE;
            final double deltaAngle = RAYS_ARC_ANGLE / ( numberOfRays - 1 );

            // Create the rays.
            for ( int i = 0; i < numberOfRays; i++ ) {
                
                // Each ray's shape is a line.
                AbstractVector2D vec = ImmutableVector2D.Double.parseAngleAndMagnitude( BULB_RADIUS, angle );
                AbstractVector2D vec1 = ImmutableVector2D.Double.parseAngleAndMagnitude( rayLength + BULB_RADIUS, angle );
                Point2D end = vec.getDestination( RAYS_ORIGIN );
                Point2D end2 = vec1.getDestination( RAYS_ORIGIN );
                Line2D.Double line = new Line2D.Double( end, end2 );
                
                // Each ray is a PhetShapeGraphic.
                PhetShapeGraphic ray = new PhetShapeGraphic( getComponent() );
                ray.setShape( line );
                ray.setPaint( RAY_COLOR );
                ray.setStroke( _rayStroke );
                
                // Add the ray to the composite graphic and to the array.
                addGraphic( ray, RAYS_LAYER );
                _rays.add( ray );

                // Increment the angle.
                angle += deltaAngle;
            }

            repaint();
        }
        
    } // update
}
