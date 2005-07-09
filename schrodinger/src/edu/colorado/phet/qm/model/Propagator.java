package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 10:34:28 PM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */
public interface Propagator {
    public void propagate( Wavefunction w );

    void setDeltaTime( double deltaTime );

    double getSimulationTime();

    void reset();

    void setBoundaryCondition( int i, int k, Complex value );
}
