// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class JMEView {
    private final ViewPort viewport;
    private final Camera camera;
    private final Node scene;

    public JMEView( ViewPort viewport, Camera camera, Node scene ) {
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
