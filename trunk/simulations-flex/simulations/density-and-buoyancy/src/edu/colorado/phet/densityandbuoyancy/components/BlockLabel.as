//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

/**
 * Displays the block name in custom object control panel. Should look like the label that appears on the block.
 * This is currently used only in Buoyancy when there are two custom objects.
 */
public class BlockLabel extends Sprite {
    private var textField: TextField;

    public function BlockLabel( name: String, visibilityProperty: BooleanProperty ) {
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.text = name;
        textField.selectable = false;
        addChild( textField );
        mouseEnabled = false;
        mouseChildren = false;
        visibilityProperty.addListener( updateGraphics );
        updateGraphics();
        const updateVisibility: Function = function(): void {
            visible = visibilityProperty.value;
        };
        visibilityProperty.addListener( updateVisibility );
        updateVisibility();//no auto-callback in addListener, so we update it ourselves
    }

    protected function updateGraphics(): void {
        graphics.clear();

        //Add the text
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 20;
        textFormat.bold = true;
        textField.setTextFormat( textFormat );

        //Adds a border
        graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( 0xFFFFFF );
        graphics.drawRoundRect( textField.x, textField.y, textField.width, textField.height, 6, 6 );
        graphics.endFill();
    }
}
}