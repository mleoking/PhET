package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;

public class EngineConfig {
    public static final EngineConfig TEST = new EngineConfig(
        0.0005,
        new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS),
        1.0,
        1.0,
        1.0
    );

    public double gravity;
    public ParticleContainer container;
    public double epsilon;
    public double rMin;
    public double deltaT;

    public EngineConfig() {
    }

    public EngineConfig(double gravity, ParticleContainer container, double epsilon, double rmin, double deltaT) {
        this.gravity   = gravity;
        this.container = container;
        this.epsilon   = epsilon;
        this.rMin      = rmin;
        this.deltaT    = deltaT;
    }
}
