//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.view.away3d {
import away3d.cameras.Camera3D;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.components.BlockLabelSprite;
import edu.colorado.phet.densityandbuoyancy.view.*;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

//REVIEW If it's a sprite, then why is the class name *Node?
/**
 * This is a flash sprite that displays text for a block, and is positioned using Away3D geometry.
 */
public class Away3DBlockLabelSprite extends BlockLabelSprite {
    private var mainViewport: Away3DViewport;
    private var mainCamera: Camera3D;
    private var cubeNode: CubeObject3D;
    private var canvas: AbstractDensityAndBuoyancyPlayAreaComponent;

    public function Away3DBlockLabelSprite( name: String, cubeNode: CubeObject3D, visibilityProperty: BooleanProperty, canvas: AbstractDensityAndBuoyancyPlayAreaComponent, mainCamera: Camera3D, mainViewport: Away3DViewport ) {
        this.canvas = canvas;
        super( name, visibilityProperty );
        this.mainViewport = mainViewport;
        this.cubeNode = cubeNode;
        this.mainCamera = mainCamera;
        cubeNode.getDensityObject().getYProperty().addListener( updateGraphics );
        cubeNode.getDensityObject().getXProperty().addListener( updateGraphics );
        cubeNode.getDensityObject().getVolumeProperty().addListener( updateGraphics );
        cubeNode.frontZProperty.addListener( updateGraphics );
        canvas.addRenderListener( updateGraphics );//have to updateGraphics immediately after render, since that is when the screen function gets updated
    }

    override protected function updateGraphics(): void {
        super.updateGraphics();
        if ( canvas.renderedOnce ) {
            try {
                var cube: PickableCube = cubeNode.getCube();
                var screenVertex: ScreenVertex = mainCamera.screen( cube, cube.vertices[4] );//top left of front face of cube, TODO: less brittle way to encode vertex?

                this.x = screenVertex.x + mainViewport.view.x + 5;
                this.y = screenVertex.y + mainViewport.view.y + 5;
            }
            catch( e: * ) {
                //null pointer exception before camera is used to render the screen once
                // note that if the scene hasn't been rendered with this density object, it will work next time (not this time)
                trace( "e=" + e );
            }
        }
    }
}
}
