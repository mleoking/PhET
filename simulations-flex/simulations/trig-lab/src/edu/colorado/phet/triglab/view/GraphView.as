/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Graphics;

import flash.display.Sprite;
//View of xy graph of trig functions
public class GraphView extends Sprite{

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var wavelengthInPix: Number; //number of pixels in one wavelength
    private var amplitudeInPix: Number;  //number of pixels in one amplitude of sine or cos = nbr pixels in one unit on y-axis
    private var nbrWavelengths: int;     //number of wavelengths in full graph; must be even number; only a portion of graph is shown to user
    private var axesGraph: Sprite;
    private var cosGraph: Sprite;
    private var sinGraph: Sprite;
    private var valueIndicator: Sprite;
    private var whichValueIndicator: String;     //the string is "cos", "sin", or "tan" depending on which graph is selected.
    private var _showCos: Boolean;
    private var _showSin: Boolean;
    private var _showTan: Boolean;


    public function GraphView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;


        this.myTrigModel.registerView( this );
        this.initializeStrings();
        this.init();
        this.myTrigModel.updateViews();
    }

    private function initializeStrings():void{
        //this.paused_str = FlexSimStrings.get( "paused", "PAUSED" );
    }

    private function init():void{
        this.selectWhichValueIndicator( "sin" );

        this.wavelengthInPix = 200;
        this.nbrWavelengths = 2*3;  //must be even number, so equal nbr of wavelengths are shown on right and left of origin.
        this.amplitudeInPix = 70;
        this.axesGraph = new Sprite();
        this.cosGraph = new Sprite();
        this.sinGraph = new Sprite();
        this.valueIndicator = new Sprite();
        this.drawAxesGraph();
        this.drawTrigFunctions();
        this.drawValueIndicator();
        this.addChild( axesGraph );
        this.addChild( cosGraph );
        this.addChild( sinGraph );
        this.addChild( valueIndicator );

    }

    //xy axes, origin is at (0, 0) in the sprite
    private function drawAxesGraph():void{
        var gAxes: Graphics = axesGraph.graphics;
        with( gAxes ){
            clear();
            lineStyle( 2, 0x000000, 1 );
            moveTo( -wavelengthInPix*nbrWavelengths/2, 0 );   //x-axis
            lineTo( wavelengthInPix*nbrWavelengths/2, 0 );
            moveTo( 0, -1.5*amplitudeInPix );                 //y-axis
            lineTo( 0, 1.5*amplitudeInPix );
            var ticLength: int = 5;
            for( var n:int = -nbrWavelengths; n <= nbrWavelengths; n++ ){
                moveTo( n*wavelengthInPix/2, -ticLength );
                lineTo( n*wavelengthInPix/2, +ticLength );
            }//end for
        }
    }//end drawAxesGraph()

    private function drawTrigFunctions():void{
        var N:int = wavelengthInPix;
        var gCos: Graphics = cosGraph.graphics;
        with( gCos ){
            clear();
            lineStyle( 3, 0x0000ff );
            for( var j:int = -nbrWavelengths/2; j < nbrWavelengths/2; j++ ){
                moveTo(j*wavelengthInPix, -amplitudeInPix*Math.cos( 0 ));
                for( var i:int = 1; i <= N; i++ ){
                    var x:Number = 2*Math.PI*i/wavelengthInPix;
                    lineTo( i + j*wavelengthInPix, -amplitudeInPix*Math.cos( x ));
                }//end for i
            }//end for j
        } //end with
        var gSin: Graphics = sinGraph.graphics;
        with( gSin ){
            clear();
            lineStyle( 3, 0x008800 );
            for( var j:int = -nbrWavelengths/2; j < nbrWavelengths/2; j++ ){
                moveTo(j*wavelengthInPix, -amplitudeInPix*Math.sin( 0 ));
                for( var i:int = 1; i <= N; i++ ){
                    var x:Number = 2*Math.PI*i/wavelengthInPix;
                    lineTo( i + j*wavelengthInPix, -amplitudeInPix*Math.sin( x ));
                }//end for i
            }//end for j
        } //end with
    } //end drawTrigFuctions()

    private function drawValueIndicator():void{
        var g:Graphics = valueIndicator.graphics;
        with( g ){
            clear();
            lineStyle( 1, 0x0000ff, 1 );
            beginFill( 0xff0000, 1 );
            drawCircle( 0, 0, 5 );
            endFill();
        }
    } //end drawValueIndicator()

    public function set showCos( tOrF:Boolean ):void{
        this._showCos = tOrF;
    }
    public function set showSin( tOrF:Boolean ):void{
        this._showSin = tOrF;
    }
    public function set showTan( tOrF:Boolean ):void{
        this._showTan = tOrF;
    }

    public function selectWhichValueIndicator( trigFunction: String ):void{
        if( trigFunction == "cos" || trigFunction == "sin" || trigFunction == "tan" ){
            this.whichValueIndicator = trigFunction;
        }else{
            trace("Invalid argument in function GraphView.selectWhichGraph()");
        }
    }

    public function update():void{
        var angleInRads:Number = myTrigModel.totalAngle;
        valueIndicator.x = (wavelengthInPix*angleInRads/(2*Math.PI)) ;
        if( whichValueIndicator == "cos"){
            valueIndicator.y = -amplitudeInPix*Math.cos( angleInRads );
        }else if( whichValueIndicator == "sin" ){
            valueIndicator.y = -amplitudeInPix*Math.sin( angleInRads );
        }else if( whichValueIndicator == "tan" ){
            valueIndicator.y = -amplitudeInPix*Math.tan( angleInRads );
        }
    }//end of update()
}//end of class
}//end of package
