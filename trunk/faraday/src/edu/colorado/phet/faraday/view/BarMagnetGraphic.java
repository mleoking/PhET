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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;


/**
 * BarMagnetGraphic is the graphical representation of a bar magnet.
 * The registration point is at the center of the image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Composite COMPOSITE = 
        AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.9f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AbstractMagnet _magnetModel;
    private boolean _transparencyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param barMagnetModel model of the bar magnet
     */
    public BarMagnetGraphic( Component component, AbstractMagnet magnetModel ) {
        super( component, FaradayConfig.BAR_MAGNET_IMAGE );
        
        assert( component != null );
        assert( magnetModel != null );
        
        // Save a reference to the model.
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
               
        // Registration point is the center of the image.
        setRegistrationPoint( getImage().getWidth() / 2, getImage().getHeight() / 2 );

        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                // Translate if the mouse cursor is inside the parent component.
                if ( getComponent().contains( e.getMouseEvent().getPoint() ) ) {
                    double x = _magnetModel.getX() + e.getDx();
                    double y = _magnetModel.getY() + e.getDy();
                    _magnetModel.setLocation( x, y );
                }
            }
        } );
        
        // Use the opaque image by default.
        setTransparencyEnabled( false );
        
        // Synchronize view with model.
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
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
     * Enabled and disables transparency of the magnet graphic.
     * 
     * @param enabled true for transparency, false for opaque
     */
    public void setTransparencyEnabled( boolean enabled ) {
        _transparencyEnabled = enabled;
        repaint();
    }
    
    /**
     * Gets the current state of the magnet graphic transparency.
     * 
     * @return true if transparency, false if opaque
     */
    public boolean isTransparencyEnabled() {
        return _transparencyEnabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        if ( isVisible() ) {
            
            clearTransform();

            // Rotation
            rotate( _magnetModel.getDirection() );
            
            // Scale
            double scaleX = _magnetModel.getWidth() / getImage().getWidth();
            double scaleY = _magnetModel.getHeight() / getImage().getHeight();
            scale( scaleX, scaleY );
            
            // Location
            setLocation( (int) _magnetModel.getX(), (int) _magnetModel.getY() );
            
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetImageGraphic overrides
    //----------------------------------------------------------------------------

    /**
     * Draws the magnet.  
     * If transparency is enabled, use alpha compositing
     * to make the magnet slightly transparent.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            if ( _transparencyEnabled ) {
                Composite oldComposite = g2.getComposite(); // save
                g2.setComposite( COMPOSITE );
                super.paint( g2 );
                g2.setComposite( oldComposite ); // restore
            }
            else {
                super.paint( g2 );
            }
        }   
    }
}
