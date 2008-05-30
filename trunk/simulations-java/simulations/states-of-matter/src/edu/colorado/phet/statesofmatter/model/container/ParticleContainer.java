package edu.colorado.phet.statesofmatter.model.container;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Collection;

public interface ParticleContainer {
    Shape getShape();

    ParticleContainerWall getNorthWall();

    ParticleContainerWall getWestWall();

    ParticleContainerWall getEastWall();

    ParticleContainerWall getSouthWall();

    Collection getAllWalls();

    void setRotation(double radians);

    double getRotation();

    Point2D.Double getCenterOfRotation();

    void setCenterOfRotation(Point2D.Double center);
}
