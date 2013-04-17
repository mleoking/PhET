/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/9/11
 * Time: 5:39 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.CapsStyle;
import flash.display.Graphics;
import flash.display.JointStyle;
import flash.display.LineScaleMode;
import flash.display.Sprite;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Rectangle;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFieldType;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;


public class VerticalSlider extends Sprite{

    //private var owner:Object;		    //container of this slider
    private var action: Function;	    //function passed in from Object containing this slider
    private var _index:int;              //optional integer index labeling the slider
    private var lengthInPix: int; 	    //length of slider in pixels
    private var minVal: Number;		    //minimum output value of slider
    private var maxVal: Number;		    //maximum value
    private var textEditable: Boolean;  //true if readout is user editable
    private var detented: Boolean;	    //false if slider continuous
    private var nbrTics: int;		    //number of tick marks, if detented
    private var readoutShown:Boolean;   //false if readout not shown
    private var rail: Sprite;		    //rail along which knob slides
    private var knob: Sprite;		    //grabbable knob on slider
    private var outputValue: Number;    //readout = scale * outputValue; scale is often 1.0, but you might want outputVal in meters and readoutVal in cm
    private var label_txt: TextField;	//static label
    private var readout_txt: TextField; //dynamic readout
    private var units_txt:TextField;    //units displayed next to readout
    private var tFormat1: TextFormat;	//format of label
    private var tFormat2: TextFormat;	//format of readout
    private var units_str:String;       //units on readout
    private var scale: Number;			//readout = scale * outputValue; scale is usually 1.0
    private var decimalPlaces: int;		//number of figures past decimal point in readout
    private var manualUpdating: Boolean;	//true if user is manually entering text in readout textfield

    public function VerticalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, textEditable:Boolean = false, detented: Boolean = false, nbrTics: int = 0 , readoutShown:Boolean = true ) {
        //this.owner = owner;
        this.action = action;
        this.lengthInPix = lengthInPix;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.scale = 1;		//default is that slider outputValue = readout value
        this.decimalPlaces = 1;
        this.textEditable = textEditable;
        this.detented = detented;
        this.nbrTics = nbrTics;
        this.readoutShown = readoutShown;
        this.rail = new Sprite();
        this.knob = new Sprite();
        this.drawSlider();
        this.addChild( this.rail );
        this.addChild( this.knob );
        //this.knob.x = this.knob.width;
        this.createReadoutFields();        //order important: must create readout field first, then label, since label positioning depends on height of readout field
        this.createLabel();
        this.makeKnobGrabbable();
        //this.switchLabelAndReadoutPositions();
        //this.drawBorder();  //for testing only
    }//end of constructor



//****Getters and Setters****

    public function getVal(): Number {
        return this.outputValue;
    }

    public function setVal( val: Number ): void {
        var yVal: Number = val;
        //trace("HorizSlider.setVal val = "+val);
        if ( yVal >= this.minVal && yVal <= this.maxVal ) {
            this.outputValue = yVal;
            this.knob.y = this.lengthInPix *(1 - (yVal - this.minVal) / (this.maxVal - this.minVal));
        }else if( yVal > this.maxVal ){
				this.outputValue = this.maxVal;
				this.knob.y = 0;
				this.updateReadout();
			}else if(yVal < this.minVal){
				this.outputValue = this.minVal;
				this.knob.y = this.lengthInPix;
			}
        this.action( this._index );
        this.updateReadout();
    }//end setVal

    public function set index( i:int ):void{
        this._index = i;
    }

    public function get index():int{
        return this._index;
    }

    public function setScale( scale: Number ): void {
        this.scale = scale;
    }

    public function setLabelFontSize( size:Number, bold:Boolean = false ):void{
        this.tFormat1.size = size;
        this.tFormat1.bold = bold;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = - 0.5 * this.label_txt.width;
    }


    public function setReadoutPrecision( nbrOfPlaces: int ): void {
        this.decimalPlaces = nbrOfPlaces;     //nbr of places displayed past the decimal point
    }

    public function setLabelText( label_str: String ): void {
        this.label_txt.text = label_str;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = - 0.5 * this.label_txt.width;
    }

    public function killLabel():void{
        this.removeChild( this.label_txt );
    }


    public function setUnitsText( str:String ):void{
        this.units_str = str;
        this.units_txt.text = " " + this.units_str;
    }

 //****PUBLIC FUNCTIONS****

    public function setSliderWithoutAction(val:Number):void{
         var yVal: Number = val;
        //trace("HorizSlider.setVal val = "+val);
        if ( yVal >= this.minVal && yVal <= this.maxVal ) {
            this.outputValue = yVal;
            this.knob.y = this.lengthInPix *(1 - (yVal - this.minVal) / (this.maxVal - this.minVal));
        }else if( yVal > this.maxVal ){
				this.outputValue = this.maxVal;
				this.knob.y = 0;
				this.updateReadout();
			}else if(yVal < this.minVal){
				this.outputValue = this.minVal;
				this.knob.y = this.lengthInPix;
			}
        this.updateReadout();
    }//end setSliderWithoutAction()


    public function switchLabelAndReadoutPositions():void{
       //this.label_txt.y = 0.4 * this.knob.height;
       //this.readout_txt.y = -1.5 * this.readout_txt.height;
       //this.units_txt.y = -1.5 * this.units_txt.height;
       this.label_txt.y = 0.4 * this.knob.height ;
       this.readout_txt.y = -this.label_txt.height - 1.0*this.readout_txt.height;
       this.units_txt.y = -this.label_txt.height - 1.0*this.units_txt.height;
    }

    public function restrictInput( allowedCharacters:String ):void{
        this.readout_txt.restrict = allowedCharacters;
    }

    private function drawSlider(): void {
        //draw rail
        var gR: Graphics = this.rail.graphics;
        gR.clear();
        gR.lineStyle( 1.5, 0x333333, 1, true );
        gR.moveTo( 0, 0 );
        gR.beginFill( 0xbbbbbb );
        gR.drawRoundRect( -2, 0,  3, this.lengthInPix, 3 );
        gR.endFill();
        //draw knob
        var gK: Graphics = this.knob.graphics;
        gK.clear();
        var kW: Number = 15; //knob width
        var kH: Number = 25; //knob height
        with(gK){
            lineStyle( 2, 0x0000ff, 1, true, LineScaleMode.NONE, CapsStyle.ROUND, JointStyle.BEVEL );
            moveTo( 0, 0 );
            beginFill(0x00ff00);
            drawRoundRect(-0.5*kW, -0.5*kH, kW, kH, kW/2 );
            moveTo( -0.5*kW, 0 );
            lineTo( 0.5*kW, 0 );
            endFill();
        }
        if ( this.detented ) {
            //draw tic marks
            gR.lineStyle( 1.5, 0x000000, 1, false, LineScaleMode.NORMAL, CapsStyle.NONE );
            for ( var i: int = 0; i < this.nbrTics; i++ ) {
                var delPix: Number = this.lengthInPix / (this.nbrTics - 1);
                gR.moveTo( 3, i * delPix  );
                gR.lineTo( 8.5, i * delPix );
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
        this.tFormat1.align = TextFormatAlign.CENTER ;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = 0; //-0.5*this.label_txt.width;

        if( readoutShown ){
            this.label_txt.y = -0.5 * this.knob.height - this.readout_txt.height - this.label_txt.height;
        } else{
            this.label_txt.y = -0.5 * this.knob.height  - this.label_txt.height;
        }
        this.addChild( this.label_txt );
        //this.label_txt.border = true;      //for testing only
    }//end createLabel()

    private function createReadoutFields(): void {
        this.readout_txt = new TextField();	//readout field
        this.units_txt = new TextField();   //units displayed next to readout field
        this.readout_txt.selectable = this.textEditable;
        this.units_txt.selectable = false;
        this.readout_txt.type = TextFieldType.INPUT;    //user-editable
        this.units_txt.type =  TextFieldType.DYNAMIC;   //user cannot edit
        this.readout_txt.border = true;
        this.readout_txt.background = true;
        this.readout_txt.backgroundColor = 0xffffff;     //white background
        this.readout_txt.autoSize = TextFieldAutoSize.RIGHT;
        this.units_txt.autoSize = TextFieldAutoSize.LEFT;
        this.readout_txt.restrict = "0-9.\\-";   //allow any number, positive or negative

        this.tFormat2 = new TextFormat();	//format of label
        this.tFormat2.font = "Arial";
        this.tFormat2.color = 0x000000;
        this.tFormat2.size = 14;
        this.tFormat2.leading = 0;
        this.readout_txt.defaultTextFormat = this.tFormat2;
        this.units_txt.defaultTextFormat = this.tFormat2;
        this.readout_txt.text = " 1.6";   //placeholder only
        this.units_txt.text = "cm";       //placeholder only
        this.readout_txt.width = 60;
        this.readout_txt.x = this.rail.width / 2 - this.readout_txt.width;
        this.readout_txt.y = -this.readout_txt.height - 0.5*knob.height;
        this.units_txt.x = this.rail.width / 2 ;
        this.units_txt.y = -this.readout_txt.height - 0.5*knob.height;
        if(this.readoutShown){
            this.addChild( this.readout_txt );
            this.addChild( this.units_txt );
        }
        this.readout_txt.addEventListener( KeyboardEvent.KEY_DOWN, onHitEnter );
        this.readout_txt.addEventListener( FocusEvent.FOCUS_OUT, onFocusOut)
    }//end createReadoutfield()

    //update readout when user hits Enter
    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        this.manualUpdating = true;
        if(keyEvt.keyCode == 13){       //13 is keyCode for Enter key
           var inputText:String  = this.readout_txt.text;
           var inputNumber:Number = Number(inputText);
           this.setVal( inputNumber / this.scale );
        }
        this.manualUpdating = false;
    }//end onHitEnter

    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        var inputText:String  = this.readout_txt.text;
        var inputNumber:Number = Number(inputText);
        this.setVal( inputNumber / this.scale );
    }


    private function updateReadout(): void {
        var readout: Number = this.scale * this.outputValue;
        // displays default precision if readout is slider-selected,
        // if readout is hand-entered, displays between default precision and upto 5 decimal places
        var roundedReadout:Number = Math.floor( readout );
        var decimalPortion:Number = readout - roundedReadout;
        var factor:Number = 1000000;
        decimalPortion = Math.round(decimalPortion * factor)/factor;   //necessary because of bug in AS3 arithmetic. Example: 1.16 - 1 = 0.160000000000019
        var decimal_str:String = decimalPortion.toString();
        var nbrDecimalPlaces:Number = decimal_str.length - 2;
        //trace("The number "+decimalPortion+" has "+nbrDecimalPlaces + " decimal places.")
        var readoutPlaces:int = this.decimalPlaces;
        if(nbrDecimalPlaces > this.decimalPlaces){
            readoutPlaces = nbrDecimalPlaces;
            if(nbrDecimalPlaces > 4){
                readoutPlaces = 4;   //limits display to 4 places past decimal point
            }
        } else if(nbrDecimalPlaces < this.decimalPlaces){
            readoutPlaces = this.decimalPlaces;
        }
        //trace("HorizontalSlider.updateReadout readoutPlaces is "+readoutPlaces);
        this.readout_txt.text = readout.toFixed( readoutPlaces );
    }//end updateReadout()

    private function makeKnobGrabbable(): void {
        this.knob.buttonMode = true;
        this.knob.addEventListener( MouseEvent.MOUSE_DOWN, grabKnob );
        var thisSlider: Object = this;
        var thisKnob: Object = this.knob;
        var delPix: Number = this.lengthInPix / (this.nbrTics - 1);

        function grabKnob( evt: MouseEvent ): void {
            stage.addEventListener( MouseEvent.MOUSE_UP, releaseKnob );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, moveKnob );
        }

        function releaseKnob( evt: MouseEvent ): void {
            stage.removeEventListener( MouseEvent.MOUSE_UP, releaseKnob );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, moveKnob );
        }

        function moveKnob( evt: MouseEvent ): void {
            //trace("HorizSlider.mouseY = "+mouseY);
            var knobY: Number;
            var maxY: int = thisKnob.parent.lengthInPix;
            if ( mouseY < 0 ) {
                knobY = 0;
            } else if ( mouseY > maxY ) {
                knobY = maxY;
            } else {
                knobY = mouseY;
            }
            if ( !thisSlider.detented ) {
                thisKnob.y = knobY;
                //round output value so that readout value is exact, according to decimal precision set by decimalPlaces
                var sliderValue: Number = thisSlider.minVal + (thisSlider.maxVal - thisSlider.minVal) * (thisSlider.lengthInPix - thisKnob.y) / thisSlider.lengthInPix;
                var factor:Number = thisSlider.scale * Math.pow( 10, thisSlider.decimalPlaces );
                thisSlider.outputValue = Math.round( sliderValue*factor )/factor;
                //trace( "HorizontalSlider.outputValue = "+thisSlider.outputValue );
                thisSlider.action( thisSlider._index);
            }
            else {
                //set knob to nearest detented position
                thisKnob.y = delPix * Math.round( knobY / delPix );
                var newVal: Number = thisSlider.minVal + Math.round( (thisSlider.maxVal - thisSlider.minVal) * (thisSlider.lengthInPix - thisKnob.y) / thisSlider.lengthInPix );
                //var factor: Number = Math.pow( 10, thisSlider.decimalPlaces );
               // var newVal:Number = Math.round(newSliderVal * factor ) / factor;
                //action only if output is changed
                if ( newVal != thisSlider.outputValue ) {
                    thisSlider.outputValue = newVal;
                    thisSlider.action( thisSlider._index);
                }
            }//end else

            thisSlider.updateReadout();
            //trace("thisSlider.outputValue " + thisSlider.outputValue);
            evt.updateAfterEvent();
        }
    }//end makeKnobGrabbable()

    //draws border around entire slider, for testing purposes only
    private function drawBorder():void{
        var rect:Rectangle = this.getBounds(this);
        var g:Graphics = this.graphics;
        g.lineStyle(1,0x000000,1);
        g.drawRect(rect.x, rect.y, rect.width, rect.height) ;
    }

} //end class
} //end package
