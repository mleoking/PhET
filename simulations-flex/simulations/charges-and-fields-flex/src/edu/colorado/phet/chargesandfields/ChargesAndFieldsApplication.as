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
import mx.controls.TextInput;
import mx.controls.Button;
import mx.core.UIComponent;

public class ChargesAndFieldsApplication extends UIComponent {

    public static const EFAC : Number = 0.2046; // E-field conversion factor: E_true = E_model*EFAC
    public static const VFAC : Number = 1.917E-3; // Voltage conversion factor: V_true = V_model*VFAC

    [Bindable]
    public var display : ChargesAndFieldsDisplay;

    public function ChargesAndFieldsApplication() {
           this.addEventListener(Event.ADDED_TO_STAGE, init);
    }

    public function init(evt : Event) : void {

        display = new ChargesAndFieldsDisplay(stage);
        this.addChild(display);

        stage.scaleMode = StageScaleMode.NO_SCALE;
        stage.align = StageAlign.TOP_LEFT;
        stage.addEventListener(Event.RESIZE, display.onResize);

        /*
        var button : Button = new Button();

        button.label = "boo";

        this.addChild( button );

        trace(button.x);
        trace(button.y);
        */

        
    }

    public function addPlusCharge() : void {
        display.addPlusCharge();
    }

    public function addMinusCharge() : void {
        display.addMinusCharge();
    }

    public function add20() : void {
        display.add20();
    }
}
}
