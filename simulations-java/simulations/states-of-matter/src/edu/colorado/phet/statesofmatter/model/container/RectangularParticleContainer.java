package edu.colorado.phet.statesofmatter.model.container;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectangularParticleContainer extends AbstractParticleContainer {
    private final Rectangle2D.Double shape;

    public RectangularParticleContainer(Rectangle2D.Double shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }
}
