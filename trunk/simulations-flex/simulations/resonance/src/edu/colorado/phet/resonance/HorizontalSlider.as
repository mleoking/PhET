//horizontal slider component

package edu.colorado.phet.resonance {
import flash.display.*;
import flash.events.*;
import flash.geom.Rectangle;
import flash.text.*;

public class HorizontalSlider extends Sprite {

    //private var owner:Object;		//container of this slider
    private var action: Function;	//function passed in from Object containing this slider
    private var lengthInPix: int; 	//length of slider in pixels
    private var minVal: Number;		//minimum output value of slider
    private var maxVal: Number;		//maximum value
    private var textEditable: Boolean; //true if readout is user editable
    private var detented: Boolean;	//false if slider continuous
    private var nbrTics: int;		//number of tick marks, if detented
    private var rail: Sprite;		//rail along which knob slides
    private var knob: Sprite;		//grabbable knob on slider
    private var outputValue: Number;
    private var label_txt: TextField;	//static label
    private var readout_txt: TextField; //dynamic readout
    private var units_txt:TextField;    //units displayed next to readout
    private var tFormat1: TextFormat;	//format of label
    private var tFormat2: TextFormat;	//format of readout
    private var units_str:String;       //units on readout
    private var scale: Number;			//readout = scale * sliderValue
    private var readoutDecimal: int;		//number of figures past decimal point in readout
    private var manualUpdating: Boolean;	//true if user is manually entering text in readout textfield

    public function HorizontalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, textEditable:Boolean = false, detented: Boolean = false, nbrTics: int = 0 ) {
        //this.owner = owner;
        this.action = action;
        this.lengthInPix = lengthInPix;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.scale = 1;		//default is that slider value = readout value
        this.readoutDecimal = 1;
        this.textEditable = textEditable;
        this.detented = detented;
        this.nbrTics = nbrTics;
        this.rail = new Sprite();
        this.knob = new Sprite();
        this.drawSlider();
        this.addChild( this.rail );
        this.addChild( this.knob );
        this.createLabel();
        this.createReadoutFields();
        this.makeKnobGrabbable();
        //this.drawBorder();  //for testing only
    }//end of constructor

    public function getVal(): Number {
        return this.outputValue;
    }

    public function setVal( val: Number ): void {
        var xVal: Number = val;  /// this.scale;
        //trace("HorizSlider.setVal val = "+val);
        if ( xVal >= this.minVal && xVal <= this.maxVal ) {
            this.outputValue = xVal;
            this.knob.x = this.lengthInPix * (xVal - this.minVal) / (this.maxVal - this.minVal);
        }//else if(xVal > this.maxVal){
//				this.outputValue = this.maxVal;
//				this.knob.x = this.lengthInPix;
//				this.updateReadout();
//			}else if(xVal < this.minVal){
//				this.outputValue = this.minVal;
//				this.knob.x = 0;
//			}
        this.action();
        if ( !manualUpdating ) {
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
        //draw rail
        var gR: Graphics = this.rail.graphics;
        gR.clear();
        gR.lineStyle( 1.5, 0x333333, 1, true );
        gR.moveTo( 0, 0 );
        gR.beginFill( 0xbbbbbb );
        gR.drawRect( 0, -2, this.lengthInPix, 4 );
        gR.endFill();
        //draw knob
        var gK: Graphics = this.knob.graphics;
        gK.clear();

        var kW: Number = 6; //knob width
        var kH: Number = 15; //knob height
        //gK.drawRoundRect(-kW/2, -kH/2, kW, kH, 3);
        with(gK){

            lineStyle( 2, 0x0000ff, 1, true, LineScaleMode.NONE, CapsStyle.ROUND, JointStyle.BEVEL );
            beginFill(0x00ff00);
            drawRect(-0.5*kW, -0.5*kH, kW, kH );
//            moveTo(-0.5*kW, -0.5*kH);
//            lineTo(0.5*kW, -0.5*kH);
//            lineTo(0.5*kW, 0.3*kH);
//            lineStyle( 2, 0x0000cc, 1, true);
//            curveTo(0, kH, -0.5*kW, 0.3*kH );
//            lineTo(-0.5*kW, -0.5*kH);
            endFill();
            //lineStyle( 1.0, 0x000000, 1, true, LineScaleMode.NONE, CapsStyle.ROUND, JointStyle.ROUND );
            //moveTo(0,-0.4*kH);
            //
            // lineTo(0,0.4*kH);
            //lineStyle( 1, 0xffff00, 1, false, LineScaleMode.NONE, CapsStyle.ROUND, JointStyle.ROUND );
            //moveTo(-0.5, -0.1*kH);
            //lineTo(-0.5, 0.5*kH);

        }
        //gK.drawRect( -kW / 2, -kH / 2, kW, kH );
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
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        this.label_txt.text = "Label";
        this.tFormat1 = new TextFormat();	//format of label
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 14;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = 0;// -0.5*this.label_txt.width;
        this.label_txt.y = 0.4 * this.knob.height;
        this.addChild( this.label_txt );
        //this.label_txt.border = true;      //for testing only
    }//end createLabel()

    public function setLabelText( label_str: String ): void {
        this.label_txt.text = label_str;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = this.rail.width / 2 - 0.5 * this.label_txt.width;
    }

    public function setUnitsText( str:String ):void{
        this.units_str = str;
        this.units_txt.text = " " + this.units_str;
    }

    public function restrictInput( allowedCharacters:String ):void{
        this.readout_txt.restrict = allowedCharacters;
    }

    private function createReadoutFields(): void {
        this.readout_txt = new TextField();	//readout field
        this.units_txt = new TextField();   //units displayed next to readout field
        this.readout_txt.selectable = this.textEditable;
        this.units_txt.selectable = false;
        this.readout_txt.type = TextFieldType.INPUT;    //user-editable
        this.units_txt.type =  TextFieldType.DYNAMIC;   //user cannot edit
        this.readout_txt.border = true;
        this.readout_txt.background = true;
        this.readout_txt.backgroundColor = 0xffffff;
        this.readout_txt.autoSize = TextFieldAutoSize.RIGHT;
        this.units_txt.autoSize = TextFieldAutoSize.LEFT;
        //this.readout_txt.restrict = "0-9.";

        this.tFormat2 = new TextFormat();	//format of label
        this.tFormat2.font = "Arial";
        this.tFormat2.color = 0x000000;
        this.tFormat2.size = 14;
        this.readout_txt.defaultTextFormat = this.tFormat2;
        this.units_txt.defaultTextFormat = this.tFormat2;
        this.readout_txt.text = " 1.6";
        this.units_txt.text = "cm";
        this.readout_txt.width = 60;
        this.readout_txt.x = this.rail.width / 2 - this.readout_txt.width;
        this.readout_txt.y = -1.5 * this.readout_txt.height;
        this.units_txt.x = this.rail.width / 2 ;
        this.units_txt.y = -1.5 * this.units_txt.height;
        this.addChild( this.readout_txt );
        this.addChild( this.units_txt );
        this.readout_txt.addEventListener( Event.CHANGE, onTextChange );
    }//end createReadoutfield()

    private function onTextChange( evt: Event ): void {
        //trace("HorizontalSlider.onTextChange called. text = "+this.evtTextToNumber(evt));
        this.manualUpdating = true;
        this.setVal( this.evtTextToNumber( evt ) );
        this.manualUpdating = false;
    }

    //unnecessary if text is not selectable
    private function evtTextToNumber( evt: Event ): Number {
        var inputText = evt.target.text;
        var outputNumber: Number;
        if ( inputText == "." ) {
            evt.target.text = "0.";
            evt.target.setSelection( 2, 2 ); //sets cursor at end of line
            outputNumber = 0;
        } else if ( inputText == "-" ) {
            outputNumber = 0;
        } else if ( inputText == "-." ) {
            evt.target.text = "-0.";
            evt.target.setSelection( 3, 3 ); //sets cursor at end of line
            outputNumber = 0;
        } else if ( isNaN( Number( inputText ) ) ) {
            evt.target.text = "0";
            outputNumber = 0;
        }
        else {
            outputNumber = Number( inputText );
        }
        return outputNumber;
    }//end textToNumber

    private function updateReadout(): void {
        var readout: Number = this.scale * this.outputValue;
        this.readout_txt.text = readout.toFixed( this.readoutDecimal );
//        if(this.textEditable){
//            this.readout_txt.text = readout.toFixed( this.readoutDecimal );
//        }else{
//           this.readout_txt.text = " " + readout.toFixed( this.readoutDecimal ) + " " + units_str ;
//        }

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

    private function drawBorder():void{
        var rect:Rectangle = this.getBounds(this);
        var g:Graphics = this.graphics;
        g.lineStyle(1,0x000000,1);
        g.drawRect(rect.x, rect.y, rect.width, rect.height) ;
//        var delX = this.width;
//        var delY = this.height;
//        g.moveTo(0,0);
//        g.lineTo(delX,0);
//        g.lineTo(delX, delY);
//        g.lineTo(0,delY);
//        g.lineTo(0,0);

    }

}//end of class
}//end of package