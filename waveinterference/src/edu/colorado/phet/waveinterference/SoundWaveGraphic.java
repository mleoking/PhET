/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.PressureWaveGraphic;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 5:20:43 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class SoundWaveGraphic extends PNode {
    private PressureWaveGraphic pressureWaveGraphic;
    private WaveModelGraphic waveModelGraphic;
    private double particleSize = 0;
    private static final double MAX_PARTICLE_SIZE = 60 * 0.35;

    public SoundWaveGraphic( WaveModelGraphic waveModelGraphic, PressureWaveGraphic pressureWaveGraphic ) {
        this.waveModelGraphic = waveModelGraphic;
        this.pressureWaveGraphic = pressureWaveGraphic;
        addChild( waveModelGraphic );
        addChild( pressureWaveGraphic );
        updateView();
    }

    private void updateView() {
        if( particleSize == 0 ) {
            pressureWaveGraphic.setVisible( false );
            waveModelGraphic.setVisible( true );
        }
        else {
            int imageHeight = (int)( particleSize * MAX_PARTICLE_SIZE );
            pressureWaveGraphic.setParticleImageSize( Math.max( 1, imageHeight ) );
            pressureWaveGraphic.setVisible( true );
            waveModelGraphic.setVisible( false );
        }
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waveModelGraphic.getLatticeScreenCoordinates();
    }

    public void update() {
        waveModelGraphic.update();
        pressureWaveGraphic.update();
    }

    public double getParticleSize() {
        return particleSize;
    }

    public void setParticleSize( double size ) {
        this.particleSize = size;
        updateView();
    }
}
