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

import java.awt.Component;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;


/**
 * BarMagnetGraphic is the graphical representation of a bar magnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AbstractMagnet _magnetModel;
    
    //----------------------------------------------------------------------------
    // Constructors
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
        
        // Registration point is the center of the image.
        setRegistrationPoint( getImage().getWidth() / 2, getImage().getHeight() / 2 );
        
        // Save a reference to the model.
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                double x = _magnetModel.getX() + e.getDx();
                double y = _magnetModel.getY() + e.getDy();
                _magnetModel.setLocation( x, y );
            }
        } );
        
        // Synchronize view with model.
        update();
    }

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
            rotate( Math.toRadians( _magnetModel.getDirection() ) );
            
            // Scale
            double scaleX = _magnetModel.getWidth() / getImage().getWidth();
            double scaleY = _magnetModel.getHeight() / getImage().getHeight();
            scale( scaleX, scaleY );
            
            // Location
            setLocation( (int) _magnetModel.getX(), (int) _magnetModel.getY() );
        }
    }
}
