/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.PCanvas;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 11:00:29 PM
 */

public class SRRWavelengthSliderComponent extends PCanvas {
    private SRRWavelengthSlider wavelengthSliderGraphic;

    public SRRWavelengthSliderComponent() {
        this( new SRRWavelengthSlider() );
    }

    public SRRWavelengthSliderComponent( SRRWavelengthSlider wavelengthSliderGraphic ) {
        this.wavelengthSliderGraphic = wavelengthSliderGraphic;
        getLayer().addChild( wavelengthSliderGraphic );
        setBounds( 0, 0, 400, 400 );
        setPreferredSize( new Dimension( (int) wavelengthSliderGraphic.getFullBounds().getWidth(), (int) wavelengthSliderGraphic.getFullBounds().getHeight() ) );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        setOpaque( false );
        setBackground( new Color( 0, 0, 0, 0 ) );
    }

    public void setOpaque( boolean isOpaque ) {
        super.setOpaque( isOpaque );
        if ( wavelengthSliderGraphic != null ) {
            wavelengthSliderGraphic.setOpaque( isOpaque );
        }
    }

    public void addChangeListener( ChangeListener changeListener ) {
        wavelengthSliderGraphic.addChangeListener( changeListener );
    }

    public Color getColor() {
        return wavelengthSliderGraphic.getVisibleColor();
    }

    public double getWavelength() {
        return wavelengthSliderGraphic.getWavelength();
    }

    public void updateSelectedWavelength( double wavelength ) {
        wavelengthSliderGraphic.setSelectedWavelength( wavelength );
        paintImmediately( 0, 0, getWidth(), getHeight() );
    }
}
