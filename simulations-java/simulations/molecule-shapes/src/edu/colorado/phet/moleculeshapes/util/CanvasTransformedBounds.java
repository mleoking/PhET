// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.CanvasTransform;

/**
 * Keeps an updated map of a transformed bounds
 */
public class CanvasTransformedBounds extends Property<Rectangle2D> {
    public CanvasTransformedBounds( final CanvasTransform canvasTransform, final Property<Rectangle2D> stageBounds ) {
        super( canvasTransform.getTransformedBounds( stageBounds.get() ) );

        SimpleObserver updateObserver = new SimpleObserver() {
            public void update() {
                set( canvasTransform.getTransformedBounds( stageBounds.get() ) );
            }
        };
        canvasTransform.transform.addObserver( updateObserver, false );
        stageBounds.addObserver( updateObserver, false );
    }
}

