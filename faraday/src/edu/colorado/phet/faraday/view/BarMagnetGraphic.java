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
import edu.colorado.phet.faraday.model.BarMagnet;


/**
 * BarMagnetGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetGraphic extends PhetImageGraphic implements SimpleObserver {

    private BarMagnet _barMagnetModel;
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public BarMagnetGraphic( Component component, BarMagnet barMagnetModel ) {
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

    /**
     *  This is called when the model changes, and updates the view to match the model.
     */
    public void update() {      
        clearTransform();
        rotate( Math.toRadians( _barMagnetModel.getDirection() ) );
        scale( _barMagnetModel.getStrength()/100 );
        translate( _barMagnetModel.getX(), _barMagnetModel.getY() );
    }
}
