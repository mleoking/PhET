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

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * PickupCoilGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic extends CompositePhetGraphic {

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public PickupCoilGraphic( Component component ) {
        super( component );
        
        // Create component graphics.
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
            public void translationOccurred( TranslationEvent translationEvent ) {
                translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        } );
        
        scale( 0.75 );
        rotate( Math.toRadians(45) );
    }

}
