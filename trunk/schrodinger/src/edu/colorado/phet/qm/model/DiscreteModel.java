/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.model.potentials.CompositePotential;
import edu.colorado.phet.qm.model.potentials.ConstantPotential;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;
import edu.colorado.phet.qm.model.propagators.ModifiedRichardsonPropagator;
import edu.colorado.phet.qm.model.waves.ZeroWave;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class DiscreteModel implements ModelElement {
    private Wavefunction sourceWave;
    private Propagator sourcePropagator;

    private CompositePotential compositePotential;

    private Wavefunction wavefunction;
    private Propagator propagator;

    private int timeStep;
    private double deltaTime;
    private Wave wave;
    private ArrayList listeners = new ArrayList();
    private DetectorSet detectorSet;
    private boolean detectionCausesCollapse = true;
    private boolean oneShotDetectors = false;
    private Damping damping;
    private boolean paused = false;
    private WaveSetup initter;

    private HorizontalDoubleSlit doubleSlitPotential;
    private MeasurementScale measurementScale;
    private boolean doubleSlitEnabled;

    public static final int DEFAULT_WIDTH = 100;
    private boolean slitAbsorptive = true;
    public final double DEFAULT_POTENTIAL_BARRIER_VALUE = Double.MAX_VALUE / 1000;

    public DiscreteModel() {
        this( DEFAULT_WIDTH, DEFAULT_WIDTH );
    }

    public DiscreteModel( int width, int height ) {
        this( width, height, createInitDT(), createInitWave() );
    }

    public DiscreteModel( int width, int height, double deltaTime, Wave wave ) {
        this.compositePotential = new CompositePotential();
        this.deltaTime = deltaTime;
        this.wave = wave;
        wavefunction = new Wavefunction( width, height );
        sourceWave = new Wavefunction( width, height );
        detectorSet = new DetectorSet( wavefunction );
        detectorSet.setOneShotDetectors( oneShotDetectors );
        initter = new WaveSetup( wave );
        initter.initialize( wavefunction );
        propagator = new ModifiedRichardsonPropagator( this, deltaTime, wave, compositePotential );
        sourcePropagator = new ModifiedRichardsonPropagator( this, deltaTime, wave, compositePotential );
        addListener( detectorSet.getListener() );

        damping = new Damping();
        doubleSlitPotential = createDoubleSlit();
        measurementScale = new MeasurementScale( getGridWidth(), 1.0 );

//        final WaveDebugger sourceWaveDebugger = new WaveDebugger( "Source wave", sourceWave );
//        sourceWaveDebugger.setVisible( true );
//        addListener( new Adapter() {
//            public void finishedTimeStep( DiscreteModel model ) {
//                sourceWaveDebugger.update();
//            }
//        } );
//        final WaveDebugger waveDebugger = new WaveDebugger( "Main wave", wavefunction );
//        waveDebugger.setVisible( true );
//        addListener( new Adapter() {
//            public void finishedTimeStep( DiscreteModel model ) {
//                waveDebugger.update();
//            }
//        } );
    }

    public Propagator getSourcePropagator() {
        return sourcePropagator;
    }

    public Wavefunction getSourceWave() {
        return sourceWave;
    }

    public MeasurementScale getMeasurementScale() {
        return measurementScale;
    }

    protected static ZeroWave createInitWave() {
        return new ZeroWave();
    }

    protected static double createInitDT() {
        return 1E-5;
    }

    public void setPropagatorModifiedRichardson() {
        setPropagator( new ModifiedRichardsonPropagator( this, deltaTime, wave, compositePotential ) );
    }

    public void setPropagatorClassical() {
        setPropagator( new ClassicalWavePropagator( this, compositePotential ) );
    }

    protected void step() {
        beforeTimeStep();
        getPropagationStrategy().step();
        incrementTimeStep();
        finishedTimeStep();
    }

    public void setSlitAbsorptive( boolean slitAbsorptive ) {
        this.slitAbsorptive = slitAbsorptive;
    }

    public boolean isSlitAbsorptive() {
        return slitAbsorptive;
    }

    public boolean isAutoDetect() {
        return detectorSet.isAutoDetect();
    }

    class ReflectivePropagate implements PropagationStrategy {
        public void step() {
            if( getWavefunction().getMagnitude() > 0 ) {
                getPropagator().propagate( getWavefunction() );
                getWavefunction().setMagnitudeDirty();
                damping.damp( wavefunction );
            }
        }
    }

    class AbsorptivePropagate implements PropagationStrategy {
        public void step() {
            getWavefunction().setMagnitudeDirty();
            if( getWavefunction().getMagnitude() > 0 ) {
                sourcePropagator.propagate( sourceWave );
                copySourceToActualSouthArea();
                getPropagator().propagate( getWavefunction() );

                getWavefunction().setMagnitudeDirty();
                damping.damp( wavefunction );
                damping.damp( sourceWave );
            }
        }
    }

    interface PropagationStrategy {
        void step();
    }

    private PropagationStrategy getPropagationStrategy() {
        if( slitAbsorptive ) {
            return new AbsorptivePropagate();
        }
        else {
            return new ReflectivePropagate();
        }
    }

    private void copySourceToActualSouthArea() {
        int maxy = getDoubleSlitPotential().getY() + getDoubleSlitPotential().getHeight();
        for( int y = maxy; y < sourceWave.getHeight(); y++ ) {
            for( int x = 0; x < sourceWave.getWidth(); x++ ) {
                copySourceToActual( x, y );
            }
        }
    }

    private void copySourceToActual( int x, int y ) {
        if( getPropagator() instanceof ClassicalWavePropagator && sourcePropagator instanceof ClassicalWavePropagator )
        {
            ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)getPropagator();
            ClassicalWavePropagator classicalSource = (ClassicalWavePropagator)sourcePropagator;
            if( classicalWavePropagator.getLast() != null && classicalSource.getLast() != null ) {
                classicalWavePropagator.getLast().setValue( x, y, classicalSource.getLast().valueAt( x, y ) );
            }
            if( classicalWavePropagator.getLast2() != null && classicalSource.getLast2() != null ) {
                classicalWavePropagator.getLast2().setValue( x, y, classicalSource.getLast2().valueAt( x, y ) );
            }
        }
        wavefunction.setValue( x, y, sourceWave.valueAt( x, y ) );
    }

    private HorizontalDoubleSlit createDoubleSlit() {
        return new HorizontalDoubleSlit( getGridWidth(),
                                         getGridHeight(),
                                         (int)( getGridHeight() * 0.4 ), 3, (int)( 8 * getGridWidth() / 100.0 ), (int)( 13 * getGridWidth() / 100.0 ), DEFAULT_POTENTIAL_BARRIER_VALUE );
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
        sourceWave.clear();
        wavefunction.clear();
        detectorSet.reset();
        propagator.reset();
        sourcePropagator.reset();
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
        sourceWave.add( particle );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleFired( this );
        }
    }

    public void setGridSpacing( int nx, int ny ) {
        if( nx != getGridWidth() || ny != getGridHeight() ) {
            wavefunction.setSize( nx, ny );
            sourceWave.setSize( nx, ny );
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
        sourcePropagator.setDeltaTime( deltaTime );
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
        boolean doubleSlitEnabled = isDoubleSlitEnabled();
        compositePotential.clear();
        setDoubleSlitEnabled( false );
        if( isDoubleSlitEnabled() != doubleSlitEnabled ) {
            notifySlitStateChanged();
        }
    }

    public double getSimulationTime() {
        return propagator.getSimulationTime();
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setOneShotDetectors( boolean oneShotDetectors ) {
        this.oneShotDetectors = oneShotDetectors;
        detectorSet.setOneShotDetectors( oneShotDetectors );
        enableAllDetectors();
    }

    public boolean isOneShotDetectors() {
        return oneShotDetectors;
    }

    public void setPropagator( Propagator propagator ) {
        this.propagator = propagator;
        sourcePropagator = propagator.copy();
        sourcePropagator.setPotential( new ConstantPotential( 0 ) );
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
        sourceWave.clear();
        propagator.reset();
        sourcePropagator.reset();
    }

    public void reduceWavefunctionNorm( double normDecrement ) {

        if( normDecrement != 0.0 ) {
            double magnitude = getWavefunction().getMagnitude();
            double newMagnitude = magnitude - normDecrement;
            double scale = newMagnitude <= 0.0 ? 0.0 : newMagnitude / magnitude;
            wavefunction.scale( scale );
            sourceWave.scale( scale );
            if( propagator instanceof ClassicalWavePropagator ) {
                ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)propagator;
                classicalWavePropagator.scale( scale );
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
        this.doubleSlitEnabled = enabled;
        notifySlitStateChanged();
    }

    private void notifySlitStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.doubleSlitVisibilityChanged();
        }
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        return getWavefunction().copyRegion( 0, detectionY, width, h );
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
        getWavefunction().setValue( i, k, value );
        propagator.setBoundaryCondition( i, k, value );
        sourcePropagator.setBoundaryCondition( i, k, value );
    }

    public boolean containsListener( Listener listener ) {
        return listeners.contains( listener );
    }

    public DetectorSet getDetectorSet() {
        return detectorSet;
    }

    public void normalizeWavefunction() {
        wavefunction.normalize();
        sourceWave.normalize();
        getPropagator().normalize();
    }

    public void setWavefunctionNorm( double norm ) {
        wavefunction.setMagnitude( norm );
        sourceWave.setMagnitude( norm );
        getPropagator().setWavefunctionNorm( norm );
    }

    public void setWaveSize( int width, int height ) {
        setGridSpacing( width, height );
        doubleSlitPotential.reset( getGridWidth(), getGridHeight(),
                                   (int)( getGridHeight() * 0.4 ), 3,
                                   8, 14,
                                   DEFAULT_POTENTIAL_BARRIER_VALUE );
        clearWavefunction();
    }

    public static interface Listener {
        void finishedTimeStep( DiscreteModel model );

        void sizeChanged();

        void potentialChanged();

        void beforeTimeStep( DiscreteModel discreteModel );

        void particleFired( DiscreteModel discreteModel );

        void doubleSlitVisibilityChanged();
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

        public void doubleSlitVisibilityChanged() {
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

    public boolean isDoubleSlitEnabled() {
        return doubleSlitEnabled;
    }
}
