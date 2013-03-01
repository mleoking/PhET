// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.PhetCamera.CameraStrategy;
import edu.colorado.phet.jmephet.PhetCamera.CenteredStageCameraStrategy;

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

    public static class CenteredStageCanvasTransform extends CanvasTransform {

        private final Dimension stageSize;
        private final Property<Dimension> canvasSize;

        public CenteredStageCanvasTransform( Property<Dimension> canvasSize ) {
            this.canvasSize = canvasSize;

            stageSize = canvasSize.get();

            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    transform.set( getTransform() );
                }
            } );
        }

        public CameraStrategy getCameraStrategy( float fovY, float near, float far ) {
            // TODO: pass in the initial size here?
            return new CenteredStageCameraStrategy( fovY, near, far );
        }

        public AffineTransform getTransform() {
            double sx = canvasSize.get().getWidth() / stageSize.getWidth();
            double sy = canvasSize.get().getHeight() / stageSize.getHeight();

            //use the smaller and maintain aspect ratio so that circles don't become ellipses
            double scale = sx < sy ? sx : sy;
            scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

            AffineTransform transform = new AffineTransform();
            double scaledStageWidth = scale * stageSize.getWidth();
            double scaledStageHeight = scale * stageSize.getHeight();
            //center it in width and height
            transform.translate( canvasSize.get().getWidth() / 2 - scaledStageWidth / 2, canvasSize.get().getHeight() / 2 - scaledStageHeight / 2 );
            transform.scale( scale, scale );

            return transform;
        }
    }
}
