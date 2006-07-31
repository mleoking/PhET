/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.GradientElectromagnet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * GradientMagnetControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientMagnetControl extends ModelSlider {
    // Makes the magnet in the model have the appropriate current, but causes a smaller
    // number to be displayed in the control panel. This was requested by Carl.
    public final static double VIEW_TO_MODEL_FACTOR = 10;

    public GradientMagnetControl( final GradientElectromagnet horizontalMagnet, String title ) {
        super( title, "", 0, MriConfig.MAX_GRADIENT_COIL_FIELD, 0 );
//        super( title, "", 0, MriConfig.MAX_GRADIENT_COIL_CURRENT, 0 );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                horizontalMagnet.setFieldStrength( getValue() * VIEW_TO_MODEL_FACTOR );
//                horizontalMagnet.setCurrent( getValue() * VIEW_TO_MODEL_FACTOR );
            }
        } );
        horizontalMagnet.setFieldStrength( 0 );
//        horizontalMagnet.setCurrent( 0 );
    }
}
