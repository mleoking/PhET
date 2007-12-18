package edu.colorado.phet.statesofmatter.model.container;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;

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

    private Point2D getOriginOfRotation() {
        return new Point2D.Double(originalShape.getCenterX(), originalShape.getMaxY());
    }

    protected void rotateImpl(double rotation) {
//        Point2D original = new Point2D.Double(x, y);
//        AffineTransform at = getRotateInstance(theta, 200.0, 200.0);
//        Point2D rotated = at.transform(original, null);

    }
}
