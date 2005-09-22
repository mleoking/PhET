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

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.control.SpectrumSlider;
import edu.colorado.phet.control.SpectrumSliderKnob;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * SpectrumSliderWithReadout
 * <p/>
 * A spectrum slider that adds a readout of the wavelength to the slider knob. It is implemented
 * as a decorator for a simple SpectrumSlider
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrumSliderWithReadout extends SpectrumSlider {
    private ReadoutKnob knob;
    private CollimatedBeam beam;
    private WavelengthReadout readout;

    public SpectrumSliderWithReadout( Component component,
                                      final SpectrumSlider wrappedSlider,
                                      CollimatedBeam beam,
                                      double minimumWavelength,
                                      double maximumWavelength ) {
        super( component, minimumWavelength, maximumWavelength );
        this.beam = beam;
        readout = new WavelengthReadout( component, wrappedSlider.getKnob() );
        ( (ApparatusPanel)component ).addGraphic( readout, 1E9 );
//        knob = new ReadoutKnob( wrappedSlider.getKnob() );
        setKnob( wrappedSlider.getKnob() );

        // Add a listener that will move the readout along with the knob, and
        addChangeListener( readout );
    }

    /*
     * Updates the knob based on the current location and value.
     * This method is shared by setter methods.
     */
    protected void updateKnob() {
        super.updateKnob();
        // Set the readout on the knob
        if( knob != null ) {
            knob.setWavelength( getValue() );
            repaint();
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A slider knob with a wavelength readout
     */
    private class ReadoutKnob extends SpectrumSliderKnob {
        private WavelengthReadout wavelengthReadout;

        public ReadoutKnob( SpectrumSliderKnob wrappedKnob ) {
            super( wrappedKnob.getComponent(),
                   wrappedKnob.getSize(),
                   wrappedKnob.getAngle() );

            wavelengthReadout = new WavelengthReadout( getComponent(), this );
//            wavelengthReadout = new WavelengthReadout( getComponent(), knob );
            addGraphic( wavelengthReadout );
        }

        public void setSize( Dimension size ) {
            super.setSize( size );
            updateShape();
        }

        public void setWavelength( double wavelength ) {
            wavelengthReadout.setValue( wavelength );
        }

        /*
         * Updates the knob's shape, based on its size and angle.
         */
        protected void updateShape() {
            super.updateShape();
            if( wavelengthReadout != null ) {
//                wavelengthReadout.setWidth( getWidth() );
            }
        }
    }

    /**
     * The wavelength readout
     */
    public class WavelengthReadout extends GraphicLayerSet implements ChangeListener {
        private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );

        private JTextField readout;
        private double wavelength;
        private SpectrumSliderKnob knob;
        private PhetGraphic readoutGraphic;

        public WavelengthReadout( final Component component, SpectrumSliderKnob knob ) {
            super( component );
            this.knob = knob;
            readout = new JTextField( 5 );
            readout.setHorizontalAlignment( JTextField.CENTER );
            readout.setFont( VALUE_FONT );
            readout.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double wavelength = 0;
                    try {
                        String text = readout.getText().toLowerCase();
                        int nmLoc = text.indexOf( "nm" );
                        text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
                        wavelength = Double.parseDouble( text );
                        beam.setWavelength( wavelength );
                        update( wavelength );
                    }
                    catch( NumberFormatException e1 ) {
                        JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ), "Wavelength must be numeric, or a number followed by \"nm\"" );
                    }
                }
            } );
            readoutGraphic = PhetJComponent.newInstance( component, readout );
            addGraphic( readoutGraphic, 1E9 );

            update( 123 );
        }

        private void update( double wavelength ) {
            this.wavelength = wavelength;
            DecimalFormat voltageFormat = new DecimalFormat( "000" );
            readout.setText( voltageFormat.format( wavelength ) + " nm" );
//            Object[] args = {voltageFormat.format( Math.abs( wavelength ) )};
//            String text = MessageFormat.format( "nm", args );
//            readout.setText( text );
        }

        void setValue( double wavelength ) {
            update( wavelength );
        }

        public void stateChanged( ChangeEvent e ) {
            if( e.getSource() == SpectrumSliderWithReadout.this ) {
                setLocation( (int)SpectrumSliderWithReadout.this.getKnob().getLocation().getX() - getWidth() / 2,
                             (int)SpectrumSliderWithReadout.this.getLocation().getY() - getHeight() );
                update( getValue() );
            }
        }
    }
}
