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
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * SourceCoilGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SourceCoilGraphic extends CompositePhetGraphic {

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public SourceCoilGraphic( Component component ) {
        super( component );
        
        // Create component graphics.
        PhetImageGraphic bField = new PhetImageGraphic( component, FaradayConfig.BFIELD_IMAGE );
        super.addGraphic( bField );
        PhetImageGraphic coil = new PhetImageGraphic( component, FaradayConfig.SOURCE_COIL_IMAGE );
        super.addGraphic( coil );
        
        // Set relative position of component graphics.
        int x = -660;
        int y = -195;
        bField.translate( x, y );
        
        // Interactivity
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                translate( translationEvent.getDx(), translationEvent.getDy() );
            }
        } );
    }

}
