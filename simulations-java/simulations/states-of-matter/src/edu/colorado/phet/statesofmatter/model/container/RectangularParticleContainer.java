package edu.colorado.phet.statesofmatter.model.container;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class RectangularParticleContainer extends AbstractParticleContainer {
    private final Rectangle2D.Double originalShape;
    private ParticleContainerWall northWall;
    private ParticleContainerWall westWall;
    private ParticleContainerWall eastWall;
    private ParticleContainerWall southWall;

    public RectangularParticleContainer(Rectangle2D.Double shape) {
        this.originalShape = shape;
    }

    public Shape getShape() {
        return originalShape;
    }

    public ParticleContainerWall getNorthWall() {
        if (northWall == null)
            northWall = new ParticleContainerWall(originalShape.x, originalShape.y, originalShape.x + originalShape.width, originalShape.y);

        return northWall;
    }

    public ParticleContainerWall getWestWall() {
        if (westWall == null)
            westWall = new ParticleContainerWall(originalShape.x, originalShape.y + originalShape.height, originalShape.x, originalShape.y);

        return westWall;
    }

    public ParticleContainerWall getEastWall() {
        if (eastWall == null)
            eastWall = new ParticleContainerWall(originalShape.x + originalShape.width, originalShape.y, originalShape.x + originalShape.width, originalShape.y + originalShape.height);

        return eastWall;
    }

    public ParticleContainerWall getSouthWall() {
        if (southWall == null)
            southWall = new ParticleContainerWall(originalShape.x + originalShape.width, originalShape.y + originalShape.height, originalShape.x, originalShape.y + originalShape.height);

        return southWall;
    }

    public Collection getAllWalls() {
        return Arrays.asList(new ParticleContainerWall[]{getNorthWall(), getWestWall(), getSouthWall(), getEastWall()});
    }

    protected void rotateImpl(double rotation) {
        northWall = eastWall = southWall = westWall = null;
        
        AffineTransform at = AffineTransform.getRotateInstance(getRotation(), getCenterOfRotation().getX(), getCenterOfRotation().getX());

        for (Iterator iterator = getAllWalls().iterator(); iterator.hasNext();) {
            ParticleContainerWall wall = (ParticleContainerWall)iterator.next();

            Point2D p1 = at.transform(wall.getP1(), null);
            Point2D p2 = at.transform(wall.getP2(), null);

            wall.setLine(p1, p2);
        }
    }
}
