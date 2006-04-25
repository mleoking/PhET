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
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.MriModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * FadingMagnetControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FadingMagnetControl extends ModelSlider {

    private static double MIN_CURRENT = MriConfig.MAX_FADING_COIL_CURRENT / 50;

    public FadingMagnetControl( final MriModel model ) {
        super( "Current to Magnet",
               "",
               MIN_CURRENT,
               MriConfig.MAX_FADING_COIL_CURRENT,
               MIN_CURRENT );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateMagnets( model, MIN_CURRENT );
            }
        } );
        updateMagnets( model, MIN_CURRENT );
    }

    private void updateMagnets( MriModel model, double value ) {
        Electromagnet upperMagnet = model.getUpperMagnet();
        upperMagnet.setCurrent( getValue() );
        Electromagnet lowerMagnet = model.getLowerMagnet();
        lowerMagnet.setCurrent( getValue() );
    }
}