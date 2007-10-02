package edu.colorado.phet.statesofmatter.model.container;

import java.awt.*;

public interface ParticleContainer {
    Shape getShape();

    ParticleContainerWall getNorthWall();

    ParticleContainerWall getWestWall();

    ParticleContainerWall getEastWall();

    ParticleContainerWall getSouthWall();
}
