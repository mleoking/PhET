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
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.faraday.model.Coil;


/**
 * PickupWidget
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupWidget extends CompositePhetGraphic implements SimpleObserver {

    private CoilGraphic _coil;
    private VoltMeterGraphic _meter;
    private LightBulbGraphic _bulb;
    private Coil _coilModel;
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public PickupWidget( Component component, CoilGraphic coil, VoltMeterGraphic meter, LightBulbGraphic bulb, Coil coilModel ) {
        super( component );
        
        _coil = coil;
        _meter = meter;
        _bulb = bulb;
        _coilModel = coilModel;
        
        super.addGraphic( bulb );
        super.addGraphic( meter );
        super.addGraphic( coil );

        enableBulb();
        
        // Set relative position of component graphics.
        _meter.setLocation( 0, -170 );
        _bulb.setLocation( 0, -100 );
        
        // Interactivity
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                double x = _coilModel.getX() + e.getDx();
                double y = _coilModel.getY() + e.getDy();
                _coilModel.setLocation( x, y );
            }
        } );
    }
    
    public void update() {
        double x = _coilModel.getX();
        double y = _coilModel.getY();
        super.clearTransform();
        super.translate( x, y );
    }
    
    public void enableBulb() {
        _bulb.setVisible( true );
        _meter.setVisible( false );
    }
    
    public void enableMeter() {
        _bulb.setVisible( false );
        _meter.setVisible( true );
    }

}
