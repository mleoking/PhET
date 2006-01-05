package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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
//    private DiscreteModel discreteModel;

    protected Propagator( Potential potential ) {
//        this.discreteModel = discreteModel;
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

//    public DiscreteModel getDiscreteModel() {
//        return discreteModel;
//    }

    public void copyTo( int i, int j, Propagator propagator ) {
    }

    public void clearWave( Rectangle rect ) {
    }

    public void splitWave( Rectangle region, Propagator a, Propagator b ) {
    }


    public void combineWaves( Rectangle region, Propagator a, Propagator b ) {
    }

    public Map getModelParameters() {
        return new HashMap();
    }
}
