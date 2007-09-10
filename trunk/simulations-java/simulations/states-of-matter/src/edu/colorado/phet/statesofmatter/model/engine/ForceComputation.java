package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class ForceComputation {
    private final Vector2D.Double[] newPositions;
    private final Vector2D.Double[] newVelocities;
    private final double kineticEnergy;
    private final double potentialEnergy;

    public ForceComputation(Vector2D.Double[] newPositions, Vector2D.Double[] newVelocities, double kineticEnergy, double potentialEnergy) {
        this.newPositions    = newPositions;
        this.newVelocities   = newVelocities;
        this.kineticEnergy   = kineticEnergy;
        this.potentialEnergy = potentialEnergy;
    }

    public Vector2D.Double[] getNewPositions() {
        return newPositions;
    }

    public Vector2D.Double[] getNewVelocities() {
        return newVelocities;
    }

    public double getKineticEnergy() {
        return kineticEnergy;
    }

    public double getPotentialEnergy() {
        return potentialEnergy;
    }
}
