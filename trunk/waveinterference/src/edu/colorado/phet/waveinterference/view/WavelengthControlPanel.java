/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 8:11:07 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class WavelengthControlPanel extends VerticalLayoutPanel {
    private SRRWavelengthSliderComponent slider;
    private WaveModelGraphic waveModelGraphic;
    private Oscillator oscillator;
    private double speedOfLight = 1000.0;

    public WavelengthControlPanel( WaveModelGraphic waveModelGraphic, Oscillator oscillator ) {
        addFullWidth( new JLabel( "Wavelength" ) );
        this.waveModelGraphic = waveModelGraphic;
        this.oscillator = oscillator;
        slider = new SRRWavelengthSliderComponent();

        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        add( slider );
        update();
    }

    protected void update() {
        getWaveModel().clear();
        PhotonEmissionColorMap colorMap = new PhotonEmissionColorMap( getWaveModel(), getColor() );
        waveModelGraphic.setColorMap( colorMap );
        double frequency = speedOfLight / slider.getWavelength();
        System.out.println( "slider.getWavelength() = " + slider.getWavelength() );
//        System.out.println( "frequency = " + frequency );
        oscillator.setFrequency( frequency );
    }

    private WaveModel getWaveModel() {
        return waveModelGraphic.getWaveModel();
    }

    public Color getColor() {
        return slider.getColor();
    }

    public void addChangeListener( ChangeListener changeListener ) {
        slider.addChangeListener( changeListener );
    }
}
