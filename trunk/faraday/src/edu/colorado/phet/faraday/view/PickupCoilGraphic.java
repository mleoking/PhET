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

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.PickupCoil;


/**
 * PickupCoilGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic extends CompositePhetGraphic implements SimpleObserver {

    private PickupCoil _pickupCoilModel;
    PhetShapeGraphic _illumination;
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public PickupCoilGraphic( Component component, PickupCoil pickupCoilModel ) {
        super( component );
        
        _pickupCoilModel = pickupCoilModel;
        
        // Create component graphics.
        _illumination = new PhetShapeGraphic( component );
        _illumination.setShape( new Ellipse2D.Double( 0, 0, 100, 100 ) );
        _illumination.setPaint( new Color(255,0,0,0) );
        super.addGraphic( _illumination );
        PhetImageGraphic lightBulb = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
        super.addGraphic( lightBulb );
        PhetImageGraphic voltMeter = new PhetImageGraphic( component, FaradayConfig.VOLTMETER_IMAGE );
        super.addGraphic( voltMeter );
        PhetImageGraphic loop1 = new PhetImageGraphic( component, FaradayConfig.LOOP_IMAGE );
        super.addGraphic( loop1 );
        PhetImageGraphic loop2 = new PhetImageGraphic( component, FaradayConfig.LOOP_IMAGE );
        super.addGraphic( loop2 );
        
        // Set relative position of component graphics.
        int bulbWidth = lightBulb.getWidth();
        int bulbHeight = lightBulb.getHeight();
        int meterWidth = voltMeter.getWidth();
        int meterHeight = voltMeter.getHeight();
        lightBulb.setLocation( (meterWidth/2) - (bulbWidth/2), 0 );
        voltMeter.setLocation( 0, bulbHeight - 20 );
        int x = 55;
        int y = bulbHeight + meterHeight - 35;
        int spacing = 15;
        loop1.setLocation( x, y ); x+= spacing;
        loop2.setLocation( x, y ); x+= spacing;
        
        // Interactivity
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                double x = _pickupCoilModel.getX() + e.getDx();
                double y = _pickupCoilModel.getY() + e.getDy();
                _pickupCoilModel.setLocation( x, y );
            }
        } );
        
        // Synchronize with model.
        update();
    }
    
    public void update() {
        
        clearTransform();
        translate( _pickupCoilModel.getX(), _pickupCoilModel.getY() );
        
        System.out.println( "emf = " + _pickupCoilModel.getEMF() ); //XXX
        double emf = Math.abs( _pickupCoilModel.getEMF() );
        double alpha = MathUtil.clamp( 0, (int)( 255 * (emf / 2000) ), 255 );
        _illumination.setPaint( new Color( 255, 0, 0, (int)alpha ) );
    }

}
