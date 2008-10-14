package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

public class EngineConfig implements Cloneable {
    public static final EngineConfig TEST = new EngineConfig(
        StatesOfMatterConstants.GRAVITY,
//        new RectangularParticleContainer(StatesOfMatterConstants.CONTAINER_BOUNDS),
        StatesOfMatterConstants.EPSILON,
        StatesOfMatterConstants.RMIN,
        StatesOfMatterConstants.DELTA_T
    );

    public double gravity;
//    public ParticleContainer container;
    public double epsilon;
    public double rMin;
    public double deltaT;

    public EngineConfig(double gravity, /* ParticleContainer container, */ double epsilon, double rmin, double deltaT) {
        this.gravity   = gravity;
//        this.container = container;
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
