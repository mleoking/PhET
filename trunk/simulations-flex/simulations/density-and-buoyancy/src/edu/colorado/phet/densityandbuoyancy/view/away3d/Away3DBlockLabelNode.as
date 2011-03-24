/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 11/8/10
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import away3d.cameras.Camera3D;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.view.*;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

public class Away3DBlockLabelNode extends BlockLabelNode {
    private var mainViewport: Away3DViewport;
    private var mainCamera: Camera3D;
    private var cubeNode: CubeNode;
    private var canvas: AbstractDBCanvas;

    public function Away3DBlockLabelNode( name: String, cubeNode: CubeNode, visibilityProperty: BooleanProperty, canvas: AbstractDBCanvas, mainCamera: Camera3D, mainViewport: Away3DViewport ) {
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
