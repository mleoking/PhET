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

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function init(): void {
        setStyle( "backgroundColor", 0xffff99 );//same color as build an atom
        percentWidth = 100;
        percentHeight = 100;

        var controlPanel:VBox = new VBox();
        var button1:Button = new Button();
        button1.label = "Press me!";
        button1.buttonMode = true;
        controlPanel.addChild(button1);
        this.addChild(controlPanel);

        button1.addEventListener(MouseEvent.CLICK, onButtonPress);
        
        const listener: Function = function( event: Event ): void {
            const sx:Number = stage.stageWidth / RENDER_WIDTH;
            const sy:Number = stage.stageHeight / RENDER_HEIGHT;
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
