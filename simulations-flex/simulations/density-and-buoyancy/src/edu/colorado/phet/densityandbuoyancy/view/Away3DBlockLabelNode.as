/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 11/8/10
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.view.away3d.CubeNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.PickableCube;

public class Away3DBlockLabelNode extends BlockLabelNode {
    private var mainViewport: Away3DViewport;
    private var mainCamera: Camera3D;
    private var densityObjectNode: DensityObjectNode;

    public function Away3DBlockLabelNode( name: String, densityObjectNode: DensityObjectNode, visibilityProperty: BooleanProperty, canvas: AbstractDBCanvas, mainCamera: Camera3D, mainViewport: Away3DViewport ) {
        super( name, visibilityProperty );
        this.mainViewport = mainViewport;
        this.densityObjectNode = densityObjectNode;
        this.mainCamera = mainCamera;
        densityObjectNode.getDensityObject().getYProperty().addListener( updateGraphics );
        densityObjectNode.getDensityObject().getXProperty().addListener( updateGraphics );
        densityObjectNode.getDensityObject().getVolumeProperty().addListener( updateGraphics );
        densityObjectNode.frontZProperty.addListener( updateGraphics );
        canvas.addRenderListener( updateGraphics );//have to updateGraphics immediately after render, since that is when the screen function gets updated
    }

    override protected function updateGraphics(): void {
        super.updateGraphics();
        try {
            var cubeNode: CubeNode = CubeNode( densityObjectNode );
            var cube: PickableCube = cubeNode.getCube();
            var screenVertex: ScreenVertex = mainCamera.screen( cube, cube.vertices[4] );//top left of front face of cube, TODO: less brittle way to encode vertex?

            this.x = screenVertex.x + mainViewport.view.x + 5;
            this.y = screenVertex.y + mainViewport.view.y + 5;
        }
        catch( e: * ) {
            //null pointer exception before camera is used to render the screen once
            trace( "e=" + e );
        }
    }
}
}
