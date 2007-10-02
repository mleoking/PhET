package edu.colorado.phet.statesofmatter.model.container;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;

public class RectangularParticleContainer extends AbstractParticleContainer {
    private final Rectangle2D.Double shape;
    private ParticleContainerWall northWall;
    private ParticleContainerWall westWall;
    private ParticleContainerWall eastWall;
    private ParticleContainerWall southWall;

    public RectangularParticleContainer(Rectangle2D.Double shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public ParticleContainerWall getNorthWall() {
        if (northWall == null)
            northWall = new ParticleContainerWall(shape.x, shape.y, shape.x + shape.width, shape.y);

        return northWall;
    }

    public ParticleContainerWall getWestWall() {
        if (westWall == null)
            westWall = new ParticleContainerWall(shape.x, shape.y + shape.height, shape.x, shape.y);

        return westWall;
    }

    public ParticleContainerWall getEastWall() {
        if (eastWall == null)
            eastWall = new ParticleContainerWall(shape.x + shape.width, shape.y, shape.x + shape.width, shape.y + shape.height);

        return eastWall;
    }

    public ParticleContainerWall getSouthWall() {
        if (southWall == null)
            southWall = new ParticleContainerWall(shape.x + shape.width, shape.y + shape.height, shape.x, shape.y + shape.height);
        
        return southWall;
    }
    
    public Collection getAllWalls() {
        return Arrays.asList(new ParticleContainerWall[]{getNorthWall(), getWestWall(), getSouthWall(), getEastWall()});
    }
}
