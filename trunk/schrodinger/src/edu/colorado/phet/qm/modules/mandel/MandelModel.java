/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.math.Complex;
import edu.colorado.phet.qm.model.potentials.ConstantPotential;
import edu.colorado.phet.qm.model.propagators.NullPropagator;


/**
 * Duplicated from SplitModel.
 */

public class MandelModel extends DiscreteModel {
    private WaveModel rightWaveModel;
    private WaveModel leftWaveModel;

    private boolean split = false;
    private static final boolean DEBUG_MANDEL_WAVES = false;

    public MandelModel() {
        this( DiscreteModel.DEFAULT_WIDTH, DiscreteModel.DEFAULT_WIDTH );
    }

    public MandelModel( int width, int height ) {
        this( width, height, DEFAULT_DT, createInitWave() );
    }

    public MandelModel( int width, int height, double deltaTime, Wave wave ) {
        super( width, height, deltaTime, wave );
        rightWaveModel = new WaveModel( new Wavefunction( getGridWidth(), getGridHeight() ), new NullPropagator( new ConstantPotential() ) );
        leftWaveModel = new WaveModel( new Wavefunction( getGridWidth(), getGridHeight() ), new NullPropagator( new ConstantPotential() ) );
        super.setBarrierAbsorptive( false );
        setDetectionCausesCollapse( false );//since it's a split model

        if( DEBUG_MANDEL_WAVES ) {
            final WaveDebugger leftWaveDebugger = new WaveDebugger( "Left", getLeftWavefunction() );
            leftWaveDebugger.setVisible( true );
            addListener( new Adapter() {
                public void finishedTimeStep( DiscreteModel model ) {
                    leftWaveDebugger.update();
                }
            } );

            final WaveDebugger rightWaveDebugger = new WaveDebugger( "Right", getRightWavefunction() );
            rightWaveDebugger.setVisible( true );
            addListener( new Adapter() {
                public void finishedTimeStep( DiscreteModel model ) {
                    rightWaveDebugger.update();
                }
            } );
        }
    }

    public void setPropagator( Propagator propagator ) {
        super.setPropagator( propagator );
        leftWaveModel.setPropagator( propagator.copy() );
        rightWaveModel.setPropagator( propagator.copy() );
    }

    protected void step() {
        beforeTimeStep();
        if( !split ) {
            getWaveModel().propagate();
            getDamping().damp( getWavefunction() );
        }
        else {
            leftWaveModel.propagate();
            rightWaveModel.propagate();
            getDamping().damp( leftWaveModel.getWavefunction() );
            getDamping().damp( rightWaveModel.getWavefunction() );
        }
        incrementTimeStep();
        finishedTimeStep();
    }

    public void clearAllWaves() {
        super.clearAllWaves();
        leftWaveModel.clear();
        rightWaveModel.clear();
    }

    public void reset() {
        super.reset();
        rightWaveModel.clear();
        leftWaveModel.clear();
    }

    public void clearWavefunction() {
        super.clearWavefunction();
        rightWaveModel.clear();
        leftWaveModel.clear();
    }

    public Wavefunction getRightWavefunction() {
        return rightWaveModel.getWavefunction();
    }

    public Wavefunction getLeftWavefunction() {
        return leftWaveModel.getWavefunction();
    }

    public Propagator getLeftPropagator() {
        return leftWaveModel.getPropagator();
    }

    public Propagator getRightPropagator() {
        return rightWaveModel.getPropagator();
    }

    public void setSplitMode( boolean split ) {
        this.split = split;
    }

    public boolean isSplit() {
        return split;
    }

    public WaveModel getLeftWaveModel() {
        return leftWaveModel;
    }

    public WaveModel getRightWaveModel() {
        return rightWaveModel;
    }

    static interface Mode {
        void step();
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        rightWaveModel.setWaveSize( width, height );
        leftWaveModel.setWaveSize( width, height );
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        if( split ) {
            Wavefunction left = getLeftWaveModel().getWavefunction().copyRegion( 0, detectionY, width, h );
            Wavefunction right = getRightWaveModel().getWavefunction().copyRegion( 0, detectionY, width, h );
            Wavefunction avg = left.copy();
            for( int i = 0; i < avg.getWidth(); i++ ) {
                for( int j = 0; j < avg.getHeight(); j++ ) {
                    Complex a = left.valueAt( i, j );
                    Complex b = right.valueAt( i, j );
                    avg.setValue( i, j, ( a.abs() + b.abs() ) / 2.0, 0 );
                }
            }
            return avg;
        }
        else {
            return getWavefunction().copyRegion( 0, detectionY, width, h );
        }
    }

    public void updateWavefunctionAfterDetection() {
//        clearAllWaves();
    }
}
