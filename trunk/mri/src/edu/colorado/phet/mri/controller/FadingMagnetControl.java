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
import edu.colorado.phet.common.view.util.SimStrings;
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

    private static double MIN_FIELD = 0;
    private static double DEFAULT_FIELD = MriConfig.MAX_FADING_COIL_FIELD / 4;

    public FadingMagnetControl( final MriModel model ) {
        super( SimStrings.get( "ControlPanel.MagneticField" ),
               "Tesla",
               MIN_FIELD,
               MriConfig.MAX_FADING_COIL_FIELD,
               DEFAULT_FIELD );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateMagnets( model, MIN_FIELD );
            }
        } );
        updateMagnets( model, MIN_FIELD );
    }

    private void updateMagnets( MriModel model, double value ) {
        Electromagnet upperMagnet = model.getUpperMagnet();
        upperMagnet.setFieldStrength( getValue() / 2 );
        Electromagnet lowerMagnet = model.getLowerMagnet();
        {
            lowerMagnet.setFieldStrength( getValue() / 2 );
        }
    }
}