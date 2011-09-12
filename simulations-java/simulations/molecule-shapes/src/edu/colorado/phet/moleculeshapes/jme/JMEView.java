// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
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
}
