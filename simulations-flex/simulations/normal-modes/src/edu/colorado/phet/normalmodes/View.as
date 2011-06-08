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

import org.aswing.ext.FormRow;

import org.aswing.plaf.basic.border.ColorChooserBorder;

public class View extends Sprite {

    public var myMainView: MainView;		//MainView
    private var myModel: Model;			    //model of shaker bar system


    private var _pixPerMeter: Number;		    //scale: number of pixels in 1 meter
    private var LinMeters:Number;           //distance between fixed walls in meters
    private var LinPix:Number;              //distance between fixed walls in pixels
    private var L0Spring;                   //equilibrium length of spring in pixels
    private var _leftEdgeY:Number;           //y-position of leftEdge in pixels measured down from top of screen
    private var _leftEdgeX:Number;          //x-position of leftEdge in pixels measured right from left edge of screen
    private var mass_arr:Array;             //array of massView instances , index 0 = mobile mass 1
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
        this._leftEdgeY = 0.4*this.stageH;
        var nMax:int = this.myModel.nMax;
        this.mass_arr = new Array( nMax );
        this.spring_arr = new Array( nMax + 1 );  //one more spring than masses
        this.walls = new Sprite();
        this.drawWalls();
        for(var i:int =0; i <= nMax; i++){       //notice one more spring than nbr masses
            this.spring_arr[i] = new Sprite();
        }
        this.drawSprings();       //need to positions springs behind masses
        //mass graphic drawn in MassView
        for(var i:int =0; i < nMax; i++){
            this.mass_arr[i] = new MassView( i+1, this.myModel, this );
        }

        this.positionGraphics();

        for(var i:int = 0; i <= this.myModel.nMax; i++){
            this.addChild(this.spring_arr[i]);
        }
        for(var i:int = 0; i < this.myModel.nMax; i++){
            this.addChild(this.mass_arr[i]);
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


    private function drawSprings():void{        //only the visible springs are drawn
        //var d:Number = 10;                      //radius of each mass in pixels, all distance in this function in pixels
        var nMasses:Number = this.myModel.N;    //number of masses in chain
        this.L0Spring = ( this.LinPix )/(nMasses + 1);  //equilibrium length of single spring in pixels
        var leadL:Number = 20;                  //length of each straight end of spring
        var nTurns:Number = 5;                  //number of turns in spring
        var w:Number = (this.L0Spring - 2*leadL)/nTurns;   //width of each turn
        var r:Number = 10;                      //radius of each turn
        for(var i:int = 0; i <= nMasses; i++){
           var g:Graphics = this.spring_arr[i].graphics;
           g.clear();
           g.lineStyle( 3, 0xff0000, 1 );
           g.moveTo( 0, 0 );
           g.lineTo(leadL, 0);
            for(var j:int = 0; j < nTurns; j++){
                g.lineTo( leadL + j*w + w/4, r );
                g.lineTo( leadL + j*w + 3*w/4, -r);
                g.lineTo( leadL + j*w + w,  0 );
            }//end for j
            g.lineTo( this.L0Spring, 0 );
         }//end for i
        //to make sure that other springs are invisible
        for(var i:int = nMasses + 1; i <= this.myModel.nMax; i++ ){
            this.spring_arr[i].visible = false;
        }
    }//end drawSprings()

    private function drawWalls():void{
        var g:Graphics = this.walls.graphics;
        var h:Number = 100;  //height of wall in pix
        g.clear();
        g.lineStyle(5, 0x444444, 1);   //gray walls
        g.moveTo(this._leftEdgeX, this._leftEdgeY - h/2);
        g.lineTo(this._leftEdgeX, this._leftEdgeY + h/2);
        g.moveTo(this._leftEdgeX + this.LinPix, this._leftEdgeY - h/2);
        g.lineTo(this._leftEdgeX + this.LinPix, this._leftEdgeY + h/2);
    }

    private function positionGraphics():void{
        var N:int = this.myModel.N;   //number of visible masses
        var nMax:int = this.myModel.nMax;
        var separationInPix:Number = this.LinPix/(N + 1);   //center-to-center separation of mobile masses in chain
        var d:Number = 10;     //radius of each mass in pix
        for(var i:int = 0; i < N; i++){
            this.mass_arr[i].visible = true;
            this.mass_arr[i].y = this._leftEdgeY;
            this.mass_arr[i].x = this._leftEdgeX + (1+i)*separationInPix;
        }
        for (var i:int = 0; i <= N; i++ ){
            this.spring_arr[i].visible = true;
            this.spring_arr[i].y = this._leftEdgeY;
            this.spring_arr[i].x = this._leftEdgeX + i*separationInPix;
        }
        for(var i:int = N; i < nMax; i++){
            this.mass_arr[i].visible = false;
        }
        for( var i:int = N+1; i <= nMax; i ++ ){
            this.spring_arr[i].visible = false;
        }
    }

    public function setNbrMasses():void{
        this.drawSprings();
        this.positionGraphics();
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

    public function get leftEdgeX():Number{
        return this._leftEdgeX;
    }

    public function get leftEdgeY():Number{
        return this._leftEdgeY;
    }

    public function update(): void {
        var xInMeters:Number;
        var yInMeters:Number;
        var xInPix:Number;
        var yInPix:Number;
        var springLengthInPix:Number;
        var scale:Number;
        //position masses
        for(var j:int = 0; j < this.myModel.N; j++){
            var i:int = j+1;    //index of mobile mass, left mass = 1
            xInMeters = this.myModel.getX(i);       //irrelevant when in transverse mode
            yInMeters = this.myModel.getY(i);       //irrelevant when in longitudinal mode
            xInPix = this._leftEdgeX + xInMeters*this._pixPerMeter;
            yInPix = this._leftEdgeY -  yInMeters*this._pixPerMeter;   //don't forget. +y direction is down in screen coords, is up in cartesian coords
            this.mass_arr[j].x = xInPix;
            this.mass_arr[j].y = yInPix;
        }//end for loop

        //position springs
        for(var i:int = 0; i <= this.myModel.N; i++){
            //position left end of spring
            xInMeters = this.myModel.getX(i);
            xInPix = this._leftEdgeX + xInMeters*this._pixPerMeter;
            yInMeters = this.myModel.getY(i);
            yInPix = this._leftEdgeY - yInMeters*this._pixPerMeter;
            this.spring_arr[i].x = xInPix;
            this.spring_arr[i].y = yInPix;
            //position right end of spring; when in transverse mode, this requires rotation
            if(this.myModel.longitudinalMode){
                this.spring_arr[i].rotation = 0;
                springLengthInPix =  (this.myModel.getX(i+1)-this.myModel.getX(i))*this.pixPerMeter;
                scale = springLengthInPix/this.L0Spring;
                this.spring_arr[i].scaleX = scale;
            }else{
                var sprLX:Number = (this.myModel.getX(i+1)-this.myModel.getX(i))*this.pixPerMeter;
                var sprLY:Number = (this.myModel.getY(i+1)-this.myModel.getY(i))*this.pixPerMeter;
                springLengthInPix = Math.sqrt(sprLX*sprLX + sprLY*sprLY);
                scale = springLengthInPix/this.L0Spring;
                this.spring_arr[i].scaleX = scale;
                //set rotation of stretched spring
                var angleInDeg:Number = (Math.atan2(-sprLY, sprLX))*180/Math.PI;
                this.spring_arr[i].rotation = angleInDeg;
            }
        }//end for loop

    }//end update()

}//end of class

}//end of package