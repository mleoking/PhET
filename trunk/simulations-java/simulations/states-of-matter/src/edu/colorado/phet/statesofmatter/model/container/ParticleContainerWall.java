package edu.colorado.phet.statesofmatter.model.container;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class ParticleContainerWall extends Line2D.Double {
    public ParticleContainerWall() {
        super();
    }

    public ParticleContainerWall(double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
    }

    public ParticleContainerWall(Point2D point2D, Point2D point2D1) {
        super(point2D, point2D1);
    }
}
