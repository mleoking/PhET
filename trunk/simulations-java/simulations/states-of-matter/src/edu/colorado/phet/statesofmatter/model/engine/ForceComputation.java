package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

public class ForceComputation {
    private final Point2D.Double[] newPositions;
    private final Vector2D.Double[] newVelocities;
    private final Vector2D.Double[] newAccelerations;
    
    ForceComputation(Point2D.Double[] newPositions, Vector2D.Double[] newVelocities, Vector2D.Double[] newAccelerations) {
        this.newPositions     = newPositions;
        this.newVelocities    = newVelocities;
        this.newAccelerations = newAccelerations;
    }

    public Point2D.Double[] getNewPositions() {
        return newPositions;
    }

    public Vector2D.Double[] getNewVelocities() {
        return newVelocities;
    }

    public Vector2D.Double[] getNewAccelerations() {
        return newAccelerations;
    }

    // TODO: Test
    public void apply(Collection particles) {
        int i = 0;

        for (Iterator iterator = particles.iterator(); iterator.hasNext();) {
            StatesOfMatterAtom p = (StatesOfMatterAtom)iterator.next();

            p.setPosition(newPositions[i].getX(), newPositions[i].getY());

            p.setVx(newVelocities[i].getX());
            p.setVy(newVelocities[i].getY());

            p.setAx(newAccelerations[i].getX());
            p.setAy(newAccelerations[i].getY());

            ++i;
        }
    }
}
