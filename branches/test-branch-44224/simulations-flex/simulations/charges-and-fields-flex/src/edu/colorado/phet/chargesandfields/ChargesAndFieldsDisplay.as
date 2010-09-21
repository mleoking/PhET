package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;
import flash.display.Stage;
import flash.events.Event;

import mx.core.Application;
import mx.core.UIComponent;
import mx.events.SliderEvent;

public class ChargesAndFieldsDisplay extends UIComponent {
    private var myWidth : Number;
    private var myHeight : Number;

    public var model : Model;

    private var background : BackgroundSprite;

    [Bindable]
    public var mosaic : VoltageMosaic;

    private var charges : Array = new Array();

    public function ChargesAndFieldsDisplay( tempStage : Stage ) {
        
        this.tabEnabled = true;
        this.tabIndex = -1;

        myWidth = tempStage.stageWidth;
        myHeight = tempStage.stageHeight;

        model = new Model();

        background = new BackgroundSprite(myWidth, myHeight);
        addChild(background);

        mosaic = new VoltageMosaic(model, myWidth, myHeight);
        addChild(mosaic);

        for ( var i : uint = 0; i < 1; i++ ) {
            var charge : Charge;
            if ( i % 2 == 0 ) {
                charge = new MinusCharge(mosaic);
            }
            else {
                charge = new PlusCharge(mosaic);
            }
            Application.application.addChild(charge);
            charges.push(charge);
            model.addCharge(charge);
            charge.setDisplayPosition(Math.random() * myWidth, Math.random() * myHeight);
        }

        mosaic.draw();

    }

    public function onResize( evt : Event ) : void {
        myWidth = this.stage.stageWidth;
        myHeight = this.stage.stageHeight;
        background.changeSize(myWidth, myHeight);
        mosaic.changeSize(myWidth, myHeight);
    }

    public function clearCharges() : void {
        for ( var i : int = 0; i < charges.length; i++ ) {
            var charge : Charge = charges[i];
            Application.application.removeChild(charge);
            model.removeCharge(charge);
        }
        charges = new Array();

        mosaic.draw();
    }

    public function addCharge( charge : Charge ) : void {
        Application.application.addChild(charge);
        charges.push(charge);
        model.addCharge(charge);
    }

    public function scatterCharge( charge : Charge ) : void {
        charge.setDisplayPosition(Math.random() * myWidth, Math.random() * myHeight);
    }

    public function addRandomCharges( quantity : int ) : void {
        for ( var i : int = 0; i < quantity; i++ ) {
            var charge : Charge;

            if ( Math.random() < 0.5 ) {
                charge = new PlusCharge(mosaic);
            }
            else {
                charge = new MinusCharge(mosaic);
            }

            addCharge(charge);
            scatterCharge(charge);
        }

        mosaic.draw();
    }

    public function addRandomCharge() : void {
        addRandomCharges(1);
    }

    public function add20() : void {
        addRandomCharges(20);
    }

    public function addPlusCharge() : void {
        var charge : Charge = new PlusCharge(mosaic);
        addCharge(charge);
        scatterCharge(charge);
        mosaic.draw();
    }

    public function addMinusCharge() : void {
        var charge : Charge = new MinusCharge(mosaic);
        addCharge(charge);
        scatterCharge(charge);
        mosaic.draw();
    }

    public function setKScaled( event : SliderEvent ) : void {
        Model.setKScaled(event);
        mosaic.draw();
    }
}
}
