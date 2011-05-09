/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/7/10
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flexsimexample {
import flash.events.Event;

import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.Alert;
import mx.controls.Button;
import mx.controls.HSlider;

public class FlexSimExampleCanvas extends Canvas {
    public function FlexSimExampleCanvas() {
    }

    private var RENDER_WIDTH: int = 400;
    private var RENDER_HEIGHT: int = 300;

    public function init(): void {
        setStyle( "backgroundColor", 0xffff99 );//same color as build an atom
        percentWidth = 100;
        percentHeight = 100;

        var controlPanel:VBox = new VBox();
        controlPanel.setStyle("borderStyle", "solid")
        controlPanel.setStyle("borderColor", 0xff0000);
        controlPanel.setStyle("cornerRadius", 10);
        controlPanel.setStyle("borderThickness", 8);
        controlPanel.setStyle("paddingTop", 8);
        controlPanel.setStyle("paddingBottom", 8);
        controlPanel.setStyle("paddingRight", 4);
        controlPanel.setStyle("paddingLeft", 4);

        var button1:Button = new Button();
        var slider1:HSlider = new HSlider();
        button1.label = " Press me! Press me! Press me!  NOW!!! ";
        button1.buttonMode = true;
        controlPanel.addChild(button1);

        controlPanel.addChild(slider1);

        this.addChild(controlPanel);

        button1.addEventListener(MouseEvent.CLICK, onButtonPress);
        
        const listener: Function = function( event: Event ): void {
            const sx:Number = stage.stageWidth / RENDER_WIDTH;
            const sy:Number = stage.stageHeight / RENDER_HEIGHT;
            var s:Number = Math.min(sx,sy);
            controlPanel.scaleX = s;
            controlPanel.scaleY = s;
        };
        stage.addEventListener( Event.RESIZE, listener );
        listener( null );
    }

    private function onButtonPress(evt:MouseEvent):void {
        trace("button1 pressed");
        //addChild(new HSlider());
    }
}
}
