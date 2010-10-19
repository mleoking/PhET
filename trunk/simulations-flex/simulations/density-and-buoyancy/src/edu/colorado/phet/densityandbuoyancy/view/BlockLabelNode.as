package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.core.base.Vertex;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class BlockLabelNode extends Sprite {
    private var mainViewport: Away3DViewport;
    private var textField: TextField;
    private var mainCamera: Camera3D;
    private var densityObjectNode: DensityObjectNode;

    public function BlockLabelNode( name: String, densityObjectNode: DensityObjectNode, mainCamera: Camera3D, mainViewport: Away3DViewport, visibilityProperty: BooleanProperty ) {
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
        densityObjectNode.frontZProperty.addListener( updateGraphics );
        updateGraphics();
        const updateVisibility: Function = function(): void {
            visible = visibilityProperty.value;
        };
        visibilityProperty.addListener( updateVisibility );
        updateVisibility();
    }

    private function updateGraphics(): void {
        graphics.clear();
        //Convert SI to cm^3
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 20;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );

        graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( 0xFFFFFF );
        graphics.drawRoundRect( textField.x, textField.y, textField.width, textField.height, 6, 6 );
        graphics.endFill();

        try {
            var vertex = new Vertex( densityObjectNode.center[0].x, densityObjectNode.center[0].y, densityObjectNode.frontZProperty.value );
            var screenVertex: ScreenVertex = mainCamera.screen( densityObjectNode, densityObjectNode.center[0] );

            this.x = screenVertex.x + mainViewport.view.x - textField.width / 2;
            this.y = screenVertex.y + mainViewport.view.y - textField.height / 2;
        }
        catch( e: * ) {
            //null pointer exception before camera is used to render the screen once
        }
    }
}
}