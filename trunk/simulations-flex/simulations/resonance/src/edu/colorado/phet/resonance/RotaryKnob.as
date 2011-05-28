//rotary knob component.
package edu.colorado.phet.resonance {
import flash.display.*;
import flash.events.*;
import flash.text.*;

public class RotaryKnob extends Sprite {

    private var action: Function;	//function performed by this knob, passed in from container
    private var knobGraphic: Sprite;
    private var knobShadow: Sprite
    private var knobDiameter: int;  //knob diameter in pixels
    private var knobRadius: int;
    private var knobColor: Number;
    private var outputAngle: Number;	//angle turned in degrees
    private var outputTurns: Number;    //angle turned in revs
    private var maxTurns: Number;	    //maximum allowed angle in revs
    private var minTurns: Number;	    //minimum allowed angle in revs
    private var label_txt: TextField;	//static label
    private var tFormat1: TextFormat;	//format of label
    private var tFormat2: TextFormat;	//format of readout
    //private var label_str:String;	//label string
    private var units_str:String;       //units on the readout
    private var readout_txt: TextField; //dynamic readout
    private var units_txt: TextField;   //textField to display units
    private var scale: Number;		    //readout = scale * turnNbr
    private var decimalPlaces: int;		//number of figures past decimal point in readout
    private var manualUpdating;         //true if user entering value in textField by keyboard


    public function RotaryKnob( action: Function, knobDiameter: Number, knobColor: Number, minTurns: Number, maxTurns: Number ) {
        this.action = action;
        this.knobGraphic = new Sprite();
        this.knobShadow = new Sprite();
        this.addChild(this.knobShadow);
        this.addChild( this.knobGraphic );
        this.knobDiameter = knobDiameter; //60;
        this.knobRadius = this.knobDiameter / 2;
        this.outputAngle = 0;
        this.outputTurns = this.outputAngle / 360;
        this.knobColor = knobColor; //0x00cc00;
        this.maxTurns = maxTurns; //5;
        this.minTurns = minTurns; //0;
        this.scale = 1;
        this.decimalPlaces = 2;
        this.manualUpdating = false;
        this.drawShadow();
        this.drawKnob();
        this.createLabel();
        this.createReadoutField();
        this.makeKnobTurnable();
    }//end of constructor

    public function getTurns(): Number {
        return this.outputTurns;
    }

    public function setTurns( turnNbr: Number ): void {
        this.outputTurns = turnNbr;
        this.outputAngle = this.outputTurns * 360;
        this.knobGraphic.rotation = this.outputAngle;
        if( !manualUpdating ){
           this.updateReadout();
        }
        this.action();
    }

    public function setMaxTurns( maxTurns: Number ): void {
        this.maxTurns = maxTurns;
    }

    public function setMinTurns( minTurns: Number ): void {
        this.minTurns = minTurns;
    }

    public function setScale( scale: Number ): void {
        this.scale = scale;
    }

    public function setVal( val: Number ): void {
        var turnsVal: Number = val;  /// this.scale;
        if ( turnsVal >= this.minTurns && turnsVal <= this.maxTurns ) {
            this.outputTurns = turnsVal;
            this.setTurns( this.outputTurns );
        }else if(turnsVal > this.maxTurns){
				this.outputTurns  = this.maxTurns;
				this.knobGraphic.rotation = this.maxTurns*360;
				this.updateReadout();
			}else if(turnsVal < this.minTurns){
				this.outputTurns  = this.minTurns;
				this.knobGraphic.rotation = this.minTurns*360;
			}
        this.action();
        this.updateReadout();
    }//end setVal

    private function drawShadow():void{
       var g: Graphics = this.knobShadow.graphics;
       var r:Number = this.knobRadius;
       var rO:Number = 1.25*r;
       var rads:Number;
       with(g){
           clear();
           lineStyle(0, 0x888888, 1, false);
           beginFill(0x888888, 0.5);
           drawCircle(-0.3*r, 0.5*r, r);
           endFill();
           lineStyle(1.5, 0x333333, 1, true );
           drawCircle(0, 0, rO);
       }
       for(var i:Number = 0; i < 9; i++ ){
            rads = i*0.25*Math.PI;
            g.moveTo(0,0);
            g.lineTo(rO*Math.cos(rads), rO*Math.sin(rads));
        }
    }

    private function drawKnob(): void {
        var g: Graphics = this.knobGraphic.graphics;
        g.clear();
        g.lineStyle( 2, 0x0000ff, 1, false );  //if pixelHinting = true, then circle is lumpy
        g.beginFill( this.knobColor );
        g.drawCircle( 0, 0, this.knobRadius );
        g.endFill();
        g.lineStyle( 4, 0x0000ff, 1, true );
        g.moveTo( 0, 0 );
        g.lineTo( 0, -knobRadius );
    }//end of drawKnob()

    private function createLabel(): void {
        this.label_txt = new TextField();	//static label
        this.addChild( this.label_txt );
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        this.label_txt.text = "Holder";
        this.tFormat1 = new TextFormat();	//format of label
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 15;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = -0.5 * this.label_txt.width;
        this.label_txt.y = 1.2 * this.knobRadius;
    }//end createLabel()

    public function setLabelText( label_str: String ): void {
        this.label_txt.text = label_str;
        //trace("this.tFormat.size = "+this.tFormat.size);
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = -0.5 * this.label_txt.width;
    }

    public function setUnitsText( str:String ):void{
        this.units_str = " " + str;     //extra space between number field and units
    }

    private function createReadoutField(): void {
        this.readout_txt = new TextField();	//static label
        this.addChild( this.readout_txt );
        this.readout_txt.selectable = true;
        this.readout_txt.type = TextFieldType.INPUT;
        this.readout_txt.border = true;
        this.readout_txt.background = true;
        this.readout_txt.backgroundColor = 0xffffff;
        //this.readout_txt.autoSize = TextFieldAutoSize.CENTER;
        this.readout_txt.autoSize = TextFieldAutoSize.RIGHT;
        this.readout_txt.restrict = "0-9.";
        //this.readout_txt.addEventListener( Event.CHANGE, onTextChange );
        this.readout_txt.addEventListener( KeyboardEvent.KEY_DOWN, onHitEnter );
        this.readout_txt.addEventListener( FocusEvent.FOCUS_OUT, onFocusOut ) ;

        this.units_txt = new TextField();   //units displayed next to readout field
        this.addChild( this.units_txt );
        this.units_txt.selectable = false;
        this.units_txt.type =  TextFieldType.DYNAMIC;   //user cannot edit
        this.units_txt.autoSize = TextFieldAutoSize.LEFT;

        this.tFormat2 = new TextFormat();	//format of label
        this.tFormat2.font = "Arial";
        this.tFormat2.color = 0x000000;
        this.tFormat2.size = 14;
        this.tFormat2.align = "right";
        this.readout_txt.defaultTextFormat = this.tFormat2;
        this.readout_txt.text = "0.00";
        this.units_txt.defaultTextFormat = this.tFormat2;
        this.units_txt.text = " Hz";

        this.readout_txt.width = 35;
        this.readout_txt.height = 22;
        this.units_txt.height = 22;
        this.readout_txt.x = -this.readout_txt.width / 2;
        this.units_txt.x = this.readout_txt.width / 2;
        this.readout_txt.y = -1.5 * this.knobRadius - this.readout_txt.height;
        this.units_txt.y = -1.5 * this.knobRadius - this.units_txt.height;
    }//end createReadoutfield()

    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        this.manualUpdating = true;
        if(keyEvt.keyCode == 13){       //13 is keyCode for Enter key
           var inputText:String  = this.readout_txt.text;
           var inputNumber:Number = Number(inputText);
           this.setVal( inputNumber / this.scale );
        }
        this.manualUpdating = false;
    }

    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        this.manualUpdating = true;
        var inputText:String  = this.readout_txt.text;
        var inputNumber:Number = Number(inputText);
        this.setVal( inputNumber / this.scale );
        this.manualUpdating = false;
    }

    //obsolete code
//    private function onTextChange( evt: Event ): void {
//        //trace("HorizontalSlider.onTextChange called. text = "+this.evtTextToNumber(evt));
//        this.manualUpdating = true;
//        this.setVal( this.evtTextToNumber( evt )/this.scale );
//        this.manualUpdating = false;
//    }

    //obsolete code
//    private function evtTextToNumber( evt: Event ): Number {
//        var inputText = evt.target.text;
//        var outputNumber: Number;
//        if ( inputText == "." ) {
//            evt.target.text = "0.";
//            evt.target.setSelection( 2, 2 ); //sets cursor at end of line
//            outputNumber = 0;
//        } else if ( inputText == "-" ) {
//            outputNumber = 0;
//        } else if ( inputText == "-." ) {
//            evt.target.text = "-0.";
//            evt.target.setSelection( 3, 3 ); //sets cursor at end of line
//            outputNumber = 0;
//        } else if ( isNaN( Number( inputText ) ) ) {
//            evt.target.text = "0";
//            outputNumber = 0;
//        }
//        else {
//            outputNumber = Number( inputText );
//        }
//        return outputNumber;
//    }//end textToNumber

    private function updateReadout(): void {
        var readout: Number = this.scale * this.outputTurns;
        // displays default precision if readout is slider-selected,
        // if readout is hand-entered, displays between default precision and upto 4 decimal places
        var roundedReadout:Number = Math.floor( readout );
        var decimalPortion:Number = readout - roundedReadout;
        var factor:Number = 1000000;
        var decimalPortion = Math.round(decimalPortion * factor)/factor;   //necessary because of bug in AS3 arithmetic. Example: 1.16 - 1 = 0.160000000000019
        var decimal_str:String = decimalPortion.toString();
        var nbrDecimalPlaces:Number = decimal_str.length - 2;
        var readoutPlaces = this.decimalPlaces;
        //trace("RotaryKnob.updateReadout(), readoutPlaces = " + readoutPlaces);
        if(nbrDecimalPlaces > this.decimalPlaces){
            readoutPlaces = nbrDecimalPlaces;
            if(nbrDecimalPlaces > 4){
                readoutPlaces = 4;   //limits display to 5 places past decimal point
            }
        } else if(nbrDecimalPlaces < this.decimalPlaces){
            readoutPlaces = this.decimalPlaces;
        }
        this.readout_txt.text = " " + readout.toFixed( readoutPlaces );
    }//end updateReadout()

    private function makeKnobTurnable(): void {
        var thisObject: Object = this;
        this.knobGraphic.buttonMode = true;
        var initAngle: Number;  //initial angle in degrees
        var positiveAngle: Number;
        var positiveInitAngle: Number;
        this.knobGraphic.addEventListener( MouseEvent.MOUSE_DOWN, startKnobTurn );
        function startKnobTurn( evt: MouseEvent ): void {
            initAngle = Math.atan2( mouseY, mouseX ) * 180 / Math.PI;
            if ( initAngle < 0 ) {
                positiveInitAngle = initAngle + 360;
            }
            else {
                positiveInitAngle = initAngle;
            }
            stage.addEventListener( MouseEvent.MOUSE_UP, stopKnobTurn );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, turnKnob );
        }

        function stopKnobTurn( evt: MouseEvent ): void {
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopKnobTurn );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, turnKnob );
            //thisObject.model.startMotion();
        }

        function turnKnob( evt: MouseEvent ): void {
            var angle: Number = Math.atan2( mouseY, mouseX ) * 180 / Math.PI;
            var delAngle: Number;
            //trace("mouseX "+mouseX);
            //trace("angle = " + angle);
            delAngle = angle - initAngle
            thisObject.knobGraphic.rotation += delAngle;
            if ( Math.abs( delAngle ) > 180 ) {
                if ( delAngle < 0 ) {
                    delAngle += 360;
                }
                else {
                    delAngle -= 360;
                }
            }
            thisObject.outputAngle += delAngle;
            if ( thisObject.outputAngle > thisObject.maxTurns * 360 ) {
                thisObject.outputAngle = thisObject.maxTurns * 360;
                thisObject.knobGraphic.rotation = thisObject.outputAngle;
            } else if ( thisObject.outputAngle < thisObject.minTurns * 360 ) {
                thisObject.outputAngle = thisObject.minTurns * 360;
                thisObject.knobGraphic.rotation = thisObject.outputAngle;
            }
            thisObject.outputTurns = thisObject.outputAngle / 360;
            //round output value so that readout value is exact, according to decimal precision set by decimalPlaces
            var factor:Number = thisObject.scale * Math.pow( 10, thisObject.decimalPlaces );
            thisObject.outputTurns = Math.round( thisObject.outputTurns*factor )/factor;
            thisObject.updateReadout();
            initAngle = angle;
            thisObject.action();
            evt.updateAfterEvent();
        }//end of turnKnob()
    }//end makeKnobTurnable()

}//end of class

}//end of package