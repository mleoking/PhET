// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.VerticalLayoutPanelWithDisable;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 5:20:27 PM
 */

public class SoundWaveGraphicRadioControl extends VerticalLayoutPanelWithDisable {
    private SoundWaveGraphic soundWaveGraphic;
    private JRadioButton grayscale;
    private JRadioButton particles;

    public SoundWaveGraphicRadioControl( final SoundWaveGraphic soundWaveGraphic ) {
        this.soundWaveGraphic = soundWaveGraphic;
        ButtonGroup buttonGroup = new ButtonGroup();
        grayscale = new JRadioButton( WIStrings.getString( "sound.grayscale" ), soundWaveGraphic.isGrayscaleVisible() );
        grayscale.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        particles = new JRadioButton( WIStrings.getString( "sound.particles" ), soundWaveGraphic.isParticleVisible() );
        particles.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        buttonGroup.add( grayscale );
        buttonGroup.add( particles );
        add( grayscale );
        add( particles );

        soundWaveGraphic.addListener( new SoundWaveGraphic.Listener() {
            public void viewChanged() {
            }

            public void viewTypeChanged() {
                grayscale.setSelected( soundWaveGraphic.isGrayscaleVisible() );
                particles.setSelected( soundWaveGraphic.isParticleVisible() );
            }
        } );

        update();
    }

    private void update() {
        boolean showGray = grayscale.isSelected();
        boolean showPart = particles.isSelected();
        soundWaveGraphic.setGrayscaleVisible( showGray );
        soundWaveGraphic.setParticlesVisible( showPart );
    }
}
