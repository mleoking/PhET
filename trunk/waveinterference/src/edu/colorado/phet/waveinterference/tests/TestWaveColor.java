/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.ColorMap;
import edu.colorado.phet.waveinterference.view.PhotonEmissionColorMap;
import edu.colorado.phet.waveinterference.view.SRRWavelengthSliderComponent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestWaveColor extends TestTopView {
    private SRRWavelengthSliderComponent slider;

    public TestWaveColor() {
        this( "Wave Color" );
    }

    public TestWaveColor( String name ) {
        super( name );
        slider = new SRRWavelengthSliderComponent();
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateColor();
            }
        } );
        getControlPanel().addControl( slider );
        updateColor();
        getOscillator().setAmplitude( 2.5 );
    }

    protected void updateColor() {
        getWaveModel().clear();
        PhotonEmissionColorMap colorMap = new PhotonEmissionColorMap( getWaveModel(), slider.getColor() );
        setColorMap( colorMap );
    }

    protected void setColorMap( ColorMap colorMap ) {
        getWaveModelGraphic().setColorMap( colorMap );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestWaveColor() );
    }
}
