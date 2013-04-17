// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.util.Random;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * RandomWalk
 * <p/>
 * Applies a random walk characteristic to the movement of bodies
 *
 * @author Ron LeMaster
 */
public class RandomWalk {

    private Random random = new Random();
    private double theta = 30;
    private Vessel vessel;

    public RandomWalk( Vessel vessel ) {
        this.vessel = vessel;
    }

    /**
     * Applies the random walk to a specified body, if the body is in the water
     * contained by the vessel. Otherwise, nothing is done.
     * <p/>
     * The velocity of the specified body is modified
     *
     * @param body
     * @return The body's new velocity
     */
    public MutableVector2D apply( Body body ) {
        if ( body.getVelocity().magnitude() != 0 && vessel.getWater().getBounds().contains( body.getPosition() ) ) {
            double theta = random.nextDouble() * Math.toRadians( this.theta ) * MathUtil.nextRandomSign();
            body.setVelocity( body.getVelocity().rotate( theta ) );
        }
        return body.getVelocity();
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta( double theta ) {
        this.theta = theta;
    }
}
