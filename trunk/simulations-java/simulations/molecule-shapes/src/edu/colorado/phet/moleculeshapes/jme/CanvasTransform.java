// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.jme.PhetCamera.CameraStrategy;
import edu.colorado.phet.moleculeshapes.jme.PhetCamera.CenteredStageCameraStrategy;

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

        private final PhetJMEApplication app;
        private final Dimension stageSize;

        public CenteredStageCanvasTransform( PhetJMEApplication app ) {
            this.app = app;

            stageSize = app.canvasSize.get();

            app.canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    transform.set( getTransform() );
//                    System.out.println( transform );
                }
            } );
        }

        public CameraStrategy getCameraStrategy( float fovY, float near, float far ) {
            // TODO: pass in the initial size here?
            return new CenteredStageCameraStrategy( fovY, near, far );
        }

        public AffineTransform getTransform() {
            Dimension canvasSize = app.canvasSize.get();

            double sx = canvasSize.getWidth() / stageSize.getWidth();
            double sy = canvasSize.getHeight() / stageSize.getHeight();

            //use the smaller and maintain aspect ratio so that circles don't become ellipses
            double scale = sx < sy ? sx : sy;
            scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

            AffineTransform transform = new AffineTransform();
            double scaledStageWidth = scale * stageSize.getWidth();
            double scaledStageHeight = scale * stageSize.getHeight();
            //center it in width and height
            transform.translate( canvasSize.getWidth() / 2 - scaledStageWidth / 2, canvasSize.getHeight() / 2 - scaledStageHeight / 2 );
            transform.scale( scale, scale );

            return transform;
        }
    }
}
