package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;

import java.awt.geom.Rectangle2D;

public class EngineConfig {
    public static final EngineConfig TEST = new EngineConfig(
        0.0,
        0.0,
        StatesOfMatterConfig.CONTAINER_BOUNDS,
        Math.pow(2.0, 1.0/6.0)
    );

    public double gravity;
    public double piston;
    public Rectangle2D.Double bounds;
    public double range;

    public EngineConfig() {
    }

    public EngineConfig(double gravity, double piston, Rectangle2D.Double bounds, double range) {
        this.gravity = gravity;
        this.piston  = piston;
        this.bounds  = bounds;
        this.range   = range;
    }
}
