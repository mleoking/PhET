package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.common.phetcommon.patterns.PubliclyCloneable;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;

public class EngineConfig implements PubliclyCloneable {
    public static final EngineConfig TEST = new EngineConfig(
        -150,
        new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS),
        1.0,
        2.0 * StatesOfMatterConfig.PARTICLE_RADIUS,
        0.000001
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


    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
