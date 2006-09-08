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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.quantum.model.Beam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

/**
 * IntensityReadout
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IntensityReadout extends GraphicLayerSet implements Beam.RateChangeListener {

    private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
    private DecimalFormat format = new DecimalFormat( "#0%" );

    private JTextField readout;
    private PhetGraphic readoutGraphic;
    private Beam beam;

    public IntensityReadout( final Component component, final Beam beam ) {
        super( component );

        beam.addRateChangeListener( this );
        this.beam = beam;

        readout = new JTextField( 3 );
        readout.setHorizontalAlignment( JTextField.HORIZONTAL );
        readout.setFont( VALUE_FONT );
        readout.addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
                // noop
            }

            public void focusLost( FocusEvent e ) {
                update( component, beam );
            }
        } );
        readout.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update( component, beam );
            }
        } );
        readoutGraphic = PhetJComponent.newInstance( component, readout );
        addGraphic( readoutGraphic, 1E9 );

        update( 123 ); // dummy value
    }

    private double photonsPerSecondToReadoutValue( double photonsPerSecond ) {
        return photonsPerSecond / beam.getMaxPhotonsPerSecond();
    }

    private double readoutValueToPhotonsPerSecond() {
        String text = readout.getText().toLowerCase();
        int nmLoc = text.indexOf( "%" );
        text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
        double percent = MathUtil.clamp( 0, Double.parseDouble( text ), 100 );
        return  ( percent / 100 ) * this.beam.getMaxPhotonsPerSecond();
    }

    private void update( final Component component, final Beam beam ) {
        double photonsPerSecond = 0;
        try {
            String text = readout.getText().toLowerCase();
            int nmLoc = text.indexOf( "%" );
            text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
            double percent = MathUtil.clamp( 0, Double.parseDouble( text ), 100 );
            photonsPerSecond = percent / 100 * this.beam.getMaxPhotonsPerSecond();
            double intensity = percent / 100 * beam.getMaxIntensity( PhotoelectricConfig.MIN_WAVELENGTH,
                                    PhotoelectricConfig.MAX_WAVELENGTH );
            beam.setIntensity( intensity,
                                    PhotoelectricConfig.MAX_WAVELENGTH );
//            this.beam.setPhotonsPerSecond( photonsPerSecond );
//            this.beam.setPhotonsPerSecond( readoutValueToPhotonsPerSecond() );

//            update( percent );

//            update( photonsPerSecond );
        }
        catch( NumberFormatException e1 ) {
            JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ), SimStrings.get( "Intensity.message" ) );
            readout.setText( format.format( beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ) );
        }
    }

    private void update( double intensity ) {
        readout.setText( format.format( intensity / beam.getMaxIntensity(PhotoelectricConfig.MIN_WAVELENGTH,
                                                              PhotoelectricConfig.MAX_WAVELENGTH ) ) );
//        readout.setText( format.format( photonsPerSecond / beam.getMaxPhotonsPerSecond() ) );
    }

    void setValue( double wavelength ) {
        update( wavelength );
    }

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        update( beam.getIntensity( PhotoelectricConfig.MAX_WAVELENGTH ) );
//            update( event.getRate() );
    }
}
