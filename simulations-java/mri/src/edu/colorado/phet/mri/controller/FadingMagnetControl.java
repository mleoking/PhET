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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.util.ControlBorderFactory;
import edu.colorado.phet.mri.util.SliderControl;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * FadingMagnetControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FadingMagnetControl extends SliderControl {

    private static double MIN_FIELD = 0;
    private static double DEFAULT_FIELD = MriConfig.MAX_FADING_COIL_FIELD / 4;

    public FadingMagnetControl( final MriModel model ) {
        super( DEFAULT_FIELD,
               MIN_FIELD,
               MriConfig.MAX_FADING_COIL_FIELD,
               0.5, 1, 2,
               SimStrings.get( "ControlPanel.MagneticField" ) + ":",
               SimStrings.get( "ControlPanel.Tesla" ),
               5,
               new Insets( 0, 0, 0, 0 )
        );
        setTextEditable( true );
        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ControlPanel.MainMagnet" ) ) );
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
        lowerMagnet.setFieldStrength( getValue() / 2 );
    }
}