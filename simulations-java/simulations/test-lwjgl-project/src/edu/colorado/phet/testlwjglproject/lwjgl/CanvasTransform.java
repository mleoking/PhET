// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;

public abstract class CanvasTransform {
    public final Property<AffineTransform> transform = new Property<AffineTransform>( null );

    public Rectangle2D getTransformedBounds( Rectangle2D bounds ) {
        return transform.get().createTransformedShape( bounds ).getBounds2D();
    }

    public Rectangle2D getInverseTransformedBounds( Rectangle2D bounds ) {
        try {
            return transform.get().createInverse().createTransformedShape( bounds ).getBounds2D();
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public static class IdentityCanvasTransform extends CanvasTransform {
        public IdentityCanvasTransform() {
            transform.set( new AffineTransform() );
        }
    }
}
