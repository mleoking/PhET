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
    private double particleSize = 1 * 0.75;
    private static final double MAX_PARTICLE_SIZE = 60 * 0.35;
    private boolean grayscaleVisible = true;
    private boolean particlesVisible = false;

    public SoundWaveGraphic( WaveModelGraphic waveModelGraphic, PressureWaveGraphic pressureWaveGraphic ) {
        this.waveModelGraphic = waveModelGraphic;
        this.pressureWaveGraphic = pressureWaveGraphic;
        addChild( waveModelGraphic );
        addChild( pressureWaveGraphic );
        updateView();
    }

    private void updateView() {
        pressureWaveGraphic.setVisible( particlesVisible );
        waveModelGraphic.setVisible( grayscaleVisible );
        int imageHeight = (int)( particleSize * MAX_PARTICLE_SIZE );
        pressureWaveGraphic.setParticleImageSize( Math.max( 1, imageHeight ) );
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

    public void setGrayscaleVisible( boolean grayscaleVisible ) {
        this.grayscaleVisible = grayscaleVisible;
        updateView();
    }

    public boolean isGrayscaleVisible() {
        return grayscaleVisible;
    }

    public void setParticlesVisible( boolean particlesVisible ) {
        this.particlesVisible = particlesVisible;
        updateView();
    }

    public boolean isParticleVisible() {
        return particlesVisible;
    }

    public WaveModelGraphic getWaveModelGraphic() {
        return waveModelGraphic;
    }
}
