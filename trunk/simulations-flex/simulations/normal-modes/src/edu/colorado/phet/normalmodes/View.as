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

import org.aswing.plaf.basic.border.ColorChooserBorder;

public class View extends Sprite {

    public var myMainView: MainView;		//MainView
    private var myModel: Model;			    //model of shaker bar system


    private var _pixPerMeter: Number;		    //scale: number of pixels in 1 meter
    private var LinMeters:Number;           //distance between fixed walls in meters
    private var LinPix:Number;              //distance between fixed walls in pixels
    private var leftEdgeY:Number;           //y-position of leftEdge in pixels measured down from top of screen
    private var _leftEdgeX:Number;           //x-position of leftEdge in pixels measured right from left edge of screen
    private var mass_arr:Array;             //array of mass sprites , index 0 = mobile mass 1
    private var spring_arr:Array;           //array of spring sprites
    private var walls:Sprite;

    //private var label_txt: TextField;
    //private var label_fmt: TextFormat;
    private var stageW: int;
    private var stageH: int;

    //strings for internationalization
    public var any_str: String;


    public function View( myMainView: MainView, myModel: Model ) {
        this.myMainView = myMainView;
        this.myModel = myModel;
        this.myModel.registerView( this );

        this.initialize();
    }//end of constructor


    public function initialize(): void {
        this.initializeStrings();
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.LinMeters =  this.myModel.L;
        this.LinPix = 0.8*this.stageW;
        this._pixPerMeter = this.LinPix/this.LinMeters;
        this._leftEdgeX = 0.1*this.stageW;
        this.leftEdgeY = 0.4*this.stageH;
        var nMax:int = this.myModel.nMax;
        this.mass_arr = new Array( nMax );
        this.spring_arr = new Array( nMax );
        for(var i:int =0; i < nMax; i++){
            this.mass_arr[i] = new MassView( i+1, this.myModel, this );
            this.spring_arr[i] = new Sprite();
        }
        this.walls = new Sprite();

        //trace("View.leftEdgeY = "+this.leftEdgeY);
        //this.createGraphics();
        this.drawWalls();
        this.positionGraphics();

        for(var i:int = 0; i < this.myModel.nMax; i++){
            this.addChild(this.mass_arr[i])
        }
        this.addChild(this.walls);
        //this.createLabel();
        //NiceButton2(myButtonWidth:Number, myButtonHeight:Number, labelText:String, buttonFunction:Function)

        //RotaryKnob(action:Function, knobDiameter:Number, knobColor:Number, minTurns:Number, maxTurns:Number)

        //HorizontalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, textEditable:Boolean = false, detented: Boolean = false, nbrTics: int = 0 )

//        this.addChild( this.ruler );
//        this.ruler.x = - barPixPerResonator*maxNbrResonators/2 - this.ruler.ruler.width; //-this.base.width/2;
//        this.ruler.y = -this.ruler.height;

        //this.makeObjectGrabbable();

        this.initializeControls();
    }//end of initialize()

    private function initializeStrings(): void {
       // driver_str = FlexSimStrings.get("driver", "DRIVER"); //"DRIVER";

    }

    private function createGraphics():void{


        this.drawWalls();
    }

    private function drawWalls():void{
        var g:Graphics = this.walls.graphics;
        var h:Number = 100;  //height of wall in pix
        g.clear();
        g.lineStyle(5, 0x444444, 1);   //gray walls
        g.moveTo(this._leftEdgeX, this.leftEdgeY - h/2);
        g.lineTo(this._leftEdgeX, this.leftEdgeY + h/2);
        g.moveTo(this._leftEdgeX + this.LinPix, this.leftEdgeY - h/2);
        g.lineTo(this._leftEdgeX + this.LinPix, this.leftEdgeY + h/2);
    }

    private function positionGraphics():void{
       var N:int = this.myModel.N;   //number of visible masses
       var separationInPix:Number = this.LinPix/(N + 1);   //center-to-center separation of mobile masses in chain
       for(var i:int = 0; i < N; i++){
           this.mass_arr[i].visible = true;
           this.mass_arr[i].y = this.leftEdgeY;
           this.mass_arr[i].x = this._leftEdgeX + (1+i)*separationInPix;
       }
    }

//    private function createLabel(): void {
//        this.label_txt = new TextField();	//static label
//        this.addChild( this.label_txt );
//        this.label_txt.selectable = false;
//        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
//        this.label_txt.text = any_str;
//        this.label_fmt = new TextFormat();	//format of label
//        this.label_fmt.font = "Arial";
//        this.label_fmt.color = 0xffffff;
//        this.label_fmt.size = 18;
//        this.label_txt.setTextFormat( this.label_fmt );
//        //this.label_txt.x = -0.5 * this.label_txt.width;
//        //this.label_txt.y = 1.1 * this.knobRadius;
//    }//end createLabel()

    public function initializeControls(): void {
        //trace("initializeShakerControls() called");

        this.update();
    }

    public function get pixPerMeter(){
        return this._pixPerMeter;
    }

    public function get leftEdgeX(){
        return this._leftEdgeX;
    }

    public function update(): void {
        for(var j:int = 0; j < this.myModel.N; j++){
            var i:int = j+1;    //index of mobile mass, left mass = 1
            var xInMeters:Number = this.myModel.getX(i);
            var xInPix:Number = this._leftEdgeX + xInMeters*this._pixPerMeter;
            this.mass_arr[j].x = xInPix;
        }//end for loop
    }//end update()

}//end of class

}//end of package