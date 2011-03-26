//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import away3d.cameras.Camera3D;
import away3d.core.base.Vertex;
import away3d.core.draw.ScreenVertex;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

/**
 * Displays a numeric readout of magnitude next to a force vector
 */
public class VectorValueNode extends Sprite {
    private var mainViewport: Away3DViewport;
    private var textField: TextField;
    private var mainCamera: Camera3D;
    private var arrowNode: ArrowNode;
    private var right: Boolean;

    public function VectorValueNode( mainCamera: Camera3D, arrowNode: ArrowNode, mainViewport: Away3DViewport, visibilityProperty: BooleanProperty, right: Boolean ) {
        this.mainViewport = mainViewport;
        this.mainCamera = mainCamera;
        this.arrowNode = arrowNode;
        this.right = right;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.text = "";
        textField.selectable = false;
        textField.textColor = arrowNode.color;
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
        updateVisibility();
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
            if ( right && arrowNode.arrowModel.y > 0 ) {
                updateLocation( arrowNode.arrowHeadRightCornerVertex, 0 );
            }
            else if ( right && arrowNode.arrowModel.y <= 0 ) {
                updateLocation( arrowNode.arrowHeadLeftCornerVertex, 0 );
            }
            else if ( !right && arrowNode.arrowModel.y > 0 ) {
                updateLocation( arrowNode.arrowHeadLeftCornerVertex, -textField.width );
            }
            else if ( !right && arrowNode.arrowModel.y <= 0 ) {
                updateLocation( arrowNode.arrowHeadRightCornerVertex, -textField.width );
            }
            else {//shouldn't happen
                x = 0;
                y = 0;
            }
        }
        catch( e: * ) {
            //null pointer exception before camera is used to render the screen once
        }
    }

    private function updateLocation( vertex: Vertex, offsetX: Number ): void {
        var screenVertex: ScreenVertex = mainCamera.screen( arrowNode, vertex );
        x = screenVertex.x + mainViewport.view.x + offsetX;
        y = screenVertex.y + mainViewport.view.y - textField.height / 2;
    }

    private function getValueText(): String {
        return String( DensityAndBuoyancyConstants.format( arrowNode.arrowModel.getMagnitude() ) );
    }
}
}