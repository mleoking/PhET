/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/9/11
 * Time: 8:26 AM
 * To change this template use File | Settings | File Templates.
 */
//Array of VerticalSliders, two sliders for each mode: amplitude and phase
package edu.colorado.phet.normalmodes {
import edu.colorado.phet.normalmodes.NiceComponents.VerticalSlider;

import flash.display.Sprite;

import mx.core.UIComponent;
import mx.effects.effectClasses.ActionEffectInstance;

public class SliderArrayPanel extends UIComponent {

    private var myMainView: MainView;
    private var myModel: Model;
    private var container: Sprite;
    private var leftEdgeX;
    private var ampliSlider_arr:Array;       //array of verticalSliders for setting amplitude of mode.
    private var phaseSlider_arr:Array   //array of vertical Sliders for setting phase of mode
    private var nMax:int;               //maximum number of mobile masses = max nbr of normal modes

    public function SliderArrayPanel( myMainView: MainView, myModel: Model) {
        this.myMainView = myMainView;
        this.myModel = myModel;
        this.container = new Sprite();
        this.leftEdgeX = this.myMainView.myView.leftEdgeX;
        this.nMax = this.myModel.nMax;
        this.ampliSlider_arr = new Array( nMax );
        this.phaseSlider_arr = new Array( nMax );
        //var vertSlider:VerticalSlider = new VerticalSlider( actionFunction, 150, 0, 10, true  );
        //vertSlider.setLabelText( "amplitude");
        //this.slider_arr[0] = vertSlider;



        this.addChild( this.container );
        this.initializeSliderArray();
        for(var i:int = 0; i < this.nMax; i++ ){
            this.container.addChild( this.ampliSlider_arr[i] );
            this.container.addChild( this.phaseSlider_arr[i] );
        }

        this.locateSliders();

    } //end constructor

    private function initializeSliderArray():void{
         for(var i:int = 0; i < this.nMax; i++){
             // VericalSlider( action: Function, lengthInPix: int, minVal: Number, maxVal: Number, textEditable:Boolean = false, detented: Boolean = false, nbrTics: int = 0 , readoutShown:Boolean = true )
             var vertSliderAmpli:VerticalSlider = new VerticalSlider( setAmplitude, 100, 0, 0.1, true  );
             var vertSliderPhase:VerticalSlider = new VerticalSlider( setPhase, 100, -Math.PI, +Math.PI, true  );
             vertSliderAmpli.setReadoutPrecision( 3 );
             vertSliderPhase.setReadoutPrecision( 2 );

//             function amplitudeFunction():void{
//                 trace( "SliderArrayPanel.sliderIndex = " + vertSliderAmpli.index);
//             }
             var j:int = i+1;
             vertSliderAmpli.index = j;     //label slider with index = mode number (not array element number)
             vertSliderPhase.index = j;
             vertSliderAmpli.setLabelText( "Ampli " + j );
             vertSliderPhase.setLabelText( "Phase " + j );
             this.ampliSlider_arr[i] = vertSliderAmpli;
             this.ampliSlider_arr[i].setSliderWithoutAction( 0 );
             this.phaseSlider_arr[i] = vertSliderPhase;
             this.phaseSlider_arr[i].setSliderWithoutAction( 0 );
         }
    }//end createSliderArray

    //Arrange the layout of sliders
    public function locateSliders():void{
        var nbrSliders:int = this.myModel.N;    //number of mobile masses = number normal modes
        var lengthBetweenWallsInPix:Number =  this.myMainView.myView.LinPix;

        var horizSpacing:Number = lengthBetweenWallsInPix/(nbrSliders + 1);
        for(var i:int = 0; i < nbrSliders; i++){
            this.ampliSlider_arr[i].visible = true;
            this.ampliSlider_arr[i].x = this.leftEdgeX + (i+1)*horizSpacing;
            this.phaseSlider_arr[i].y =  1.1*this.ampliSlider_arr[i].height;
            this.phaseSlider_arr[i].visible = true;
            this.phaseSlider_arr[i].x = this.leftEdgeX + (i+1)*horizSpacing;
        }
        for( i = nbrSliders; i < this.nMax; i++ ){
            this.ampliSlider_arr[i].visible = false;
            this.phaseSlider_arr[i].visible = false;
        }

        this.container.x = 0;
    } //end positionSliders();

    public function resetSliders():void{
        var amplitude:Number;
        var phase:Number;
        for(var j:int = 1; j <= this.myModel.N; j++ ){
            amplitude = this.myModel.getModeAmpli( j );
            phase = this.myModel.getModePhase( j );
            this.ampliSlider_arr[ j - 1 ].setSliderWithoutAction( amplitude );
            this.phaseSlider_arr[j - 1 ].setSliderWithoutAction( phase );
        }
    }


    private function setAmplitude( indx:int ):void{
        var A:Number = this.ampliSlider_arr[ indx - 1 ].getVal();
        this.myModel.setModeAmpli( indx,  A );
        //trace("SliderArrayPanel.amplitudeFunction. Index = "+passedIndex)
    }

    private function setPhase( indx:int ):void{
        var phase:Number = this.phaseSlider_arr[ indx - 1 ].getVal();
        this.myModel.setModePhase( indx,  phase );
    }
}//end class
}//end package
