// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Prism {
    public final Property<Shape> shape;

    public Prism( Shape shape ) {
        this.shape = new Property<Shape>( shape );
    }

    public void translate( double dx, double dy ) {
        shape.setValue( AffineTransform.getTranslateInstance( dx, dy ).createTransformedShape( shape.getValue() ) );
    }
}
