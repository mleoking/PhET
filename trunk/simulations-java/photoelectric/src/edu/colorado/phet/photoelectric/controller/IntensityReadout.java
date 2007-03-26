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
import edu.colorado.phet.photoelectric.model.util.PhotoelectricModelUtil;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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
public class IntensityReadout extends GraphicLayerSet implements Beam.RateChangeListener, ChangeListener {

    private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
    private DecimalFormat format = new DecimalFormat( "#0%" );

    private JTextField readout;
    private PhetGraphic readoutGraphic;
    private Beam beam;
    private BeamControl.Mode mode;

    public IntensityReadout( final Component component, final Beam beam ) {
        super( component );

        beam.addRateChangeListener( this );
        this.beam = beam;

        readout = new JTextField( 4 );
        readout.setHorizontalAlignment( JTextField.RIGHT );
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

    private void update( final Component component, final Beam beam ) {
        double photonsPerSecond = 0;
        try {
            String text = readout.getText().toLowerCase();
            int nmLoc = text.indexOf( "%" );
            text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
            double percent = MathUtil.clamp( 0, Double.parseDouble( text ), 100 );
            photonsPerSecond = percent / 100 * this.beam.getMaxPhotonsPerSecond();
            this.beam.setPhotonsPerSecond( photonsPerSecond );
        }
        catch( NumberFormatException e1 ) {
            JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ), SimStrings.getInstance().getString( "Intensity.message" ) );
            readout.setText( format.format( beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ) );
        }
    }

    private void update( double photonsPerSecond ) {
        double value = beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond();

        // If the beam control is in INTENSITY mode, we need to make the readout value
        // reflect that
        if( mode == BeamControl.INTENSITY ) {
            value = PhotoelectricModelUtil.photonRateToIntensity( value, beam.getWavelength() );
        }
        readout.setText( format.format( value ));
    }

    void setValue( double wavelength ) {
        update( wavelength );
    }

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
            update( event.getRate() );
    }

    public void stateChanged( ChangeEvent e ) {
        if( e.getSource() instanceof BeamControl ) {
            mode = ((BeamControl)e.getSource()).getMode();
        }
    }
}
