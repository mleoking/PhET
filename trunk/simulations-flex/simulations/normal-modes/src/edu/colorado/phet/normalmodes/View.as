package edu.colorado.phet.normalmodes {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.display.*;
import flash.events.*;
import flash.filters.*;
import flash.geom.*;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class View extends Sprite {

    public var myMainView: MainView;		//MainView
    private var model: Model;			//model of shaker bar system


    public var pixPerMeter: Number;		//scale: number of pixels in 1 meter
    private var barPixPerResonator: Number; //number of pixels along bar per Resonator
    private var label_txt: TextField;
    private var label_fmt: TextFormat;
    private var stageW: int;
    private var stageH: int;

    //strings for internationalization
    public var driver_str: String;


    public function View( myMainView: MainView, model: Model ) {
        this.myMainView = myMainView;
        this.model = model;
        this.model.registerView( this );
        this.initialize();
    }//end of constructor

    //add "piston driver" label

    public function initialize(): void {

        this.initializeStrings();

        this.stageW = Util.STAGEW;
        this.stageH = Util.STAGEH;
        this.pixPerMeter = 800;
        this.createLabel();
        //NiceButton2(myButtonWidth:Number, myButtonHeight:Number, labelText:String, buttonFunction:Function)

        //RotaryKnob(action:Function, knobDiameter:Number, knobColor:Number, minTurns:Number, maxTurns:Number)

        //HorizontalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, textEditable:Boolean = false, detented: Boolean = false, nbrTics: int = 0 )



        //this.addChild( this.springHolder );
        //this.addChild( this.bar );
//        this.addChild( this.base );
//        this.addChild( this.ruler );
//        this.ruler.x = - barPixPerResonator*maxNbrResonators/2 - this.ruler.ruler.width; //-this.base.width/2;
//        this.ruler.y = -this.ruler.height;

        //this.makeObjectGrabbable();

        this.initializeControls();
    }//end of initialize()

    private function initializeStrings(): void {
       // driver_str = FlexSimStrings.get("driver", "DRIVER"); //"DRIVER";

    }

    private function createLabel(): void {
        this.label_txt = new TextField();	//static label
        this.addChild( this.label_txt );
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        this.label_txt.text = driver_str;
        this.label_fmt = new TextFormat();	//format of label
        this.label_fmt.font = "Arial";
        this.label_fmt.color = 0xffffff;
        this.label_fmt.size = 18;
        this.label_txt.setTextFormat( this.label_fmt );
        //this.label_txt.x = -0.5 * this.label_txt.width;
        //this.label_txt.y = 1.1 * this.knobRadius;
    }//end createLabel()

    public function initializeControls(): void {
        //trace("initializeShakerControls() called");

        this.update();

    }




//    private function drawOnLight( color: Number ): void {
//        var gOL: Graphics = this.onLight.graphics;
//        gOL.clear();
//        gOL.lineStyle( 0, 0x5555ff, 1, true, LineScaleMode.NONE );
//        var radius: Number = 8;
//        gOL.beginFill( color );
//        gOL.drawCircle( 0, 0, radius );
//        gOL.endFill();
//        //draw specular highlight
//        gOL.lineStyle( 0, 0xffffff, 1, true, LineScaleMode.NONE );
//        gOL.beginFill( 0xdd9999 );
//        gOL.drawCircle( 0.3 * radius, -0.3 * radius, 1 );
//        gOL.endFill();
//        this.onLight.filters = [this.glow];
//        if ( color == 0x000000 ) {
//            this.onLight.filters = [];
//        }
//        else {
//            this.onLight.filters = [this.glow];
//        }
//    }//end drawLight





    private function makeSpriteGrabbable( mySprite:Sprite): void {
        //this.bar.buttonMode = true;
        var target = mySprite;
        var thisObject: Object = this;
        var wasRunning: Boolean;
        //var L0inPix:Number = this.orientation*this.pixPerMeter * this.model.getL0();
        //var D0inPix:Number = this.orientation*this.L0InPix;
        target.buttonMode = true;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            if ( !thisObject.model.paused ) {
                clickOffset = new Point( evt.localX, evt.localY );
                wasRunning = thisObject.model.getRunning();
                thisObject.model.stopShaker();
                stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
                stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            }
            //problem with localX, localY if sprite is rotated.
            //trace("evt.target.y: "+evt.target.y);
            //thisObject.drawOnLight();
            //thisObject.spring.scaleY *= 1.5;
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            if ( wasRunning ) {thisObject.model.startShaker();}
            //thisObject.drawOnLight();
        }

        function dragTarget( evt: MouseEvent ): void {
            var barYInPix = mouseY - clickOffset.y;
            var barYInMeters = -barYInPix / thisObject.pixPerMeter;
            var limit: Number = 0.05;
            if ( barYInMeters > limit ) {
                //trace("bar high");
                barYInMeters = limit;
            } else if ( barYInMeters < -limit ) {
                barYInMeters = -limit;
                //trace("bar low");
            }
            thisObject.model.setY0( barYInMeters );
            //trace("ShakerView.makeBar..  barYInMeters = " + barYInMeters);
            //trace("evt.localY = "+evt.localY);
            //trace("mouseY = "+mouseY);
            evt.updateAfterEvent();
        }//end of dragTarget()

    }

    public function update(): void {

    }//end update()

}//end of class

}//end of package