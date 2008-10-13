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

import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.util.ControlBorderFactory;
import edu.colorado.phet.mri.util.SliderControl;

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
               MriResources.getString( "ControlPanel.MagneticField" ) + ":",
               MriResources.getString( "ControlPanel.Tesla" ),
               5,
               new Insets( 0, 0, 0, 0 )
        );
        setTextEditable( true );
        setBorder( ControlBorderFactory.createPrimaryBorder( MriResources.getString( "ControlPanel.MainMagnet" ) ) );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateMagnets( model );
            }
        } );
        updateMagnets( model );
        getSlider().addMouseListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
//                System.out.println( "FadingMagnetControl.mouseReleased" );
                if( model.isTransitionMatch() ) {
                    double magnetStrength = model.getMatchingMainMagnetField();
                    System.out.println( "magnet strength=" + magnetStrength );
                    setValue( magnetStrength );
                }
            }
        } );
    }

    private void updateMagnets( MriModel model ) {
//        System.out.println( "getValue() = " + getValue() );
        model.getUpperMagnet().setFieldStrength( getValue() / 2 );
        model.getLowerMagnet().setFieldStrength( getValue() / 2 );
    }
}