package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class ForceComputation {
    private final Vector2D.Double[] newPositions;
    private final Vector2D.Double[] newVelocities;

    ForceComputation(Vector2D.Double[] newPositions, Vector2D.Double[] newVelocities) {
        this.newPositions  = newPositions;
        this.newVelocities = newVelocities;
    }

    public Vector2D.Double[] getNewPositions() {
        return newPositions;
    }

    public Vector2D.Double[] getNewVelocities() {
        return newVelocities;
    }
}
