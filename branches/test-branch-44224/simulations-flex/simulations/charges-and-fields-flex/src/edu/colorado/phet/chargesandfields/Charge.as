package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.ui.Keyboard;
import flash.utils.Timer;

import mx.core.Application;
import mx.core.UIComponent;
import mx.managers.IFocusManagerComponent;

public class Charge extends UIComponent implements IFocusManagerComponent {
    public var q : Number = 0;

    public var modelX : Number;
    public var modelY : Number;

    private var mosaic : VoltageMosaic;

    public var dragging : Boolean;

    private var timer : Timer;

    private var focusIsEnabled : Boolean = true;

    private static var tabNumber : int = 5000;

    protected var chargeMC : Sprite;

    private var highlightSprite : Sprite;

    public var keyboardStep : Number = 5;

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

        drawHighlight();
    }

    public function drawHighlight() : void {

        var highlightRadius : Number = 15;
        var highlightAlpha : Number = 0.8;

        highlightSprite = new Sprite();
        highlightSprite.visible = false;
        addChildAt( highlightSprite, 0 );

        // dark area
        highlightSprite.graphics.beginFill( 0x000000, highlightAlpha );
        highlightSprite.graphics.moveTo( 0, 0 );
        highlightSprite.graphics.lineTo( 0, -highlightRadius );
        highlightSprite.graphics.curveTo( -highlightRadius, -highlightRadius, -highlightRadius, 0 );
        highlightSprite.graphics.lineTo( highlightRadius, 0 );
        highlightSprite.graphics.curveTo( highlightRadius, highlightRadius, 0, highlightRadius );
        highlightSprite.graphics.endFill();

        // light area
        highlightSprite.graphics.beginFill( 0xFFFFFF, highlightAlpha );
        highlightSprite.graphics.moveTo( 0, 0 );
        highlightSprite.graphics.lineTo( 0, highlightRadius );
        highlightSprite.graphics.curveTo( -highlightRadius, highlightRadius, -highlightRadius, 0 );
        highlightSprite.graphics.lineTo( highlightRadius, 0 );
        highlightSprite.graphics.curveTo( highlightRadius, -highlightRadius, 0, -highlightRadius );
        highlightSprite.graphics.endFill();
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




    /*
    public function get focusEnabled():Boolean {
        return focusIsEnabled;
    }

    public function set focusEnabled( value:Boolean ):void {
        focusIsEnabled = value;
    }

    public function setFocus():void {
    }
    */

    public override function get mouseFocusEnabled():Boolean {
        return true;
    }

    public function keyCallback( event : KeyboardEvent ) : void {
        //trace( event.keyCode );

        switch( event.keyCode ) {
            case Keyboard.LEFT:
                this.x -= keyboardStep;
                break;
            case Keyboard.UP:
                this.y -= keyboardStep;
                break;
            case Keyboard.RIGHT:
                this.x += keyboardStep;
                break;
            case Keyboard.DOWN:
                this.y += keyboardStep;
                break;
            default:
                return;
        }

        displayPositionToModel();
        mosaic.draw();
    }

    public override function drawFocus( isFocused:Boolean ):void {
        if( isFocused ) {
            //chargeMC.alpha = 0.5;
            Application.application.addEventListener( KeyboardEvent.KEY_DOWN, keyCallback );
            highlightSprite.visible = true;
        } else {
            //chargeMC.alpha = 1;
            Application.application.removeEventListener( KeyboardEvent.KEY_DOWN, keyCallback );
            highlightSprite.visible = false;
        }
    }

}
}