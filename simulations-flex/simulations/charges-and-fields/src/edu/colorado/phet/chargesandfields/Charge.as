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

public class Charge extends Sprite {
    public var q : Number;

    public var modelX : Number;
    public var modelY : Number;

    public function Charge() {

        // make it appear hand-like
        this.useHandCursor = true;
		this.buttonMode = true;

        addEventListener(MouseEvent.MOUSE_DOWN, mouseDown);
		addEventListener(MouseEvent.MOUSE_UP, mouseUp);
    }

    public function setDisplayPosition(x : Number, y : Number) : void {
        this.x = x;
        this.y = y;

        // TODO: integrate in scale. if we find this in the GUI
        modelX = x;
        modelY = y;
    }

    public function mouseDown(evt : MouseEvent) : void {
        startDrag();
    }

    public function mouseUp(evt : MouseEvent) : void {
        stopDrag();
    }
}
}