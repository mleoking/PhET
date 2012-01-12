// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import edu.colorado.phet.jmephet.input.JMEInputHandler;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;

public class JMEView {
    private final PhetJMEApplication app;
    private final ViewPort viewport;
    private final Camera camera;
    private final Node scene;

    public JMEView( PhetJMEApplication app, ViewPort viewport, Camera camera, Node scene ) {
        // TODO: instead of handling creation here, make hooks in app and handle it mainly from here
        this.app = app;
        this.viewport = viewport;
        this.camera = camera;
        this.scene = scene;
    }

    public ViewPort getViewport() {
        return viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    public Node getScene() {
        return scene;
    }

    public void setVisible( boolean visible ) {
        viewport.setEnabled( visible );
    }

    public boolean isGuiView() {
        return scene.getQueueBucket() == Bucket.Gui;
    }

    public Ray getCameraRay( Vector2f screenPoint ) {
        if ( isGuiView() ) {
            return new Ray( new Vector3f( screenPoint.x, screenPoint.y, 0f ), new Vector3f( 0, 0, 1 ) );
        }
        else {
            Vector3f click3d = camera.getWorldCoordinates( new Vector2f( screenPoint.x, screenPoint.y ), 0f ).clone();
            Vector3f dir = camera.getWorldCoordinates( new Vector2f( screenPoint.x, screenPoint.y ), 1f ).subtractLocal( click3d );
            return new Ray( click3d, dir );
        }
    }

    public Ray getCameraRayUnderCursor( JMEInputHandler inputHandler ) {
        return getCameraRay( inputHandler.getCursorPosition() );
    }

    public CollisionResults hitsUnderPoint( Vector2f screenPoint ) {
        CollisionResults results = new CollisionResults();
        scene.collideWith( getCameraRay( screenPoint ), results );
        return results;
    }

    public CollisionResults hitsUnderCursor( JMEInputHandler inputHandler ) {
        return hitsUnderPoint( inputHandler.getCursorPosition() );
    }
}
