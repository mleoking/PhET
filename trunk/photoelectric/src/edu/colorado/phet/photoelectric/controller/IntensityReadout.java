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

import edu.colorado.phet.common.math.MathUtil;
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
    private DecimalFormat voltageFormat = new DecimalFormat( "#0%" );

    private JTextField readout;
    private PhetGraphic readoutGraphic;
    private CollimatedBeam beam;

    public IntensityReadout( final Component component, CollimatedBeam beam ) {
        super( component );

        beam.addRateChangeListener( this );
        this.beam = beam;

        readout = new JTextField( 3 );
        readout.setHorizontalAlignment( JTextField.HORIZONTAL );
        readout.setFont( VALUE_FONT );
        readout.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double photonsPerSecond = 0;
                try {
                    String text = readout.getText().toLowerCase();
                    int nmLoc = text.indexOf( "%" );
                    text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
                    double percent = MathUtil.clamp( 0, Double.parseDouble( text ), 100 );
                    photonsPerSecond = percent / 100 * IntensityReadout.this.beam.getMaxPhotonsPerSecond();
                    System.out.println( "photonsPerSecond = " + photonsPerSecond );
                    IntensityReadout.this.beam.setPhotonsPerSecond( photonsPerSecond );
                    update( photonsPerSecond );
                }
                catch( NumberFormatException e1 ) {
                    JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ), "Wavelength must be numeric, or a number followed by \"%\"" );
                }
            }
        } );
        readoutGraphic = PhetJComponent.newInstance( component, readout );
        addGraphic( readoutGraphic, 1E9 );

        update( 123 ); // dummy value
    }

    private void update( double intensity ) {
        readout.setText( voltageFormat.format( intensity / beam.getMaxPhotonsPerSecond() ) );
    }

    void setValue( double wavelength ) {
        update( wavelength );
    }

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        update( event.getRate() );
    }
}
