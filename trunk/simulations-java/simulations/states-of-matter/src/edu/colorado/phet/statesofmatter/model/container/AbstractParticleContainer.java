package edu.colorado.phet.statesofmatter.model.container;

import java.awt.geom.Point2D;

public abstract class AbstractParticleContainer implements ParticleContainer {
    private double rotation;
    private Point2D.Double centerOfRotation;

    public final double getRotation() {
        return rotation;
    }

    public final void setRotation(double rotation) {
        this.rotation = rotation;

        rotateImpl(rotation);
    }

    public Point2D.Double getCenterOfRotation() {
        if (centerOfRotation == null) {
            centerOfRotation = new Point2D.Double(getSouthWall().getBounds().getCenterX(), getSouthWall().getBounds().getCenterY());
        }
        
        return centerOfRotation;
    }

    public void setCenterOfRotation(Point2D.Double center) {
        this.centerOfRotation = center;
    }

    protected abstract void rotateImpl(double rotation);
}
