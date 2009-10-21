package edu.colorado.phet.genenetwork.model;

import java.awt.*;
import java.awt.geom.Point2D;

public interface IModelElement {
    Shape getShape();

    Point2D getPosition();
}
