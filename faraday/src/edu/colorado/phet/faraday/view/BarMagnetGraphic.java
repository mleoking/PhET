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
import edu.colorado.phet.faraday.model.HollywoodMagnet;


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

    private HollywoodMagnet _barMagnetModel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param barMagnetModel model of the bar magnet
     */
    public BarMagnetGraphic( Component component, HollywoodMagnet barMagnetModel ) {
        super( component, FaradayConfig.BAR_MAGNET_IMAGE );
        
        // Registration point is the center of the image.
        setRegistrationPoint( getImage().getWidth() / 2, getImage().getHeight() / 2 );
        
        // Save a reference to the model.
        _barMagnetModel = barMagnetModel;
        
        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                double x = _barMagnetModel.getX() + e.getDx();
                double y = _barMagnetModel.getY() + e.getDy();
                _barMagnetModel.setLocation( x, y );
            }
        } );
        
        // Synchronize view with model.
        update();
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        // Determine how to scale the image.
        double scaleX = _barMagnetModel.getWidth() / getImage().getWidth();
        double scaleY = _barMagnetModel.getHeight() / getImage().getHeight();
        
        clearTransform();
        rotate( Math.toRadians( _barMagnetModel.getDirection() ) );
        scale( scaleX, scaleY );
        translate( _barMagnetModel.getX(), _barMagnetModel.getY() );
    }
}
