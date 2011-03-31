//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.HoverCamera3D;
import away3d.containers.Scene3D;
import away3d.containers.View3D;
import away3d.core.render.IRenderer;
import away3d.core.render.Renderer;
import away3d.lights.DirectionalLight3D;

import flash.display.Stage;

/**
 * Contains a 3D scene with its own camera. Responsible for the 3D rendering
 */
public class Away3DViewport {

    //engine variables
    private var _scene: Scene3D;
    private var _camera: HoverCamera3D;
    private var _renderer: IRenderer;
    private var _view: View3D;

    public function Away3DViewport() {
    }

    public function get view(): View3D {
        return _view;
    }

    public function get scene(): Scene3D {
        return _scene;
    }

    public function get camera(): HoverCamera3D {
        return _camera;
    }

    public function get renderer(): IRenderer {
        return _renderer;
    }

    public function updateDisplayList( unscaledWidth: Number, unscaledHeight: Number ): void {
        if ( view != null ) {
            view.x = unscaledWidth / 2;
            view.y = unscaledHeight / 2;
        }
    }

    public function initEngine(): void {
        _scene = new Scene3D();

        _camera = new HoverCamera3D( { distance: 1600, mintiltangle: 0, maxtitlangle: 90 } );
        camera.targetpanangle = camera.panangle = 180;
        camera.targettiltangle = camera.tiltangle = 8;
        camera.hover();

        // alternative renderers
        //        renderer = Renderer.BASIC;
        //        renderer = Renderer.CORRECT_Z_ORDER;
        //        renderer = new QuadrantRenderer();
        _renderer = Renderer.INTERSECTING_OBJECTS;

        _view = new View3D( {scene:scene, camera:camera, renderer:renderer} );
    }

    public function addLight(): void {
        var light: DirectionalLight3D = new DirectionalLight3D( {color:0xFFFFFF, ambient:0.2, diffuse:0.75, specular:0.1} );
        light.x = 10000;
        light.z = -35000;
        light.y = 50000;
        scene.addChild( light );
    }

    public function onResize( stage: Stage ): void {
        // center the view
        view.x = stage.stageWidth / 2;
        view.y = stage.stageHeight / 2;

        // zoom in/out to match the size of the stage
        camera.zoom = Math.min( stage.stageWidth / 100, stage.stageHeight / 65 );
    }
}
}