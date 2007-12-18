package edu.colorado.phet.statesofmatter.model.container;

import java.awt.*;
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

}
