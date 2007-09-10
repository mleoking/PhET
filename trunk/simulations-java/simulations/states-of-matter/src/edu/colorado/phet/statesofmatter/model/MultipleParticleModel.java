package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.engine.EngineConfig;
import edu.colorado.phet.statesofmatter.model.engine.ForceComputation;
import edu.colorado.phet.statesofmatter.model.engine.ForceEngine;
import edu.colorado.phet.statesofmatter.model.engine.RungeKuttaForceEngine;
import edu.colorado.phet.statesofmatter.model.particle.NonOverlappingParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.ParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleParticleModel extends BaseModel implements ClockListener {
    private final List particles = new ArrayList();

    public static final MultipleParticleModel TEST = new MultipleParticleModel(ConstantDtClock.TEST);

    private double particleRadius = 0.1;
    private double particleMass   = 1024;
    private ForceEngine forceEngine = new RungeKuttaForceEngine();

    public MultipleParticleModel(IClock clock) {
        clock.addClockListener(this);

        initialize();
    }

    public void initialize() {
        ParticleCreationStrategy strategy = new NonOverlappingParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS, particleRadius);

        particles.clear();

        for (int i = 0; i < StatesOfMatterConfig.INITIAL_PARTICLE_COUNT; i++) {
            particles.add(strategy.createNewParticle(particles, particleRadius, particleMass));
        }
    }

    public List getParticles() {
        return Collections.unmodifiableList(particles);
    }

    public int getNumParticles() {
        return particles.size();
    }

    public StatesOfMatterParticle getParticle(int i) {
        return (StatesOfMatterParticle)particles.get(i);
    }

    public void clockTicked(ClockEvent clockEvent) {
        ForceComputation computation = forceEngine.compute((StatesOfMatterParticle[])particles.toArray(new StatesOfMatterParticle[0]), EngineConfig.TEST);

        Vector2D.Double[] newPositions  = computation.getNewPositions();
        Vector2D.Double[] newVelocities = computation.getNewVelocities();

        for (int i = 0; i < getNumParticles(); i++) {
            StatesOfMatterParticle p = getParticle(i);

            p.setX(newPositions[i].getX());
            p.setY(newPositions[i].getY());

            p.setVx(newVelocities[i].getX());
            p.setVy(newVelocities[i].getY());
        }
//        for (int i = 0; i < getNumParticles(); i++) {
//            StatesOfMatterParticle p = getParticle(i);
//
//            p.setX(p.getX() + 0.01 * (Math.random() * 2.0 - 1.0));
//            p.setY(p.getY() + 0.01 * (Math.random() * 2.0 - 1.0));
//        }
    }

    public void clockStarted(ClockEvent clockEvent) {
    }

    public void clockPaused(ClockEvent clockEvent) {
    }

    public void simulationTimeChanged(ClockEvent clockEvent) {
    }

    public void simulationTimeReset(ClockEvent clockEvent) {
    }
}
