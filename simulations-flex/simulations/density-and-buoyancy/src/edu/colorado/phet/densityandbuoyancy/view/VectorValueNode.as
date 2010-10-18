package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class VectorValueNode extends Sprite {
    private var mainViewport: Away3DViewport;
    private var textField: TextField;
    private var mainCamera: Camera3D;
    private var arrowNode: ArrowNode;

    public function VectorValueNode( mainCamera: Camera3D, arrowNode: ArrowNode, mainViewport: Away3DViewport, visibilityProperty: BooleanProperty ) {
        this.mainViewport = mainViewport;
        this.mainCamera = mainCamera;
        this.arrowNode = arrowNode;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.text = "";
        textField.selectable = false;
        addChild( textField );
        mouseEnabled = false;
        mouseChildren = false;
        arrowNode.arrowModel.addListener( updateGraphics );
        updateGraphics();
        const updateVisibility: Function = function(): void {
            visible = arrowNode.visibilityProperty.value && Number( getValueText() ) != 0 && visibilityProperty.value;
        };
        arrowNode.visibilityProperty.addListener( updateVisibility );
        arrowNode.arrowModel.addListener( updateVisibility );
        visibilityProperty.addListener( updateVisibility );
    }

    private function updateGraphics(): void {
        graphics.clear();
        //Convert SI to cm^3
        textField.text = FlexSimStrings.get( "properties.vectorValue", "{0} N", [getValueText()] );
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 20;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );

        graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( 0xFFFFFF );
        graphics.drawRoundRect( textField.x, textField.y, textField.width, textField.height, 6, 6 );
        graphics.endFill();

        try {
            var screenVertex: ScreenVertex = mainCamera.screen( arrowNode, arrowNode.tip );
            this.x = screenVertex.x + mainViewport.view.x - textField.width / 2;
            this.y = screenVertex.y + mainViewport.view.y + (arrowNode.arrowModel.y > 0 ? -textField.height * 1.2 : textField.height * 0.2);
        }
        catch( e: * ) {
            //null pointer exception before camera is used to render the screen once
        }
    }

    private function getValueText(): String {
        return String( DensityConstants.format( arrowNode.arrowModel.getMagnitude() ) );
    }
}
}