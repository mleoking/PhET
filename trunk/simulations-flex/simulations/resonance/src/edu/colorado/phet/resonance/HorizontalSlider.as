//horizontal slider component

package edu.colorado.phet.resonance {
import flash.display.*;
import flash.events.*;
import flash.text.*;

public class HorizontalSlider extends Sprite {

    //private var owner:Object;		//container of this slider
    private var action: Function;	//function passed in from Object containing this slider
    private var lengthInPix: int; 	//length of slider in pixels
    private var minVal: Number;		//minimum output value of slider
    private var maxVal: Number;		//maximum value
    private var detented: Boolean;	//false if slider continuous
    private var nbrTics: int;		//number of tick marks, if detented
    private var rail: Sprite;		//rail along which knob slides
    private var knob: Sprite;		//grabbable knob on slider
    private var outputValue: Number;
    private var label_txt: TextField;	//static label
    private var tFormat1: TextFormat;	//format of label
    private var tFormat2: TextFormat;	//format of readout
    private var readout_txt: TextField; 	//dynamic readout
    private var scale: Number;			//readout = scale * turnNbr
    private var readoutDecimal: int;		//number of figures past decimal point in readout

    public function HorizontalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, detented: Boolean = false, nbrTics: int = 0 ) {
        //this.owner = owner;
        this.action = action;
        this.lengthInPix = lengthInPix;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.scale = 1;
        this.readoutDecimal = 3;
        this.detented = detented;
        this.nbrTics = nbrTics;
        this.rail = new Sprite();
        this.knob = new Sprite();
        this.drawSlider();
        this.addChild( this.rail );
        this.addChild( this.knob );
        this.createLabel();
        this.createReadoutField();
        this.makeKnobGrabbable();
    }//end of constructor

    public function getVal(): Number {
        return this.outputValue;
    }

    public function setVal( val: Number ): void {
        var xVal: Number = val / this.scale;
        if ( xVal >= this.minVal && xVal <= this.maxVal ) {
            this.outputValue = xVal;
            this.knob.x = this.lengthInPix * (xVal - this.minVal) / (this.maxVal - this.minVal);
            this.action();
            this.updateReadout();
        }
    }//end setVal

    public function setScale( scale: Number ): void {
        this.scale = scale;
    }

    public function setReadoutPrecision( nbrOfPlaces: int ): void {
        this.readoutDecimal = nbrOfPlaces;
    }

    private function drawSlider(): void {
        var gR: Graphics = this.rail.graphics;
        gR.clear();
        gR.lineStyle( 1.5, 0x000000, 1, true );
        gR.moveTo( 0, 0 );
        gR.beginFill( 0xffffff );
        gR.drawRect( 0, -2, this.lengthInPix, 4 );
        gR.endFill();
        var gK: Graphics = this.knob.graphics;
        gK.clear();
        gK.lineStyle( 1.5, 0x0000ff, 1, true );
        gK.beginFill( 0x00ff00 );
        var kW: Number = 6; //knob width
        var kH: Number = 14; //knob height
        //gK.drawRoundRect(-kW/2, -kH/2, kW, kH, 3);
        gK.drawRect( -kW / 2, -kH / 2, kW, kH );
        gK.endFill();
        if ( this.detented ) {
            //draw tic marks
            gR.lineStyle( 1.5, 0x000000, 1, false, LineScaleMode.NORMAL, CapsStyle.NONE );
            for ( var i: int = 0; i < this.nbrTics; i++ ) {
                var delPix: Number = this.lengthInPix / (this.nbrTics - 1);
                gR.moveTo( i * delPix, 3 );
                gR.lineTo( i * delPix, 8.5 );
            }//end for
        }//end if
        //trace("drawSlider called." + this.lengthInPix);
    }//end drawSlider()

    private function createLabel(): void {
        this.label_txt = new TextField();	//static label
        this.addChild( this.label_txt );
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        this.label_txt.text = "Label";
        this.tFormat1 = new TextFormat();	//format of label
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 17;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = 0;// -0.5*this.label_txt.width;
        this.label_txt.y = 0.4 * this.knob.height;
    }//end createLabel()

    public function setLabelText( label_str: String ): void {
        this.label_txt.text = label_str;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = this.rail.width / 2 - 0.5 * this.label_txt.width;
    }

    private function createReadoutField(): void {
        this.readout_txt = new TextField();	//static label
        this.addChild( this.readout_txt );
        this.readout_txt.selectable = true;
        this.readout_txt.type = TextFieldType.INPUT;
        this.readout_txt..border = true;
        this.readout_txt.background = true;
        this.readout_txt.backgroundColor = 0xffffff;
        this.readout_txt.autoSize = TextFieldAutoSize.LEFT;
        this.readout_txt.restrict = "0-9.";

        this.tFormat2 = new TextFormat();	//format of label
        this.tFormat2.font = "Arial";
        this.tFormat2.color = 0x000000;
        this.tFormat2.size = 18;
        this.readout_txt.defaultTextFormat = this.tFormat2;
        this.readout_txt.text = "0.00";
        this.readout_txt.width = 20;
        this.readout_txt.x = this.rail.width / 2 - this.readout_txt.width / 2;
        this.readout_txt.y = -1.5 * this.readout_txt.height;

    }//end createReadoutfield()

    private function updateReadout(): void {
        var readout: Number = this.scale * this.outputValue;
        this.readout_txt.text = readout.toFixed( this.readoutDecimal );
    }//end updateReadout()

    private function makeKnobGrabbable(): void {
        this.knob.buttonMode = true;
        this.knob.addEventListener( MouseEvent.MOUSE_DOWN, grabKnob );
        var thisSlider: Object = this;
        var thisKnob: Object = this.knob;
        var delPix: Number = this.lengthInPix / (this.nbrTics - 1);

        function grabKnob( evt: MouseEvent ): void {
            ;
            stage.addEventListener( MouseEvent.MOUSE_UP, releaseKnob );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, moveKnob );
        }

        function releaseKnob( evt: MouseEvent ): void {
            stage.removeEventListener( MouseEvent.MOUSE_UP, releaseKnob );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, moveKnob );
        }

        function moveKnob( evt: MouseEvent ): void {
            //trace("HorizSlider.mouseX = "+mouseX);
            var knobX: Number;
            var maxX: int = thisKnob.parent.lengthInPix;
            if ( mouseX < 0 ) {
                knobX = 0;
            } else if ( mouseX > maxX ) {
                knobX = maxX;
            }
            else {
                knobX = mouseX
            }
            if ( !thisSlider.detented ) {
                thisKnob.x = knobX;
                thisSlider.outputValue = thisSlider.minVal + (thisSlider.maxVal - thisSlider.minVal) * thisKnob.x / thisSlider.lengthInPix;
                thisSlider.action();
            }
            else {
                //set knob to nearest detented position
                thisKnob.x = delPix * Math.round( knobX / delPix );
                var newVal: Number = thisSlider.minVal + Math.round( (thisSlider.maxVal - thisSlider.minVal) * thisKnob.x / thisSlider.lengthInPix );
                //action only if output is changed
                if ( newVal != thisSlider.outputValue ) {
                    thisSlider.outputValue = newVal;
                    thisSlider.action();
                }
            }//end else

            thisSlider.updateReadout();
            //trace("thisSlider.outputValue " + thisSlider.outputValue);
            evt.updateAfterEvent();
        }
    }//end makeKnobGrabbable()

}//end of class
}//end of package