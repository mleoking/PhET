/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.model.potentials.CompositePotential;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.propagators.FiniteDifferencePropagator2ndOrder;
import edu.colorado.phet.qm.model.propagators.ModifiedRichardsonPropagator;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class DiscreteModel implements ModelElement {
    private Wavefunction wavefunction;
    private CompositePotential compositePotential;

    private Propagator propagator;
    private int timeStep;
    private double deltaTime;
    private Wave wave;
    private ArrayList listeners = new ArrayList();
    private DetectorSet detectorSet;
    private boolean detectionCausesCollapse = true;
    private boolean oneShotDetectors = true;
    private Damping damping;
    private boolean paused = false;
    private WaveSetup initter;

    private HorizontalDoubleSlit doubleSlitPotential;

    private HorizontalDoubleSlit createDoubleSlit() {
        double potentialValue = Double.MAX_VALUE / 1000;
        HorizontalDoubleSlit doubleSlit = new HorizontalDoubleSlit( getGridWidth(),
                                                                    getGridHeight(),
                                                                    (int)( getGridWidth() * 0.4 ), 10, 5, 10, potentialValue );
        return doubleSlit;
    }


    public DiscreteModel( int width, int height ) {
        this( width, height, createInitDT(), createInitWave() );
    }

    public DiscreteModel( int width, int height, double deltaTime, Wave wave ) {
        this.compositePotential = new CompositePotential();
        this.deltaTime = deltaTime;
        this.wave = wave;
        wavefunction = new Wavefunction( width, height );
        detectorSet = new DetectorSet( wavefunction );
        initter = new WaveSetup( wave );
        initter.initialize( wavefunction );
        propagator = new ModifiedRichardsonPropagator( deltaTime, wave, compositePotential );
        addListener( detectorSet.getListener() );

        damping = new Damping();
        addListener( damping );

        doubleSlitPotential = createDoubleSlit();
    }

    protected static ZeroWave createInitWave() {
        return new ZeroWave();
    }

    protected static double createInitDT() {
        return 1E-5;
    }

    public void setPropagatorModifiedRichardson() {
        propagator = new ModifiedRichardsonPropagator( deltaTime, wave, compositePotential );
    }

    public void setPropagatorClassical() {
        propagator = new FiniteDifferencePropagator2ndOrder( compositePotential );
    }

    protected void step() {
        beforeTimeStep();
        getPropagator().propagate( getWavefunction() );
        incrementTimeStep();
        finishedTimeStep();
    }

    protected void beforeTimeStep() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.beforeTimeStep( this );
        }
    }


    protected void incrementTimeStep() {
        timeStep++;
    }

    protected void finishedTimeStep() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.finishedTimeStep( this );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void reset() {
        wavefunction.clear();
        detectorSet.reset();
        propagator.reset();
    }

    public int getGridWidth() {
        return wavefunction.getWidth();
    }

    public int getGridHeight() {
        return wavefunction.getHeight();
    }

    public Potential getPotential() {
        return compositePotential;
    }

    public void stepInTime( double dt ) {
        if( !paused ) {
            step();
        }
    }

    public Wavefunction getWavefunction() {
        return wavefunction;
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void fireParticle( WaveSetup waveSetup ) {
        Wavefunction particle = new Wavefunction( getGridWidth(), getGridHeight() );
        waveSetup.initialize( particle );
        wavefunction.add( particle );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleFired( this );
        }
    }

    public void setGridSpacing( int nx, int ny ) {
        if( nx != getGridWidth() || ny != getGridHeight() ) {
            wavefunction.setSize( nx, ny );
            initter.initialize( wavefunction );
            notifySizeChanged();
        }
    }

    private void notifySizeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.sizeChanged();
        }
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime( double t ) {
        this.deltaTime = t;
        propagator.setDeltaTime( deltaTime );
    }

    public void addDetector( Detector detector ) {
        detectorSet.addDetector( detector );
    }

    public void setDetectionCausesCollapse( boolean selected ) {
        this.detectionCausesCollapse = selected;
    }

    public void addPotential( Potential potential ) {
        compositePotential.addPotential( potential );
    }

    public void clearPotential() {
        compositePotential.clear();
    }

    public double getSimulationTime() {
        return propagator.getSimulationTime();
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setOneShotDetectors( boolean oneShotDetectors ) {
        this.oneShotDetectors = oneShotDetectors;
    }

    public boolean isOneShotDetectors() {
        return oneShotDetectors;
    }

    public void setPropagator( Propagator propagator ) {
        this.propagator = propagator;
    }

    public Propagator getPropagator() {
        return propagator;
    }

    public Point getCollapsePoint() {
        return detectorSet.getCollapsePoint();
    }

    public Damping getDamping() {
        return damping;
    }

    public void setPaused( boolean b ) {
        paused = b;
    }

    public void removePotential( Potential potential ) {
        this.compositePotential.removePotential( potential );
    }

    public void setAutoDetect( boolean selected ) {
        detectorSet.setAutoDetect( selected );
    }

    public void detect() {
        detectorSet.fireAllEnabledDetectors();
    }

    public void enableAllDetectors() {
        detectorSet.enableAll();
    }

    public void removeDetector( Detector detector ) {
        detectorSet.removeDetector( detector );
    }

    public void clearWavefunction() {
        wavefunction.clear();
        propagator.reset();
    }

    public void reduceWavefunctionNorm( double normDecrement ) {

        if( normDecrement != 0.0 ) {
            double magnitude = getWavefunction().getMagnitude();
            double newMagnitude = magnitude - normDecrement;
            double scale = newMagnitude <= 0.0 ? 0.0 : newMagnitude / magnitude;
            wavefunction.scale( scale );
            if( propagator instanceof FiniteDifferencePropagator2ndOrder ) {
                FiniteDifferencePropagator2ndOrder finiteDifferencePropagator2ndOrder = (FiniteDifferencePropagator2ndOrder)propagator;
                finiteDifferencePropagator2ndOrder.scale( scale );
            }
        }
    }

    public HorizontalDoubleSlit getDoubleSlitPotential() {
        return doubleSlitPotential;
    }

    public void setDoubleSlitEnabled( boolean enabled ) {
        removePotential( doubleSlitPotential );
        if( enabled ) {
            addPotential( doubleSlitPotential );
        }
        else {
            removePotential( doubleSlitPotential );
        }
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        return getWavefunction().copyRegion( 0, detectionY, width, h );
    }

    public static interface Listener {
        void finishedTimeStep( DiscreteModel model );

        void sizeChanged();

        void potentialChanged();

        void beforeTimeStep( DiscreteModel discreteModel );

        void particleFired( DiscreteModel discreteModel );
    }

    public static class Adapter implements Listener {

        public void finishedTimeStep( DiscreteModel model ) {
        }

        public void sizeChanged() {
        }

        public void potentialChanged() {
        }

        public void beforeTimeStep( DiscreteModel discreteModel ) {
        }

        public void particleFired( DiscreteModel discreteModel ) {
        }
    }

    public Wave getBoundaryCondition() {
        return wave;
    }

    public void setBoundaryCondition( Wave wave ) {
        this.wave = wave;
    }

    public boolean isDetectionCausesCollapse() {
        return detectionCausesCollapse;
    }

    public boolean containsDetector( Detector detector ) {
        return detectorSet.containsDetector( detector );
    }
}
