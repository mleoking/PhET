/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.potentials.ConstantPotential;
import edu.colorado.phet.qm.model.propagators.NullPropagator;


/**
 * Duplicated from SplitModel.
 */

public class MandelModel extends DiscreteModel {
    private WaveModel rightWaveModel;
    private WaveModel leftWaveModel;

    private Mode mode = new NormalMode();

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

        setDetectionCausesCollapse( false );//since it's a split model

        if( DEBUG_WAVES ) {
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
        mode.step();
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

    private void superStep() {
        super.step();
    }

    public void setSplitMode( boolean split ) {
        this.mode = split ? (Mode)new SplitMode() : new NormalMode();
    }

    static interface Mode {
        void step();
    }

    class NormalMode implements Mode {
        public void step() {
            MandelModel.this.superStep();
        }
    }

    class SplitMode implements Mode {
        public void step() {
            beforeTimeStep();
            getPropagationStrategy().step();
            finishStep();
        }

        private void finishStep() {
            rightWaveModel.propagate();
            leftWaveModel.propagate();
            getDamping().damp( getRightWavefunction() );
            getDamping().damp( getLeftWavefunction() );

            incrementTimeStep();
            finishedTimeStep();
        }
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        rightWaveModel.setWaveSize( width, height );
        leftWaveModel.setWaveSize( width, height );
    }
}
