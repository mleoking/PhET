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

    public ParticleContainerWall getNorthWall() {
        return new ParticleContainerWall(shape.x, shape.y, shape.x + shape.width, shape.y);
    }

    public ParticleContainerWall getWestWall() {
        return new ParticleContainerWall(shape.x, shape.y + shape.height, shape.x, shape.y);
    }

    public ParticleContainerWall getEastWall() {
        return new ParticleContainerWall(shape.x + shape.width, shape.y, shape.x + shape.width, shape.y + shape.height);
    }

    public ParticleContainerWall getSouthWall() {
        return new ParticleContainerWall(shape.x + shape.width, shape.y + shape.height, shape.x, shape.y + shape.height);
    }
}
