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

//    private static double MIN_CURRENT = MriConfig.MAX_FADING_COIL_CURRENT / 50;
    private static double MIN_FIELD = 0;

    public FadingMagnetControl( final MriModel model ) {
        super( SimStrings.get( "ControlPanel.MagneticField" ),
               "Tesla",
               MIN_FIELD,
//               MIN_CURRENT,
MriConfig.MAX_FADING_COIL_FIELD,
//               MriConfig.MAX_FADING_COIL_CURRENT,
MIN_FIELD );
//               MIN_CURRENT );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateMagnets( model, MIN_FIELD );
//                updateMagnets( model, MIN_CURRENT );
            }
        } );
        updateMagnets( model, MIN_FIELD );
//        updateMagnets( model, MIN_CURRENT );

        // Add a listener that will update the slider if the magnet's current is changed by
        // some means other than the slider
//        Electromagnet.ChangeListener magnetListener = new Electromagnet.ChangeListener() {
//            public void stateChanged( Electromagnet.ChangeEvent event ) {
//                setValue( event.getElectromagnet().getFieldStrength() );
//                setValue( event.getElectromagnet().getCurrent() );
//            }
//        };
//        model.getUpperMagnet().addChangeListener( magnetListener );
//        model.getLowerMagnet().addChangeListener( magnetListener );
    }

    private void updateMagnets( MriModel model, double value ) {
        Electromagnet upperMagnet = model.getUpperMagnet();
        upperMagnet.setFieldStrength( getValue() / 2 );
//        upperMagnet.setCurrent( getValue() );
        Electromagnet lowerMagnet = model.getLowerMagnet();
//        lowerMagnet.setCurrent( getValue() );
        lowerMagnet.setFieldStrength( getValue() / 2 );
    }
}