// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;

/**
 * @author Sam Reid
 */
public class Prism {
    private final Shape shape;

    public Prism( Shape shape ) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }
}
