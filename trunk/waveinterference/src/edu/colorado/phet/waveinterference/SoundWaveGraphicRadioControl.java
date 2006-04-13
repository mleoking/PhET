/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 5:20:27 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class SoundWaveGraphicRadioControl extends HorizontalLayoutPanel {
    private SoundWaveGraphic soundWaveGraphic;
    private JRadioButton grayscale;
    private JRadioButton particles;

    public SoundWaveGraphicRadioControl( final SoundWaveGraphic soundWaveGraphic ) {
        this.soundWaveGraphic = soundWaveGraphic;
        ButtonGroup buttonGroup = new ButtonGroup();
        grayscale = new JRadioButton( "Grayscale", soundWaveGraphic.isGrayscaleVisible() );
        grayscale.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        particles = new JRadioButton( "Particles", soundWaveGraphic.isParticleVisible() );
        particles.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        buttonGroup.add( grayscale );
        buttonGroup.add( particles );
        add( grayscale );
        add( particles );
        update();
    }

    private void update() {
        soundWaveGraphic.setGrayscaleVisible( grayscale.isSelected() );
        soundWaveGraphic.setParticlesVisible( !grayscale.isSelected() );
    }
}
