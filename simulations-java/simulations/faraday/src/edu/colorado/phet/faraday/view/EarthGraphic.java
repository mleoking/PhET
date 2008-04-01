/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.model.BarMagnet;

/**
 * EarthGraphic draws a representation of planet earth.
 * The south pole of the Earth is aligned with the north pole of the magnet, and visa versa. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EarthGraphic extends PhetImageGraphic implements SimpleObserver {

    // the image is opaque, this operator is used to make it transparent
    private static final Composite COMPOSITE = 
        AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.75f /* alpha */ );
    
    private static final double IMAGE_ROTATION = -( Math.PI / 2 );  // Earth image was created with north up, bar magnet has north to the right
    private static final double IMAGE_SCALE = 0.60; // scales the Earth image
    
    private BarMagnet _barMagnetModel;
    
    /**
     * Constructor.
     * 
     * @param component
     * @param barMagnetModel
     */
    public EarthGraphic( Component component, BarMagnet barMagnetModel ) {
        super( component, FaradayResources.getImage( FaradayConstants.EARTH_IMAGE ) );
        
        setIgnoreMouse( true ); // not draggable
        
        // Save a reference to the model.
        _barMagnetModel = barMagnetModel;
        _barMagnetModel.addObserver( this );
        
        // origin is at the center of the image
        centerRegistrationPoint();
        
        update();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _barMagnetModel.removeObserver( this );
    }
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( isVisible() ) {
            
            clearTransform();
            
            // Scale
            scale( IMAGE_SCALE );

            // Rotation
            rotate( _barMagnetModel.getDirection() + IMAGE_ROTATION );
            
            // Location
            setLocation( (int) _barMagnetModel.getX(), (int) _barMagnetModel.getY() );
            
            repaint();
        }
    }
    
    /**
     * Draws Earth with transparency.  
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            Composite oldComposite = g2.getComposite(); // save
            g2.setComposite( COMPOSITE );
            super.paint( g2 );
            g2.setComposite( oldComposite ); // restore
        }
    }
}
