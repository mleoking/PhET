package edu.colorado.phet.chargesandfields {

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.display.Stage
import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;
import flash.text.TextField;
import flash.ui.Keyboard;
import flash.utils.Timer;
import flash.events.TimerEvent;

public class Charge extends Sprite {
    public var q : Number = 0;

    public var modelX : Number;
    public var modelY : Number;

    private var mosaic : VoltageMosaic;

    public var dragging : Boolean;

    private var timer : Timer;

    public function Charge(mosaic : VoltageMosaic) {

        this.mosaic = mosaic;
        dragging = false;

        timer = new Timer( 50 );

        timer.addEventListener( TimerEvent.TIMER, onTick );

        // make it appear hand-like
        this.useHandCursor = true;
		this.buttonMode = true;

        addEventListener(MouseEvent.MOUSE_DOWN, mouseDown);
		addEventListener(MouseEvent.MOUSE_UP, mouseUp);
    }

    public function setDisplayPosition(x : Number, y : Number) : void {
        this.x = x;
        this.y = y;

        displayPositionToModel();
    }

    private function displayPositionToModel() : void {
        // TODO: integrate in scale. if we find this in the GUI
        modelX = x;
        modelY = y;
    }

    public function mouseDown(evt : MouseEvent) : void {
        startDrag();
        timer.start();
    }

    public function mouseUp(evt : MouseEvent) : void {
        timer.stop();
        stopDrag();
        displayPositionToModel();
        mosaic.draw();
    }

    public function onTick( event : TimerEvent ) : void {
        //trace("tick");
        displayPositionToModel();
        mosaic.draw();
    }
}
}