/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.controls.SRRWavelengthSlider;
import edu.umd.cs.piccolo.PCanvas;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 11:00:29 PM
 * Copyright (c) Jan 22, 2006 by Sam Reid
 */

public class SRRWavelengthSliderComponent extends PCanvas {
    private SRRWavelengthSlider wavelengthSliderGraphic;

    public SRRWavelengthSliderComponent( SRRWavelengthSlider wavelengthSliderGraphic ) {
        this.wavelengthSliderGraphic = wavelengthSliderGraphic;
        getLayer().addChild( wavelengthSliderGraphic );
        setBounds( 0, 0, 400, 400 );
        setPreferredSize( new Dimension( (int)wavelengthSliderGraphic.getFullBounds().getWidth(), (int)wavelengthSliderGraphic.getFullBounds().getHeight() ) );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        setOpaque( false );
        setBackground( new Color( 0, 0, 0, 0 ) );
    }

    public void setOpaque( boolean isOpaque ) {
        super.setOpaque( isOpaque );
        wavelengthSliderGraphic.setOpaque( isOpaque );
    }
}
