package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
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

    public MultipleParticleModel(IClock clock) {
        clock.addClockListener(this);

        initialize();
    }

    public void initialize() {
        particles.clear();

        ParticleCreationStrategy strategy = new NonOverlappingParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS, particleRadius);

        for (int i = 0; i < StatesOfMatterConfig.INITIAL_PARTICLE_COUNT; i++) {
            particles.add(strategy.createNewParticle(particles, particleRadius));
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
        for (int i = 0; i < getNumParticles(); i++) {
            StatesOfMatterParticle p = getParticle(i);

            p.setX(p.getX() + 0.01 * (Math.random() * 2.0 - 1.0));
            p.setY(p.getY() + 0.01 * (Math.random() * 2.0 - 1.0));
        }
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
