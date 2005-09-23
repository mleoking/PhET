/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * IntensityReadout
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IntensityReadout extends GraphicLayerSet implements CollimatedBeam.RateChangeListener {

    private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );

    private JTextField readout;
    private PhetGraphic readoutGraphic;

    public IntensityReadout( final Component component, final CollimatedBeam beam ) {
        super( component );

        beam.addRateChangeListener( this );

        readout = new JTextField( 5 );
        readout.setHorizontalAlignment( JTextField.CENTER );
        readout.setFont( VALUE_FONT );
        readout.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double photonsPerSecond = 0;
                try {
                    String text = readout.getText().toLowerCase();
                    int nmLoc = text.indexOf( "intensity" );
                    text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
                    photonsPerSecond = Double.parseDouble( text );
                    beam.setPhotonsPerSecond( photonsPerSecond );
                    update( photonsPerSecond );
                }
                catch( NumberFormatException e1 ) {
                    JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ), "Wavelength must be numeric, or a number followed by \"nm\"" );
                }
            }
        } );
        readoutGraphic = PhetJComponent.newInstance( component, readout );
        addGraphic( readoutGraphic, 1E9 );

        update( 123 ); // dummy value
    }

    private void update( double wavelength ) {
        // Update the text
        DecimalFormat voltageFormat = new DecimalFormat( "#0" );
        readout.setText( voltageFormat.format( wavelength ) );
//            readout.setText( voltageFormat.format( wavelength ) + " nm" );
//            Object[] args = {voltageFormat.format( Math.abs( wavelength ) )};
//            String text = MessageFormat.format( "nm", args );
//            readout.setText( text );
    }

    void setValue( double wavelength ) {
        update( wavelength );
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        update( event.getRate() );
    }
}
