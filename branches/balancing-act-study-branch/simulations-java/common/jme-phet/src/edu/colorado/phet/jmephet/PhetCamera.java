// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Dimension;

import com.jme3.renderer.Camera;

public class PhetCamera extends Camera {
    private final Dimension initialSize;

    private CameraStrategy strategy;

    public PhetCamera( Dimension initialSize, CameraStrategy strategy ) {
        super( initialSize.width, initialSize.height );

        this.initialSize = initialSize;
        this.strategy = strategy;

        strategy.initialize( this, initialSize );
    }

    @Override public void resize( int width, int height, boolean fixAspect ) {
        strategy.resize( width, height );
    }

    public static interface CameraStrategy {
        public void initialize( PhetCamera camera, Dimension initialSize );

        public void resetFrustrum();

        public void resize( int width, int height );
    }

    public static class IdentityCameraStrategy implements CameraStrategy {
        private final float fovY;
        private final float near;
        private final float far;
        private PhetCamera camera;
        private Dimension initialSize;

        public IdentityCameraStrategy( float fovY, float near, float far ) {
            this.fovY = fovY;
            this.near = near;
            this.far = far;
        }

        public void initialize( PhetCamera camera, Dimension initialSize ) {
            this.camera = camera;
            this.initialSize = initialSize;
            resetFrustrum();
        }

        public void resetFrustrum() {
            camera.setFrustumPerspective( fovY, (float) initialSize.width / initialSize.height, near, far );
        }

        public void resize( int width, int height ) {
            // don't do anything!
        }
    }

    public static class CenteredStageCameraStrategy implements CameraStrategy {
        private final float fovY;
        private final float near;
        private final float far;

        private float initialAspectRatio;
        private float initialFrustumRight;
        private float initialFrustumTop;
        private PhetCamera camera;

        public CenteredStageCameraStrategy( float fovY, float near, float far ) {
            this.fovY = fovY;
            this.near = near;
            this.far = far;
        }

        public void initialize( PhetCamera camera, Dimension initialSize ) {
            this.camera = camera;

            initialAspectRatio = (float) initialSize.width / initialSize.height;

            camera.setFrustumPerspective( fovY, initialAspectRatio, near, far );

            initialFrustumRight = camera.frustumRight;
            initialFrustumTop = camera.frustumTop;
        }

        public void resetFrustrum() {
            camera.setFrustumPerspective( fovY, initialAspectRatio, near, far );
        }

        public void resize( int width, int height ) {
            camera.width = width;
            camera.height = height;
            camera.onViewPortChange();

            float canvasAspectRatio = (float) width / height;

            if ( canvasAspectRatio > initialAspectRatio ) {
                // our screen has a wider aspect ratio than our starting setup (height-limited)
                camera.frustumBottom = -( camera.frustumTop = initialFrustumTop );
                camera.frustumLeft = -( camera.frustumRight = camera.frustumTop * canvasAspectRatio );
            }
            else {
                // our screen has a narrower aspect ration than our starting setup (width-limited)
                camera.frustumLeft = -( camera.frustumRight = initialFrustumRight );
                camera.frustumBottom = -( camera.frustumTop = camera.frustumRight / canvasAspectRatio );
            }

            camera.onFrustumChange();
        }
    }
}
