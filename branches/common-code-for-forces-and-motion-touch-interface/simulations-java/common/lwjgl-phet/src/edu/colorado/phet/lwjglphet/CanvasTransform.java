// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Handles the stage to canvas transforms that allow scaling based on how large the canvas is to a fixed stage size.
 * <p/>
 * TODO: with how OpenGL works, can't we just ignore this general kind of thing and wrap it in the projection matrix?
 */
public abstract class CanvasTransform {
    public final Property<AffineTransform> transform = new Property<AffineTransform>( null );

    public abstract float getFieldOfViewYFactor();

    public Rectangle2D getTransformedBounds( Rectangle2D bounds ) {
        return transform.get().createTransformedShape( bounds ).getBounds2D();
    }

    public Rectangle2D getInverseTransformedBounds( Rectangle2D bounds ) {
        try {
            return transform.get().createInverse().createTransformedShape( bounds ).getBounds2D();
        }
        catch( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }

    public static class IdentityCanvasTransform extends CanvasTransform {
        public IdentityCanvasTransform() {
            transform.set( new AffineTransform() );
        }

        @Override public float getFieldOfViewYFactor() {
            return 1;
        }
    }

    public static class StageCenteringCanvasTransform extends CanvasTransform {
        private final Dimension stageSize;
        private final Property<Dimension> canvasSize;

        public StageCenteringCanvasTransform( final Property<Dimension> canvasSize, final Dimension stageSize ) {
            this.canvasSize = canvasSize;
            this.stageSize = stageSize;

            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    transform.set( getStageCenteringTransform( canvasSize.get(), stageSize ) );
                }
            } );
        }

        public Property<Dimension> getCanvasSize() {
            return canvasSize;
        }

        public Dimension getStageSize() {
            return stageSize;
        }

        @Override public float getFieldOfViewYFactor() {
            return getStageCenteringFieldOfViewYFactor( canvasSize.get(), stageSize );
        }
    }

    public static float getStageCenteringFieldOfViewYFactor( Dimension canvasSize, Dimension stageSize ) {
        double sx = canvasSize.getWidth() / stageSize.getWidth();
        double sy = canvasSize.getHeight() / stageSize.getHeight();
        if ( sx == 0 || sy == 0 ) {
            return 1;
        }
        return sy > sx ? (float) ( sy / sx ) : 1;
    }

    public static AffineTransform getStageCenteringTransform( Dimension canvasSize, Dimension stageSize ) {
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

    public static void applyAffineTransform( AffineTransform transform ) {
        // TODO: beware of potential perm-gen memory leaks here? are we sure that all JREs collect the direct buffers?
        double[] m = new double[6];
        transform.getMatrix( m );
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer( 16 );
        // column-major order. argh
        buffer.put( new double[]{m[0], m[1], 0, 0, m[2], m[3], 0, 0, 0, 0, 1, 0, m[4], m[5], 0, 1} );
        buffer.rewind();
        GL11.glMultMatrix( buffer );
    }

}
