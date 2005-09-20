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

import edu.colorado.phet.control.SpectrumSlider;
import edu.colorado.phet.control.SpectrumSliderKnob;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * SpectrumSliderWithReadout
 * <p>
 * A spectrum slider that adds a readout of the wavelength to the slider knob. It is implemented
 * as a decorator for a simple SpectrumSlider
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrumSliderWithReadout extends SpectrumSlider {
    private ReadoutKnob knob;

    public SpectrumSliderWithReadout( Component component, SpectrumSlider wrappedSlider ) {
        super( component );
        knob = new ReadoutKnob( wrappedSlider.getKnob() );
        setKnob( knob );
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

            wavelengthReadout = new WavelengthReadout( getComponent(), wrappedKnob );
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
                wavelengthReadout.setWidth( getWidth() );
            }
        }
    }

    /**
     * The wavelength readout
     */
    public class WavelengthReadout extends CompositePhetGraphic {
        private Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
        private Color VALUE_COLOR = Color.BLACK;

        private PhetTextGraphic valueText;
        private PhetShapeGraphic background;
        private double wavelength;
        private Rectangle2D backgroundRect;
        private SpectrumSliderKnob wrappedKnob;

        public WavelengthReadout( Component component, SpectrumSliderKnob wrappedKnob ) {
            super( component );
            this.wrappedKnob = wrappedKnob;

            backgroundRect = new Rectangle2D.Double( 0, 0, 40, 20 );
            background = new PhetShapeGraphic( component, backgroundRect, Color.lightGray, new BasicStroke( 1 ), Color.black );
            addGraphic( background );

            valueText = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( valueText );

            update( 123 );
        }

        private void update( double wavelength ) {
            this.wavelength = wavelength;
            DecimalFormat voltageFormat = new DecimalFormat( "000" );
            Object[] args = {voltageFormat.format( Math.abs( wavelength ) )};
            String text = MessageFormat.format( "nm", args );
            valueText.setText( voltageFormat.format( wavelength ) + "nm" );

            // Move the wavelength label to the positive end of the battery
            valueText.setLocation( (int)background.getBounds().getWidth(), (int)background.getBounds().getHeight() );

            // Right justify in the bckground rectangle
            valueText.setRegistrationPoint( valueText.getWidth(), 6 );
        }

        public void setWidth( int width ) {
            int inset = 10;
            backgroundRect.setRect( 0, 0, width - inset, (int)( wrappedKnob.getBounds().getHeight() * 0.67 ) - inset / 2 );
            setRegistrationPoint( (int)backgroundRect.getWidth() / 2, -(int)( wrappedKnob.getBounds().getHeight() * 0.33 ) );
            update( wavelength );
            setBoundsDirty();
            repaint();
        }

        void setValue( double wavelength ) {
            update( wavelength );
        }
    }
}
