/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2003
 * Time: 11:05:08 AM
 * Copyright (c) Dec 17, 2003 by Sam Reid
 */
public class FixedModelShape extends SimpleObservable implements HasModelShape {
    Shape shape;

    public Shape getShape() {
        return shape;
    }

    public FixedModelShape(Shape shape) {
        this.shape = shape;
    }
}
