/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.photoelectric.model.util.BeamIntensityMeter;
import edu.colorado.phet.photoelectric.model.util.ScalarDataRecorder;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * AmmeterView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IntensityView extends JPanel {

    private JTextField intensityTF;
    private DecimalFormat format = new DecimalFormat( "#0.0000" );

    public IntensityView( final BeamIntensityMeter beamIntensityMeter ) {
        setLayout( new GridLayout( 1, 2 ));
        add( new JLabel( "Intensity: "));
        intensityTF = new JTextField( 10 );
        add( intensityTF );

        beamIntensityMeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
            public void update( ScalarDataRecorder.UpdateEvent event ) {
                double current = beamIntensityMeter.getIntesity();
                intensityTF.setText( format.format( current ));
            }
        } );
    }
}
