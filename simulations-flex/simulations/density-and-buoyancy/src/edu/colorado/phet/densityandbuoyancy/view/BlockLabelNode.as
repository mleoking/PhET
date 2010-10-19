package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.view.away3d.CubeNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.PickableCube;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class BlockLabelNode extends Sprite {
    private var mainViewport: Away3DViewport;
    private var textField: TextField;
    private var mainCamera: Camera3D;
    private var densityObjectNode: DensityObjectNode;

    public function BlockLabelNode( canvas: AbstractDBCanvas, name: String, densityObjectNode: DensityObjectNode, mainCamera: Camera3D, mainViewport: Away3DViewport, visibilityProperty: BooleanProperty ) {
        this.densityObjectNode = densityObjectNode;
        this.mainViewport = mainViewport;
        this.mainCamera = mainCamera;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.text = name;
        textField.selectable = false;
        addChild( textField );
        mouseEnabled = false;
        mouseChildren = false;
        densityObjectNode.getDensityObject().getYProperty().addListener( updateGraphics );
        densityObjectNode.getDensityObject().getXProperty().addListener( updateGraphics );
        densityObjectNode.getDensityObject().getVolumeProperty().addListener( updateGraphics );
        densityObjectNode.frontZProperty.addListener( updateGraphics );
        visibilityProperty.addListener( updateGraphics );
        canvas.addRenderListener( updateGraphics );//have to updateGraphics immediately after render, since that is when the screen function gets updated
        updateGraphics();
        const updateVisibility: Function = function(): void {
            visible = visibilityProperty.value;
        };
        visibilityProperty.addListener( updateVisibility );
        updateVisibility();
    }

    private function updateGraphics(): void {
        graphics.clear();
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 20;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );

        graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( 0xFFFFFF );
        graphics.drawRoundRect( textField.x, textField.y, textField.width, textField.height, 6, 6 );
        graphics.endFill();

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