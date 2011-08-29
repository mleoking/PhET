// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Solution;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * Strategy pattern for updating particles in their different states, such as "flowing toward drain", "random walk", "flowing out of drain", "dropping from the crystal shaker"
 * Using the strategy pattern here provides the following benefits:
 * 1. Modularize the code for updating particles, split into smaller files
 * 2. No particle can belong to two strategies
 * 3. A good place to store state for each particle (such as speed of flow to drain, etc)
 * <p/>
 * There are, however, the following disadvantages:
 * 1. More difficult to mix and match several facets of different behavior
 *
 * @author Sam Reid
 */
public abstract class UpdateStrategy implements IUpdateStrategy {
    public final MicroModel model;
    public final Solution solution;
    public final DoubleProperty waterVolume;

    //Speed at which freely moving particles should random walk
    public static final double FREE_PARTICLE_SPEED = 6E-10;

    //Particle mass, used in stepping forward in time according to newton's 2nd law.
    //This number was obtained by guessing and checking to find a value that looked good for accelerating the particles out of the shaker
    public static final double PARTICLE_MASS = 1E10;

    public UpdateStrategy( MicroModel model ) {
        this.model = model;
        solution = model.solution;
        waterVolume = model.waterVolume;
    }

    public abstract void stepInTime( Particle particle, double dt );
}