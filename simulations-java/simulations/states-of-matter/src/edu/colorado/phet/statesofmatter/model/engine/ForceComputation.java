package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class ForceComputation {
    private final Vector2D.Double[] newPositions;
    private final Vector2D.Double[] newVelocities;
    private final Vector2D.Double[] newAccelerations;

    ForceComputation(Vector2D.Double[] newPositions, Vector2D.Double[] newVelocities) {
        this(newPositions, newVelocities, new Vector2D.Double[0]);
    }

    ForceComputation(Vector2D.Double[] newPositions, Vector2D.Double[] newVelocities, Vector2D.Double[] newAccelerations) {
        this.newPositions     = newPositions;
        this.newVelocities    = newVelocities;
        this.newAccelerations = newAccelerations;
    }

    public Vector2D.Double[] getNewPositions() {
        return newPositions;
    }

    public Vector2D.Double[] getNewVelocities() {
        return newVelocities;
    }

    public Vector2D.Double[] getNewAccelerations() {
        return newAccelerations;
    }
}
