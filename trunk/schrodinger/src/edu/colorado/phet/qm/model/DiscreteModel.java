/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.model.potentials.CompositePotential;
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
    private WaveModel waveModel;
    private CompositePotential compositePotential;

    private WaveModel sourceWaveModel;
    private CompositePotential sourcePotential;

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
    protected final boolean DEBUG_WAVES = true;

    public DiscreteModel() {
        this( DEFAULT_WIDTH, DEFAULT_WIDTH );
    }

    public DiscreteModel( int width, int height ) {
        this( width, height, createInitDT(), createInitWave() );
    }

    public DiscreteModel( int width, int height, double deltaTime, Wave wave ) {
        this.compositePotential = new CompositePotential();
        this.sourcePotential = new CompositePotential();
        this.deltaTime = deltaTime;
        this.wave = wave;
        this.waveModel = new WaveModel( new Wavefunction( width, height ), new ModifiedRichardsonPropagator( this, deltaTime, wave, compositePotential ) );

        detectorSet = new DetectorSet( getWavefunction() );
        detectorSet.setOneShotDetectors( oneShotDetectors );
        initter = new WaveSetup( wave );
        initter.initialize( getWavefunction() );

        sourceWaveModel = new WaveModel( new Wavefunction( width, height ), new ModifiedRichardsonPropagator( this, deltaTime, wave, sourcePotential ) );
        addListener( detectorSet.getListener() );

        damping = new Damping();
        doubleSlitPotential = createDoubleSlit();
        measurementScale = new MeasurementScale( getGridWidth(), 1.0 );

        if( DEBUG_WAVES ) {
            final WaveDebugger sourceWaveDebugger = new WaveDebugger( "Source wave", getSourceWave() );
            sourceWaveDebugger.setVisible( true );
            addListener( new Adapter() {
                public void finishedTimeStep( DiscreteModel model ) {
                    sourceWaveDebugger.update();
                }
            } );
            final WaveDebugger waveDebugger = new WaveDebugger( "Main wave", getWavefunction() );
            waveDebugger.setVisible( true );
            addListener( new Adapter() {
                public void finishedTimeStep( DiscreteModel model ) {
                    waveDebugger.update();
                }
            } );
        }
    }

    public Propagator getSourcePropagator() {
        return sourceWaveModel.getPropagator();
    }

    public Wavefunction getSourceWave() {
        return sourceWaveModel.getWavefunction();
    }

    public WaveModel getSourceWaveModel() {
        return sourceWaveModel;
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

    public void clearAllWaves() {
        sourceWaveModel.clear();
        waveModel.clear();
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public void updateWavefunctionAfterDetection() {
        clearAllWaves();
    }

    class DefaultPropagate implements PropagationStrategy {
        public void step() {
            defaultPropagate();
        }
    }

    class AbsorptivePropagate implements PropagationStrategy {
        public void step() {
            absorptivePropagate();
        }
    }

    protected void defaultPropagate() {
        if( getWavefunction().getMagnitude() > 0 ) {
            getWaveModel().propagate();
            damping.damp( getWavefunction() );
        }
    }

    protected void absorptivePropagate() {
        getWavefunction().setMagnitudeDirty();
        if( getWavefunction().getMagnitude() > 0 ) {
            getSourceWaveModel().propagate();
            copySourceToActualSouthArea();
            getWaveModel().propagate();
            damping.damp( getWavefunction() );
            damping.damp( getSourceWave() );
        }
    }

    interface PropagationStrategy {
        void step();
    }

    protected PropagationStrategy getPropagationStrategy() {
        if( slitAbsorptive ) {
            return new AbsorptivePropagate();
        }
        else {
            return new DefaultPropagate();
        }
    }


    public void copyActualToSource() {
        for( int y = 0; y < getSourceWave().getHeight(); y++ ) {
            for( int x = 0; x < getSourceWave().getWidth(); x++ ) {
                copyActualToSource( x, y );
            }
        }
    }

    //todo refactor using WaveModel
    private void copyActualToSource( int x, int y ) {
        if( getPropagator() instanceof ClassicalWavePropagator && getSourcePropagator()instanceof ClassicalWavePropagator )
        {
            ClassicalWavePropagator actualPropagator = (ClassicalWavePropagator)getPropagator();
            ClassicalWavePropagator sourcePropagator = (ClassicalWavePropagator)getSourcePropagator();
            if( actualPropagator.getLast() != null && sourcePropagator.getLast() != null ) {
                sourcePropagator.getLast().setValue( x, y, actualPropagator.getLast().valueAt( x, y ) );
            }
            if( actualPropagator.getLast2() != null && sourcePropagator.getLast2() != null ) {
                sourcePropagator.getLast2().setValue( x, y, actualPropagator.getLast2().valueAt( x, y ) );
            }
        }
        getSourceWave().setValue( x, y, getWavefunction().valueAt( x, y ) );
    }

    protected void copySourceToActualSouthArea() {
        int maxy = getDoubleSlitPotential().getY() + getDoubleSlitPotential().getHeight();
        for( int y = maxy; y < getSourceWave().getHeight(); y++ ) {
            for( int x = 0; x < getSourceWave().getWidth(); x++ ) {
                copySourceToActual( x, y );
            }
        }
    }

    private void copySourceToActual( int x, int y ) {
        if( getPropagator() instanceof ClassicalWavePropagator && getSourcePropagator()instanceof ClassicalWavePropagator )
        {
            ClassicalWavePropagator actualPropagator = (ClassicalWavePropagator)getPropagator();
            ClassicalWavePropagator sourcePropagator = (ClassicalWavePropagator)getSourcePropagator();
            if( actualPropagator.getLast() != null && sourcePropagator.getLast() != null ) {
                actualPropagator.getLast().setValue( x, y, sourcePropagator.getLast().valueAt( x, y ) );
            }
            if( actualPropagator.getLast2() != null && sourcePropagator.getLast2() != null ) {
                actualPropagator.getLast2().setValue( x, y, sourcePropagator.getLast2().valueAt( x, y ) );
            }
        }
        getWavefunction().setValue( x, y, getSourceWave().valueAt( x, y ) );
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
        getSourceWave().clear();
        getWavefunction().clear();
        detectorSet.reset();
        getPropagator().reset();
        getSourcePropagator().reset();
    }

    public int getGridWidth() {
        return getWavefunction().getWidth();
    }

    public int getGridHeight() {
        return getWavefunction().getHeight();
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
        return waveModel.getWavefunction();
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void fireParticle( WaveSetup waveSetup ) {
        Wavefunction particle = new Wavefunction( getGridWidth(), getGridHeight() );
        waveSetup.initialize( particle );
        getWavefunction().add( particle );
        getSourceWave().add( particle );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleFired( this );
        }
    }

    public void setGridSpacing( int nx, int ny ) {
        if( nx != getGridWidth() || ny != getGridHeight() ) {
            getWavefunction().setSize( nx, ny );
            getSourceWave().setSize( nx, ny );
            initter.initialize( getWavefunction() );
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
        getPropagator().setDeltaTime( deltaTime );
        getSourcePropagator().setDeltaTime( deltaTime );
    }

    public void addDetector( Detector detector ) {
        detectorSet.addDetector( detector );
    }

    public void setDetectionCausesCollapse( boolean selected ) {
        this.detectionCausesCollapse = selected;
    }

    public void addPotential( Potential potential ) {
        compositePotential.addPotential( potential );
        updatePotential();
    }

    private void updatePotential() {
        sourcePotential.clear();
        for( int i = 0; i < compositePotential.numPotentials(); i++ ) {
            Potential p = compositePotential.potentialAt( i );
            if( p != doubleSlitPotential ) {
                sourcePotential.addPotential( p );
            }
        }
    }

    public void clearPotential() {
        boolean doubleSlitEnabled = isDoubleSlitEnabled();
        compositePotential.clear();
        setDoubleSlitEnabled( false );
        if( isDoubleSlitEnabled() != doubleSlitEnabled ) {
            notifySlitStateChanged();
        }
        updatePotential();
    }

    public double getSimulationTime() {
        return getPropagator().getSimulationTime();
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
        this.waveModel.setPropagator( propagator );
        sourceWaveModel.setPropagator( propagator.copy() );
        sourceWaveModel.getPropagator().setPotential( sourcePotential );
    }

    public Propagator getPropagator() {
        return waveModel.getPropagator();
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
        updatePotential();
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
        sourceWaveModel.clear();
        waveModel.clear();
    }

    public void reduceWavefunctionNorm( double normDecrement ) {
        if( normDecrement != 0.0 ) {
            double magnitude = getWavefunction().getMagnitude();
            double newMagnitude = magnitude - normDecrement;
            double scale = newMagnitude <= 0.0 ? 0.0 : newMagnitude / magnitude;
            getWavefunction().scale( scale );
            getSourceWave().scale( scale );
            if( getPropagator()instanceof ClassicalWavePropagator ) {
                ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)getPropagator();
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
        getPropagator().setBoundaryCondition( i, k, value );
        getSourcePropagator().setBoundaryCondition( i, k, value );
    }

    public boolean containsListener( Listener listener ) {
        return listeners.contains( listener );
    }

    public DetectorSet getDetectorSet() {
        return detectorSet;
    }

    public void normalizeWavefunction() {
        sourceWaveModel.normalize();
        waveModel.normalize();
    }

    public void setWavefunctionNorm( double norm ) {
        sourceWaveModel.setWavefunctionNorm( norm );
        waveModel.setWavefunctionNorm( norm );
        //todo working here
//        getWavefunction().setMagnitude( norm );
//        getSourceWave().setMagnitude( norm );
//        getPropagator().setWavefunctionNorm( norm );
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
