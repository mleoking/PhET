// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import com.jme3.renderer.Camera;

/**
 * A camera specifically built for overlay views, where the viewport has a restricted screen bounds that may change.
 */
public abstract class OverlayCamera extends Camera {
    private final Property<Dimension> canvasSize;
    private final Property<? extends Rectangle2D> screenBounds;

    public OverlayCamera( Dimension initialSize, Property<Dimension> canvasSize, Property<? extends Rectangle2D> screenBounds ) {
        super( initialSize.width, initialSize.height );
        this.canvasSize = canvasSize;
        this.screenBounds = screenBounds;

        SimpleObserver updateObserver = JMEUtils.jmeObserver( new Runnable() {
            public void run() {
                // make sure to run these updates in the JME thread!
                updateOverlay();
            }
        } );
        canvasSize.addObserver( updateObserver, false );
        screenBounds.addObserver( updateObserver, false );

        positionMe();
    }

    public abstract void positionMe();

    public void updateOverlay() {
        // rescale these numbers to between 0 and 1 (for the entire JME3 canvas size)
        float finalLeft = (float) ( screenBounds.get().getMinX() / canvasSize.get().width );
        float finalRight = (float) ( screenBounds.get().getMaxX() / canvasSize.get().width );
        float finalBottom = (float) ( screenBounds.get().getMinY() / canvasSize.get().height );
        float finalTop = (float) ( screenBounds.get().getMaxY() / canvasSize.get().height );

        // position the overlay viewport over this region
        setViewPort( finalLeft, finalRight, finalBottom, finalTop );

        // reposition, due to as-of-yet unknown reasons
        positionMe();

        // trigger internal recalculations
        update();
    }

    @Override public void resize( int width, int height, boolean fixAspect ) {
        super.resize( width, height, fixAspect );

        updateOverlay();
    }
}
