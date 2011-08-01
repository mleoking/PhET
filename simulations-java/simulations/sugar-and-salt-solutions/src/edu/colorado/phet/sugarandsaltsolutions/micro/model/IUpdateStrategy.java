// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Strategy pattern interface for updating particles as time passes, see UpdateStrategy.
 *
 * @author Sam Reid
 */
public interface IUpdateStrategy {
    public abstract void stepInTime( Particle particle, double dt );
}
