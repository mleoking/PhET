package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 10:34:28 PM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */
public abstract class Propagator {
    private double simulationTime;
    private double deltaTime;
    private Potential potential;
    private DiscreteModel discreteModel;

    protected Propagator( DiscreteModel discreteModel, Potential potential ) {
        this.discreteModel = discreteModel;
        this.potential = potential;
    }

    public abstract void propagate( Wavefunction w );

    public void setDeltaTime( double deltaTime ) {
        this.deltaTime = deltaTime;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public abstract void reset();

    public abstract void setBoundaryCondition( int i, int k, Complex value );

    public abstract Propagator copy();

    public abstract void normalize();

    public abstract void setWavefunctionNorm( double norm );

    public void setPotential( Potential potential ) {
        this.potential = potential;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public Potential getPotential() {
        return potential;
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

}
