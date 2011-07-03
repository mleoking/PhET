/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/9/11
 * Time: 8:26 AM
 * To change this template use File | Settings | File Templates.
 */
//Array of VerticalSliders, two sliders for each mode: amplitude and phase
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.NiceComponents.VerticalSlider;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

import mx.controls.Text;
import mx.controls.sliderClasses.Slider;

import mx.core.UIComponent;
import mx.effects.effectClasses.ActionEffectInstance;

public class SliderArrayPanel extends UIComponent {

    private var myMainView: MainView;
    private var myModel1: Model1;
    private var container: Sprite;
    private var leftEdgeX;
    private var ampliSlider_arr:Array;   //array of verticalSliders for setting amplitude of mode.
    private var phaseSlider_arr:Array    //array of vertical Sliders for setting phase of mode
    private var nMax:int;                //maximum number of mobile masses = max nbr of normal modes
    private var phasesShown:Boolean;     //true if phases sliders are visible
    private var modeLabel_txt: TextField
    private var amplitudeLabel_txt:TextField;
    private var phaseLabel_txt:TextField;
    private var plusPi_txt:TextField;
    private var zero_txt:TextField;
    private var minusPi_txt:TextField;
    private var tFormat1:TextFormat;
    private var amplitude_str:String;
    private var phase_str:String;
    private var mode_str:String;
    private var plusPi_str:String;
    private var minusPi_str:String;

    public function SliderArrayPanel( myMainView: MainView, myModel1: Model1) {
        this.myMainView = myMainView;
        this.myModel1 = myModel1;
        this.myModel1.registerView( this );
        this.container = new Sprite();
        this.leftEdgeX = this.myMainView.myView1.leftEdgeX;
        this.nMax = this.myModel1.nMax;
        this.phasesShown = false;
        this.ampliSlider_arr = new Array( nMax );
        this.phaseSlider_arr = new Array( nMax );
        //this.amplitudeLabel_txt = new TextField();
        //this.phaseLabel_txt = new TextField();
        //this.tFormat1 = new TextFormat();
        //var vertSlider:VerticalSlider = new VerticalSlider( actionFunction, 150, 0, 10, true  );
        //vertSlider.setLabelText( "amplitude");
        //this.slider_arr[0] = vertSlider;

        this.addChild( this.container );
        this.initializeSliderArray();
        this.initializeStrings();

        for(var i:int = 0; i < this.nMax; i++ ){
            this.container.addChild( this.ampliSlider_arr[i] );
            this.container.addChild( this.phaseSlider_arr[i] );
        }
        this.createLabels();
        //this.container.addChild( this.amplitudeLabel_txt );
        //this.container.addChild( this.phaseLabel_txt );
        this.locateSliders();

    } //end constructor

    private function initializeSliderArray():void{
         for(var i:int = 0; i < this.nMax; i++){
             // VericalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, textEditable:Boolean = false, detented: Boolean = false, nbrTics: int = 0 , readoutShown:Boolean = true )
             var vertSliderAmpli:VerticalSlider = new VerticalSlider( setAmplitude, 100, 0, 0.1, false, false, 0, false  );
             var vertSliderPhase:VerticalSlider = new VerticalSlider( setPhase, 100, -Math.PI, +Math.PI, false, false, 0, false  );
             //vertSliderAmpli.setReadoutPrecision( 3 );
             //vertSliderPhase.setReadoutPrecision( 2 );

//             function amplitudeFunction():void{
//                 trace( "SliderArrayPanel.sliderIndex = " + vertSliderAmpli.index);
//             }
             var j:int = i+1;
             vertSliderAmpli.index = j;     //label slider with index = mode number (not array element number)
             vertSliderPhase.index = j;
             vertSliderAmpli.setLabelText( j.toString() );
             vertSliderPhase.killLabel();
             //vertSliderPhase.setLabelText( "Phase " + j );
             vertSliderAmpli.setScale( 100 );
             vertSliderAmpli.setLabelFontSize( 18, true );
             //vertSliderPhase.setScale( 1/Math.PI );
             //vertSliderAmpli.setUnitsText("cm");
             //vertSliderPhase.setUnitsText("pi");
             vertSliderAmpli.setReadoutPrecision( 1 );

             this.ampliSlider_arr[i] = vertSliderAmpli;
             this.ampliSlider_arr[i].setSliderWithoutAction( 0 );
             this.phaseSlider_arr[i] = vertSliderPhase;
             this.phaseSlider_arr[i].setSliderWithoutAction( 0 );
         }
    }//end createSliderArray

    private function initializeStrings():void{
        this.amplitude_str = "Amplitude:";
        this.phase_str = "Phase:";
        this.mode_str = "Mode:";
        this.plusPi_str = "+pi";
        this.minusPi_str = "-pi";
    }

    private function createLabels():void{
        this.tFormat1 = new TextFormat();
        this.tFormat1.align = TextFormatAlign.RIGHT;
        this.tFormat1.size = 20;
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.modeLabel_txt = new TextField();
        this.amplitudeLabel_txt = new TextField();
        this.phaseLabel_txt = new TextField();
        this.plusPi_txt = new TextField();
        this.zero_txt = new TextField();
        this.minusPi_txt = new TextField();
        this.amplitudeLabel_txt.text = this.amplitude_str;
        this.phaseLabel_txt.text = this.phase_str;
        this.modeLabel_txt.text = this.mode_str;
        this.plusPi_txt.text = this.plusPi_str;
        this.zero_txt.text = "0";
        this.minusPi_txt.text = minusPi_str;
        //this.amplitudeLabel_txt.setTextFormat( this.tFormat1 );
        //this.addChild( this.amplitudeLabel_txt );
        //this.amplitudeLabel_txt.x = -this.amplitudeLabel_txt.width;
        //this.phaseLabel_txt.setTextFormat( this.tFormat1 );
        setLabel( this.modeLabel_txt );
        setLabel( this.amplitudeLabel_txt );
        setLabel( this.phaseLabel_txt );
        setLabel( this.plusPi_txt );
        setLabel( this.zero_txt );
        setLabel( this.minusPi_txt );
        function setLabel( txtField:TextField ):void{
            txtField.autoSize = TextFieldAutoSize.RIGHT;
            txtField.setTextFormat( tFormat1 );
            addChild( txtField );     //this.addChild( txtField)  throws an error
            //txtField.border = true; //for testing only
        }

    }//end createLabels()

    //Arrange the layout of sliders
    public function locateSliders():void{
        var nbrSliders:int = this.myModel1.N;    //number of mobile masses = number normal modes
        var lengthBetweenWallsInPix:Number =  this.myMainView.myView1.LinPix;

        var horizSpacing:Number = 0.7*lengthBetweenWallsInPix/(nbrSliders + 1);
        var widthOfAllVisibleSliders = ( nbrSliders - 1) * horizSpacing;
        for(var i:int = 0; i < nbrSliders; i++){
            this.ampliSlider_arr[i].visible = true;
            this.ampliSlider_arr[i].x = this.leftEdgeX + 0.5*lengthBetweenWallsInPix - 0.5*widthOfAllVisibleSliders + i*horizSpacing;
            this.phaseSlider_arr[i].y =  1.0*this.ampliSlider_arr[i].height - 20;
            //this.phaseSlider_arr[i].visible = false;
            this.phaseSlider_arr[i].x = this.leftEdgeX + 0.5*lengthBetweenWallsInPix - 0.5*widthOfAllVisibleSliders + i*horizSpacing;
        }
        for( i = nbrSliders; i < this.nMax; i++ ){
            this.ampliSlider_arr[i].visible = false;
            //this.phaseSlider_arr[i].visible = false;
        }
        this.showPhaseSliders( this.phasesShown );
        var leftEdgeOfSliders:Number = this.leftEdgeX + 0.5*lengthBetweenWallsInPix - 0.5*widthOfAllVisibleSliders - 30;   //-30 to put 30 pix of space between label and leftEdge slider
        this.modeLabel_txt.y = -50;
        this.amplitudeLabel_txt.y = +30
        this.phaseLabel_txt.y = +160;
        this.modeLabel_txt.x = leftEdgeOfSliders - this.modeLabel_txt.width;
        this.amplitudeLabel_txt.x = leftEdgeOfSliders - this.amplitudeLabel_txt.width;
        this.phaseLabel_txt.x = leftEdgeOfSliders - 1.5*this.phaseLabel_txt.width;
        this.plusPi_txt.y = this.phaseSlider_arr[0].y - 0.5* this.phaseLabel_txt.height;
        this.zero_txt.y = this.plusPi_txt.y + 50;
        this.minusPi_txt.y = this.plusPi_txt.y + 100;
        var xOffset:Number = 10;
        this.plusPi_txt.x = leftEdgeOfSliders + xOffset - this.plusPi_txt.width;
        this.zero_txt.x = leftEdgeOfSliders + xOffset - this.zero_txt.width;
        this.minusPi_txt.x = leftEdgeOfSliders + xOffset - this.minusPi_txt.width;

        this.container.x = 0;
    } //end positionSliders();



    public function resetSliders():void{
        var amplitude:Number;
        var phase:Number;
        for(var j:int = 1; j <= this.myModel1.N; j++ ){
            amplitude = this.myModel1.getModeAmpli( j );
            phase = this.myModel1.getModePhase( j );
            this.ampliSlider_arr[ j - 1 ].setSliderWithoutAction( amplitude );
            this.phaseSlider_arr[ j - 1 ].setSliderWithoutAction( phase );
        }
    }

    private function setAmplitude( indx:int ):void{
        var A:Number = this.ampliSlider_arr[ indx - 1 ].getVal();
        this.myModel1.setModeAmpli( indx,  A );
        //trace("SliderArrayPanel.amplitudeFunction. Index = "+passedIndex)
    }

    private function setPhase( indx:int ):void{
        var phase:Number = this.phaseSlider_arr[ indx - 1 ].getVal();
        this.myModel1.setModePhase( indx,  phase );
    }

    public function showPhaseSliders( tOrF:Boolean ):void{
        this.phasesShown = tOrF;
        var nbrMasses:int = this.myModel1.N;
        this.phaseLabel_txt.visible = tOrF;
        this.plusPi_txt.visible = tOrF;
        this.zero_txt.visible = tOrF;
        this.minusPi_txt.visible = tOrF;
        for( var i:int = 0; i < nbrMasses; i++ ){
            this.phaseSlider_arr[i].visible = tOrF;

        }
        for ( i = nbrMasses; i < this.nMax; i++ ){
            this.phaseSlider_arr[i].visible = false;
        }
    }

    public function update():void{
        if( this.myModel1.modesChanged ){
            this.resetSliders();
            this.myModel1.modesChanged = false;
        }

    }
}//end class
}//end package
