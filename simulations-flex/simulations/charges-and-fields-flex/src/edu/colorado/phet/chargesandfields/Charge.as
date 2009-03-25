package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.managers.IFocusManagerComponent;

public class Charge extends Sprite implements IFocusManagerComponent {
    public var q : Number = 0;

    public var modelX : Number;
    public var modelY : Number;

    private var mosaic : VoltageMosaic;

    public var dragging : Boolean;

    private var timer : Timer;

    private var focusIsEnabled = true;

    private static var tabNumber : int = 5000;

    public function Charge( mosaic : VoltageMosaic ) {

        this.tabEnabled = true;
        //this.tabIndex = tabNumber++;
        this.tabIndex = -1;

        this.mosaic = mosaic;
        dragging = false;

        timer = new Timer(50);

        timer.addEventListener(TimerEvent.TIMER, onTick);

        // make it appear hand-like
        this.useHandCursor = true;
        this.buttonMode = true;

        addEventListener(MouseEvent.MOUSE_DOWN, mouseDown);
        addEventListener(MouseEvent.MOUSE_UP, mouseUp);
    }

    public function setDisplayPosition( x : Number, y : Number ) : void {
        this.x = x;
        this.y = y;

        displayPositionToModel();
    }

    private function displayPositionToModel() : void {
        // TODO: integrate in scale. if we find this in the GUI
        modelX = x;
        modelY = y;
    }

    public function mouseDown( evt : MouseEvent ) : void {
        startDrag();
        timer.start();
    }

    public function mouseUp( evt : MouseEvent ) : void {
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




    
    public function get focusEnabled():Boolean {
        return focusIsEnabled;
    }

    public function set focusEnabled( value:Boolean ):void {
        focusIsEnabled = value;
    }

    public function get mouseFocusEnabled():Boolean {
        return true;
    }

    public function setFocus():void {
    }

    public function drawFocus( isFocused:Boolean ):void {
        // draw something since it has focus!!!
    }
}
}