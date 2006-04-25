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
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.mri.MriConfig;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * GradientMagnetControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientMagnetControl extends ModelSlider {

    public GradientMagnetControl( final GradientElectromagnet magnet ) {
        super( "Current to Gradient Magnet", "", 0, MriConfig.MAX_FADING_COIL_CURRENT / 5, 0 );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                magnet.setCurrent( getValue() );
            }
        } );
        magnet.setCurrent( 0 );
    }
}
