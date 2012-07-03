/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/23/12
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.geom.Rectangle;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFieldType;
import flash.text.TextFormat;

public class NiceTextField extends Sprite {
    private var action: Function;	    //function passed in from Object containing this textField
    private var _label_str: String;
    private var editable: Boolean;      //True if textField is editable
    private var readoutWidth: Number;   //width of textField
    private var readoutValue: Number;    //readout = readoutValue; scale is often 1.0, but you might want outputVal in meters and readoutVal in cm
    private var _minVal: Number;
    private var _maxVal: Number;
    private var label_txt: TextField;	//static label
    private var readout_txt: TextField; //dynamic readout
    private var _units_str: String;      //optional units string to add to output field
    //private var units_txt:TextField;    //units displayed next to readout
    private var label_fmt: TextFormat;	//format of label
    private var readout_fmt: TextFormat;	//format of readout
    private var _decimalPlaces: int;		//number of figures past decimal point in readout
    private var manualUpdating: Boolean;	//true if user is manually entering text in readout textfield


    public function NiceTextField( action:Function, labelText:String, minVal:Number,  maxVal:Number,  readoutWidth:Number = 50, editable:Boolean = true, decimalPlaces:int = 1 ) {
        this.action = action;
        this._label_str = labelText;
        this._units_str = "";
        this._minVal = minVal;
        this._maxVal = maxVal;
        this.readoutWidth = readoutWidth;
        this.editable = editable;
        this._decimalPlaces = decimalPlaces;
        this.createLabel();
        this.createReadoutField();
        this.locateFields();

    }//end constructor

    //default: label on left, readout on right
    private function createLabel(): void {
        this.label_txt = new TextField();	//static label
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.RIGHT;
        this.label_txt.text = this._label_str; //"Label";
        this.label_fmt = new TextFormat();	//format of label
        this.label_fmt.font = "Arial";
        this.label_fmt.color = 0x000000;
        this.label_fmt.size = 14;
        this.label_txt.setTextFormat( this.label_fmt );

        this.addChild( this.label_txt );
        //this.label_txt.border = true;      //for testing only
    }//end createLabel()

    private function createReadoutField(): void {
        this.readout_txt = new TextField();	//readout field
        //this.units_txt = new TextField();   //units displayed next to readout field
        this.readout_txt.selectable = this.editable;
        //this.units_txt.selectable = false;
        this.readout_txt.type = TextFieldType.INPUT;    //user-editable
        //this.units_txt.type =  TextFieldType.DYNAMIC;   //user cannot edit
        this.readout_txt.border = true;
        this.readout_txt.background = true;
        this.readout_txt.backgroundColor = 0xffffff;     //white background
        //this.readout_txt.autoSize = TextFieldAutoSize.LEFT;
        //this.units_txt.autoSize = TextFieldAutoSize.LEFT;
        this.readout_txt.restrict = "0-9.\\-";   //allow any number, positive or negative

        this.readout_fmt = new TextFormat();	//format of readout
        this.readout_fmt.font = "Arial";
        this.readout_fmt.color = 0x000000;
        this.readout_fmt.size = 14;
        this.readout_fmt.leading = 0;
        this.readout_txt.defaultTextFormat = this.readout_fmt;
        //this.units_txt.defaultTextFormat = this.tFormat2;
        this.readout_txt.text = " 1.6";     //dummy placeholder
        //this.units_txt.text = "cm";
        this.readout_txt.width = this.readoutWidth;
        this.readout_txt.height = 20;
        //this.units_txt.height = 12;

        //this.units_txt.x = this.rail.width / 2 ;
        //this.units_txt.y = 0.7*knob.height; //-1.5 * this.units_txt.height;
        this.addChild( this.readout_txt );

        //this.readout_txt.addEventListener( Event.CHANGE, onTextChange );
        this.readout_txt.addEventListener( KeyboardEvent.KEY_DOWN, onHitEnter );
        this.readout_txt.addEventListener( FocusEvent.FOCUS_OUT, onFocusOut)
    }//end createReadoutfield()

    private function locateFields():void{
        this.label_txt.x = 0;
        this.label_txt.y = 0;
        this.readout_txt.x = this.label_txt.width ;
        this.readout_txt.y = 0;
    }

    //update readout when user hits Enter
    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        this.manualUpdating = true;
        if(keyEvt.keyCode == 13){       //13 is keyCode for Enter key
            var inputText:String  = this.readout_txt.text;
            var inputNumber:Number = Number(inputText);
            this.action( inputNumber );
        }
        this.manualUpdating = false;
    }

    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        this.manualUpdating = true;
        var inputText:String  = this.readout_txt.text;
        var inputNumber:Number = Number(inputText);
        this.action( inputNumber );
        this.manualUpdating = false;
    }

    public function setVal( value:Number ):void{
        //round value to nbr of decimal places
        var factor:Number = Math.pow( 10, decimalPlaces );
        value = Math.round( value*factor );
        value = value/factor;
        if(value > _maxVal ){
            this.readoutValue = _maxVal;
        }else if (value < _minVal ){
            this.readoutValue = _minVal;
        }else{
            this.readoutValue = value;
        }
        if( !manualUpdating ){
            var readoutString: String = this.readoutValue.toString();
            var zeroString: String = "";
            if( readoutValue%1 == 0 ){
                if( decimalPlaces = 1 ){
                    var zeroString:String = ".0";
                }
            }
            this.readout_txt.text = readoutString + zeroString + _units_str;
        }
    } //end setVal

    private function updateReadout(): void {
        var readout: Number = this.readoutValue;
        // displays default precision if readout is slider-selected,
        // if readout is hand-entered, displays between default precision and upto 5 decimal places
        var roundedReadout:Number = Math.floor( readout );
        var decimalPortion:Number = readout - roundedReadout;
        var factor:Number = 1000000;
        decimalPortion = Math.round(decimalPortion * factor)/factor;   //necessary because of bug in AS3 arithmetic. Example: 1.16 - 1 = 0.160000000000019
        var decimal_str:String = decimalPortion.toString();
        var nbrDecimalPlaces:Number = decimal_str.length - 2;
        //trace("The number "+decimalPortion+" has "+nbrDecimalPlaces + " decimal places.")
        var readoutPlaces:int = this._decimalPlaces;
        if(nbrDecimalPlaces > this._decimalPlaces){
            readoutPlaces = nbrDecimalPlaces;
            if(nbrDecimalPlaces > 4){
                readoutPlaces = 4;   //limits display to 4 places past decimal point
            }
        } else if(nbrDecimalPlaces < this._decimalPlaces){
            readoutPlaces = this._decimalPlaces;
        }
        //trace("HorizontalSlider.updateReadout readoutPlaces is "+readoutPlaces);
        this.readout_txt.text = readout.toFixed( readoutPlaces );
    }//end updateReadout()

    //draws border around entire slider, for testing purposes only
    private function drawBorder():void{
        var rect:Rectangle = this.getBounds(this);
        var g:Graphics = this.graphics;
        g.lineStyle(1,0x000000,1);
        g.drawRect(rect.x, rect.y, rect.width, rect.height) ;
    }//end drawBorder()

    //****Getters and Setters****
    public function get label_str():String {
        return _label_str;
    }

    public function set label_str(value:String):void {
        _label_str = value;
    }

    public function get decimalPlaces():int {
        return _decimalPlaces;
    }

    public function set decimalPlaces( value:int ):void {
        _decimalPlaces = value;
    }

    public function set units_str(value:String):void {
        _units_str = value;
    }
}//end class
}//end package
