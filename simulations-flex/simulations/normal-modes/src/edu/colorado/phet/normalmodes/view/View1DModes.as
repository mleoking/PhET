/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 7/31/11
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.control.ShowHideButton;
import edu.colorado.phet.normalmodes.model.Model1;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

//View of individual 1D Modes. 1 Graph for each mode, with graphs lined up aligned vertically
//Each graph is simple sine wave

public class View1DModes extends Sprite {

    public var myMainView: MainView;		//MainView
    private var myModel1: Model1;			//model for this view
    private var _N: int;                    //current max mode number = nbr of masses
    private var _pixPerMeter: Number;		//scale: number of pixels in 1 meter
    private var _LinMeters: Number;         //wall-to-wall distance in meters
    private var _LinPix: Number;            //wall-to-wall distance in pixels
    private var _ySeparationOfGraphs;       //vertical pixel separation between adjacent graphs

    private var showHideButton:ShowHideButton;
    private var walls_arr:Array;            //array of wall graphics, one for each mode
    private var sineWave_arr:Array;         //array of sine wave graphiscs, one for each mode
    private var nbrLabel_arr:Array;         //array of number labels
    private var normalModes_txt:TextField;  //Label above mode display
    public var normalModes_str:String;
    private var tFormat1:TextFormat;
    private var tFormat2:TextFormat;

    private var stageW: Number;
    private var stageH: Number;

    public function View1DModes( myMainView: MainView, myModel1: Model1 ) {
        this.myMainView = myMainView;
        this.myModel1 = myModel1;
        this.myModel1.registerView( this );
        this.showHideButton = new ShowHideButton( this );
        this.addChild( showHideButton );
        this.initialize();
    }

    private function initialize():void{
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this._LinMeters =  this.myModel1.L;
        this._LinPix = Math.round( 0.15*this.stageW );
        this._pixPerMeter = this._LinPix/this._LinMeters;
        //trace("View1DMode.initialize. pixPerMeter =" + _pixPerMeter );
        this._ySeparationOfGraphs = 30;
        var nMax = this.myModel1.nMax;

        this.walls_arr = new Array( nMax + 1 );  //need nMax+1 elements, since zero element is dummy
        this.sineWave_arr = new Array( nMax + 1);

        for( var i:int = 0; i <= nMax; i++ ){
            walls_arr[i] = new Sprite();
            sineWave_arr[i] = new Sprite();
            this.addChild( walls_arr[i] );
            this.addChild( sineWave_arr[i] );
        }
        this.drawWalls();

        this.makeLabels();
        this.positionLabels();
    }//end initialize()

    private function drawModeN( modeN: int ):void{
        var amplitude:Number = myModel1.getModeAmpli( modeN );
        var omega:Number = myModel1.getModeOmega( modeN );
        var phase:Number = myModel1.getModePhase( modeN );
        var time:Number = myModel1.t;
        var leftEdgeX = 0;
        var leftEdgeY = ( modeN - 1) * this._ySeparationOfGraphs;
        var g:Graphics = this.sineWave_arr[ modeN ].graphics;
        g.clear();
        g.lineStyle(3, 0x0000ff, 1);   //blue string
        g.moveTo( leftEdgeX, leftEdgeY );
        for(var xPix:int = 0; xPix <= this._LinPix; xPix += 2 ){
            var xPos:Number = xPix/_pixPerMeter;
            var yPos:Number = amplitude*Math.sin( xPos * modeN * Math.PI )*Math.cos( omega*time - phase );
            var yPix:Number = yPos * _pixPerMeter;
            g.lineTo( leftEdgeX + xPix, leftEdgeY - yPix);
        }
    }//end drawModeN()

    private function drawWalls():void{
        var nMax: int = this.myModel1.nMax;
        var h:Number = 10; //height of wall in pix
        var leftEdgeX = 0;
        for(var i:int = 1; i <= nMax; i++ ){
            var leftEdgeY = (i-1)*this._ySeparationOfGraphs;
            var g:Graphics = this.walls_arr[i].graphics;
            g.clear();
            g.lineStyle(5, 0x444444, 1);   //gray walls
            g.moveTo(leftEdgeX, leftEdgeY - h/2);
            g.lineTo(leftEdgeX, leftEdgeY + h/2);
            g.moveTo(leftEdgeX + this._LinPix, leftEdgeY - h/2);
            g.lineTo(leftEdgeX + this._LinPix, leftEdgeY + h/2);
        }
    }//end drawWalls()

    private function makeLabels():void{
        //normalModes_str = "Normal Modes";
        normalModes_str = FlexSimStrings.get( "normalModes", "Normal Modes");
        var nMax = this.myModel1.nMax;
        this.nbrLabel_arr = new Array( nMax + 1 );
        var tFormat1:TextFormat = new TextFormat();
        tFormat1.align = TextFormatAlign.CENTER;
        tFormat1.size = 16;
        tFormat1.font = "Arial";
        tFormat1.color = 0x000000;
        this.normalModes_txt = new TextField();
        normalModes_txt.text = normalModes_str;
        this.addChild( this.normalModes_txt );
        normalModes_txt.autoSize = TextFieldAutoSize.RIGHT;
        normalModes_txt.setTextFormat( tFormat1 );
        for( var i:int = 0; i <= nMax; i++ ){
            this.nbrLabel_arr[i] = new TextField();
            this.addChild( this.nbrLabel_arr[i] );
            this.nbrLabel_arr[i].autoSize = TextFieldAutoSize.RIGHT;
            this.nbrLabel_arr[i].text = i;
            this.nbrLabel_arr[i].setTextFormat( tFormat1 );
        }
    }//end make Labels

    private function positionLabels():void{
        var nMax = this.myModel1.nMax;
        this.normalModes_txt.y = -40;
        this.normalModes_txt.x = 0.5*_LinPix - 0.5*this.normalModes_txt.width;
        this.showHideButton.y = -30;
        this.showHideButton.x = this.normalModes_txt.x - 15; //+ this.normalModes_txt.width + 15;
        for( var i:int = 1; i <= nMax; i++ ){
            this.nbrLabel_arr[i].x = -nbrLabel_arr[i].width - 7;
            this.nbrLabel_arr[i].y = (i - 1)*this._ySeparationOfGraphs - 0.5*nbrLabel_arr[i].height;
        }
    }

    //show() hide() functions required by ShowHideButton
    public function show():void{
        var nMax: int = this.myModel1.nMax;
        for(var i:int = 1; i <= _N; i++ ){
            this.walls_arr[i].visible = true;
            this.sineWave_arr[i].visible = true;
            this.nbrLabel_arr[i].visible = true;
        }
    }

    public function hide():void{
        var nMax: int = this.myModel1.nMax;
        for(var i:int = 1; i <= _N; i++ ){
            this.walls_arr[i].visible = false;
            this.sineWave_arr[i].visible = false;
            this.nbrLabel_arr[i].visible = false;
        }
    }

    public function update():void{
        if( this.myModel1.nChanged ){
            this._N = this.myModel1.N;
            for( var j:int = 0; j <= this.myModel1.nMax ; j++ ){
                this.walls_arr[j].visible = false;
                this.sineWave_arr[j].visible = false;
                this.nbrLabel_arr[j].visible = false;
            }
            for( var n:int = 1; n <= _N; n++ ){
                this.walls_arr[n].visible = true;
                this.sineWave_arr[n].visible = true;
                this.nbrLabel_arr[n].visible = true;
            }
        }//end if(nChanged)
        for( n = 1; n <= this._N; n++ ){
            this.drawModeN( n );
        }
    }//end update()
} //end class
} //end package
