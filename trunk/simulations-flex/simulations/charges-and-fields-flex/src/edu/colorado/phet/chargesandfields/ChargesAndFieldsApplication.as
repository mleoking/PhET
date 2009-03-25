package edu.colorado.phet.chargesandfields {

import edu.colorado.phet.flexcommon.FlexCommonComponent;

import flash.display.StageAlign;
import flash.display.StageScaleMode;
import flash.events.Event;

import flash.ui.ContextMenu;
import mx.controls.Button;
import mx.core.UIComponent;

public class ChargesAndFieldsApplication extends UIComponent {

    public static const EFAC : Number = 0.2046; // E-field conversion factor: E_true = E_model*EFAC
    public static const VFAC : Number = 1.917E-3; // Voltage conversion factor: V_true = V_model*VFAC

    [Bindable]
    public var display : ChargesAndFieldsDisplay;

    public function ChargesAndFieldsApplication() {
        this.addEventListener(Event.ADDED_TO_STAGE, init);

        super();

        this.tabEnabled = true;
        this.tabIndex = -1;
    }

    public function init( evt : Event ) : void {

        display = new ChargesAndFieldsDisplay(stage);
        this.addChild(display);

        stage.scaleMode = StageScaleMode.NO_SCALE;
        //stage.scaleMode = StageScaleMode.SHOW_ALL;
        stage.align = StageAlign.TOP_LEFT;
        stage.addEventListener(Event.RESIZE, display.onResize);

        stage.showDefaultContextMenu = true;

        this.addChild(new FlexCommonComponent());

        var button : Button = new Button();
        button.label = "boo";
        button.x = 200;
        button.y = 200;
        this.addChild(button);


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
