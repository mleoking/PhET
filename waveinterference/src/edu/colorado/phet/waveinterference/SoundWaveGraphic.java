/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.PressureWaveGraphic;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

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
    //this is a trial for a few interviews to set particle mode default on initialization.
//    private boolean grayscaleVisible = false;
//    private boolean particlesVisible = true;

    public SoundWaveGraphic( WaveModelGraphic waveModelGraphic, PressureWaveGraphic pressureWaveGraphic ) {
        this.waveModelGraphic = waveModelGraphic;
        this.pressureWaveGraphic = pressureWaveGraphic;
        addChild( waveModelGraphic );
        addChild( pressureWaveGraphic );
        updateView();
    }

    private ArrayList listeners = new ArrayList();

    public void reset() {
        setParticlesVisible( false );
        setGrayscaleVisible( true );
        notifyViewChanged();
        pressureWaveGraphic.reset();
    }

    public PressureWaveGraphic getPressureWaveGraphic() {
        return pressureWaveGraphic;
    }

    public static interface Listener {
        void viewChanged();

        void viewTypeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifyViewChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.viewChanged();
        }
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
        notifyViewChanged();
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
        notifyViewChanged();
        notifyViewTypeChanged();
    }

    public boolean isGrayscaleVisible() {
        return grayscaleVisible;
    }

    public void setParticlesVisible( boolean particlesVisible ) {
        this.particlesVisible = particlesVisible;
        updateView();
        notifyViewChanged();
        notifyViewTypeChanged();
    }

    private void notifyViewTypeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.viewTypeChanged();
        }
    }

    public boolean isParticleVisible() {
        return particlesVisible;
    }

    public WaveModelGraphic getWaveModelGraphic() {
        return waveModelGraphic;
    }
}
