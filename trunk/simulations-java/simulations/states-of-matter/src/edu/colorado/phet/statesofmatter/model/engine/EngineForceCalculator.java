package edu.colorado.phet.statesofmatter.model.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.colorado.phet.statesofmatter.model.engine.gravity.GravityForceCalculator;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesForce;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesForceCalculator;
import edu.colorado.phet.statesofmatter.model.engine.lj.LennardJonesWallForceCalculator;

public class EngineForceCalculator extends CompositeCalculator {
    public EngineForceCalculator(double g, LennardJonesForce ljf, Collection particles, Collection walls) {
        super(new ArrayList());

        getCalculators().add(new GravityForceCalculator(g));
        getCalculators().add(new LennardJonesForceCalculator(ljf, particles));

        for (Iterator iterator = walls.iterator(); iterator.hasNext();) {
//            ParticleContainerWall wall = (ParticleContainerWall)iterator.next();

//            getCalculators().add(new LennardJonesWallForceCalculator(ljf, wall));
        }
    }
}
