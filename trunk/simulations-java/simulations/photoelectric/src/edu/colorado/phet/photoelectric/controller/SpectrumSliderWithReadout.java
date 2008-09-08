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

import edu.colorado.phet.common.controls.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.quantum.model.Beam;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

/**
 * SpectrumSliderWithReadout
 * <p/>
 * A spectrum slider that adds a readout of the wavelength to the slider knob. It is implemented
 * as a decorator for a simple SpectrumSlider.
 * <p/>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrumSliderWithReadout extends SpectrumSliderWithSquareCursor {
    private Beam beam;
    private WavelengthReadout readout;

    public SpectrumSliderWithReadout( Component component,
                                      final SpectrumSliderWithSquareCursor wrappedSliderWithSquareCursor,
                                      Beam beam,
                                      double minimumWavelength,
                                      double maximumWavelength,
                                      Point location ) {
        super( component, minimumWavelength, maximumWavelength );
        this.beam = beam;
        beam.addWavelengthChangeListener( new WavelengthChangeListener() );
        readout = new WavelengthReadout( component, location, minimumWavelength, maximumWavelength );

        // We have to add the readout directly to the apparatus panel, otherwise we can't
        // get it to respond like a JComponent, and type into it
        ( (ApparatusPanel)component ).addGraphic( readout, 1E14 );
        setKnob( wrappedSliderWithSquareCursor.getKnob() );

        // Add a listener that will move the readout along with the knob
        addChangeListener( readout );
    }

    public void setValue( int value ) {
        super.setValue( value );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A listener to update the slider if wavelength changes
     */
    public class WavelengthChangeListener implements Beam.WavelengthChangeListener {
        public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
            if( (int)event.getWavelength() != getValue() ) {
                SpectrumSliderWithReadout.this.setValue( (int)event.getWavelength() );
            }
        }
    }

    /**
     * The wavelength readout
     */
    public class WavelengthReadout extends GraphicLayerSet implements ChangeListener {
        private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );

        private JTextField readout;
        private PhetGraphic readoutGraphic;
        private Point baseLocation;
        private double minWavelength;
        private double maxWavelength;

        public WavelengthReadout( final Component component, Point baseLocation, double minWavelength, double maxWavelength ) {
            super( component );
            this.baseLocation = baseLocation;
            this.minWavelength = minWavelength;
            this.maxWavelength = maxWavelength;
            readout = new JTextField( 5 );
            readout.setHorizontalAlignment( JTextField.RIGHT );
//            readout.setHorizontalAlignment( JTextField.CENTER );
            readout.setFont( VALUE_FONT );
            readout.addFocusListener( new FocusListener() {
                public void focusGained( FocusEvent e ) {
                    // noop
                }

                public void focusLost( FocusEvent e ) {
                    update( component );
                }
            } );
            readout.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    update( component );
                }
            } );
            readoutGraphic = PhetJComponent.newInstance( component, readout );
            addGraphic( readoutGraphic, 1E9 );

            update( beam.getWavelength() ); // dummy value
        }

        private void update( final Component component ) {
            double wavelength = 0;
            try {
                String text = readout.getText().toLowerCase();
                int nmLoc = text.indexOf( "nm" );
                text = nmLoc >= 0 ? readout.getText().substring( 0, nmLoc ) : text;
                wavelength = MathUtil.clamp( minWavelength,
                                             Double.parseDouble( text ),
                                             maxWavelength );
                beam.setWavelength( wavelength );
                update( wavelength );
            }
            catch( NumberFormatException e1 ) {
                JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ),
                                               SimStrings.getInstance().getString( "Wavelength.message" ) );
                setText( beam.getWavelength() );
            }
        }

        private void update( double wavelength ) {
            // Move to the right spot. The -15 is a total hack. I can't understand why I need it.
            int x = (int)( baseLocation.x + getKnob().getLocation().getX() - getBounds().getWidth() / 2 - 15 );
            int y = (int)( baseLocation.y - getHeight() - 5 );
            setLocation( x, y );
            // Update the text
            setText( wavelength );
        }

        private void setText( double wavelength ) {
            DecimalFormat voltageFormat = new DecimalFormat( "000" );
            readout.setText( voltageFormat.format( wavelength ) + " "+SimStrings.getInstance().getString( "units.nm" ) );
        }

        void setValue( double wavelength ) {
            update( wavelength );
        }

        public void stateChanged( ChangeEvent e ) {
            if( e.getSource() == SpectrumSliderWithReadout.this ) {
                update( getValue() );
            }
        }
    }
}
