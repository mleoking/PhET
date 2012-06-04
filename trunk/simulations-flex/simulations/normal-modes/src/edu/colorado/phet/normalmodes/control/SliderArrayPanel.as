/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/9/11
 * Time: 8:26 AM
 * To change this template use File | Settings | File Templates.
 */
//Array of VerticalSliders, two sliders for each mode: amplitude and phase
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.flashcommon.controls.VerticalSlider;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.text.Font;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

import mx.containers.Canvas;

import mx.controls.Text;
import mx.controls.sliderClasses.Slider;

import mx.core.UIComponent;
import mx.effects.effectClasses.ActionEffectInstance;

//Array of amplitude and phase sliders which only appear with 1D View
public class SliderArrayPanel extends Canvas {

    private var myMainView: MainView;
    private var myModel1: Model1;
    private var showHideButton:ShowHideButton;
    private var container: Sprite;
    private var boundingBox:Sprite;
    private var myPolarizationPanel: PolarizationPanel;
    private var leftEdgeX;
    private var ampliSlider_arr:Array;   //array of verticalSliders for setting amplitude of mode.
    private var phaseSlider_arr:Array    //array of vertical Sliders for setting phase of mode
    private var nMax:int;                //maximum number of mobile masses = max nbr of normal modes
    private var phasesShown:Boolean;     //true if phases sliders are visible
    private var modeSpectrum_txt: TextField;
    private var modeLabel_txt: TextField
    private var amplitudeLabel_txt:TextField;
    private var frequency_txt:TextField;
    private var freqLabelIndex:int;
    private var phaseLabel_txt:TextField;
    private var plusPi_txt:TextField;
    private var zero_txt:TextField;
    private var minusPi_txt:TextField;
    //private var tFormat1:TextFormat;
    private var modeSpectrum_str:String;
    private var mode_str:String;
    private var amplitude_str:String;
    private var frequency_str:String;
    private var omega_str:String;
    private var phase_str:String;
    private var plusPi_str:String;
    private var minusPi_str:String;

    public function SliderArrayPanel( myMainView: MainView, myModel1: Model1) {
        //percentWidth = 100;
        //percentHeight = 100;
        this.myMainView = myMainView;
        this.myModel1 = myModel1;
        this.myModel1.registerView( this );
        this.showHideButton = new ShowHideButton( this );
        this.container = new Sprite();
        this.boundingBox = new Sprite();
        this.myPolarizationPanel = new PolarizationPanel( myMainView,  myModel1 );
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

        this.addChild( new SpriteUIComponent ( this.container ) );
        this.addChild( new SpriteUIComponent ( this.boundingBox ));
        this.initializeSliderArray();
        this.initializeStrings();

        for(var i:int = 0; i < this.nMax; i++ ){
            this.container.addChild( this.ampliSlider_arr[i] );
            this.container.addChild( this.phaseSlider_arr[i] );
        }
        this.createLabels();
        this.createFrequencyLabels();
        this.createModeIcons();


        this.addChild( this.myPolarizationPanel );
        this.myPolarizationPanel.x = this.container.width + 20;
        this.myPolarizationPanel.y = 0;
        this.addChild( new SpriteUIComponent( this.showHideButton ) );
        //this.showHideButton.x =  this.container.width + 40;
        //this.showHideButton.y = -20;
        this.locateSlidersAndLabels();
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
        this.modeSpectrum_str = FlexSimStrings.get("modeSpectrum", "Normal Mode Spectrum");
        this.mode_str = FlexSimStrings.get("normalMode:", "Normal Mode:");
        this.amplitude_str = FlexSimStrings.get("amplitude:", "Amplitude:");
        this.frequency_str = FlexSimStrings.get("frequency:", "Frequency:");
        this.omega_str = FlexSimStrings.get("omega", "\u03c9");
        this.phase_str = FlexSimStrings.get("phase:", "Phase:");
        this.plusPi_str = FlexSimStrings.get("plusPi", "+\u03c0");
        this.minusPi_str = FlexSimStrings.get("minusPi", "-\u03c0");
    }

    private function createLabels():void{
        var tFormat:TextFormat = new TextFormat();
        tFormat.align = TextFormatAlign.RIGHT;
        tFormat.size = 20;
        tFormat.font = "Arial";
        tFormat.color = 0x000000;
        this.modeSpectrum_txt = new TextField();
        this.modeLabel_txt = new TextField();
        this.amplitudeLabel_txt = new TextField();
        this.frequency_txt = new TextField();
        this.phaseLabel_txt = new TextField();
        this.plusPi_txt = new TextField();
        this.zero_txt = new TextField();
        this.minusPi_txt = new TextField();
        this.modeSpectrum_txt.text = this.modeSpectrum_str;
        this.amplitudeLabel_txt.text = this.amplitude_str;
        this.frequency_txt.text = this.frequency_str;
        this.phaseLabel_txt.text = this.phase_str;
        this.modeLabel_txt.text = this.mode_str;
        this.plusPi_txt.text = this.plusPi_str;
        this.zero_txt.text = "0";
        this.minusPi_txt.text = minusPi_str;
        setLabel( this.modeLabel_txt );
        setLabel( this.amplitudeLabel_txt );
        setLabel( this.frequency_txt );
        setLabel( this.phaseLabel_txt );
        setLabel( this.plusPi_txt );
        setLabel( this.zero_txt );
        setLabel( this.minusPi_txt );
        function setLabel( txtField:TextField ):void{
            txtField.autoSize = TextFieldAutoSize.RIGHT;
            txtField.setTextFormat( tFormat );
            container.addChild( txtField );     //this.addChild( txtField)  throws an error
            //txtField.border = true; //for testing only
        }
        tFormat.size = 16;
        this.frequency_txt.setTextFormat( tFormat );
        tFormat.font = "Times New Roman";//"Symbol";
        tFormat.size = 20;
        this.plusPi_txt.setTextFormat( tFormat );
        this.minusPi_txt.setTextFormat( tFormat );
        //Reset tFormat for Mode Spectrum label
        tFormat.size = 20;
        tFormat.font = "Arial";
        this.modeSpectrum_txt.autoSize = TextFieldAutoSize.LEFT;
        this.modeSpectrum_txt.setTextFormat( tFormat );
        this.boundingBox.addChild( modeSpectrum_txt );
        this.modeSpectrum_txt.visible = false;  //only visible when container hidden by ShowHide Button
        //this.plusPi_txt.border = true;    //for testing only
    }//end createLabels()

    private function createModeIcons():void{
        //Sine Wave Icons above each amplitude slider
        var nI:uint = 10;  //number of icons shown
        //draw a sine wave with i half-wavelengths
        for( var i:int = 1; i <= nI; i++ ){
            var w:Number = 40;  //width of icon in pixels
            var h:Number = 20;  //height of icon in pixels
            var iconSprite:Sprite = new Sprite();
            var iG:Graphics = iconSprite.graphics;
            iG.clear();
            iG.lineStyle(2, 0x0000ff, 1 );
            iG.moveTo( 0, 0 );
            var nP:int = w;     //number of points on x-axis
            for( var xP:int = 1; xP <= nP; xP++ ){
                var lambda:Number =  2*w/i;   //i*lambda/2 = w
                iG.lineTo( xP,  -(h/2)*Math.sin(2*Math.PI*xP/lambda) );
            }
            //horizontal line at y = 0
            iG.lineStyle( 2, 0x000000, 1 );
            iG.moveTo( 0, 0 );
            iG.lineTo( w, 0 );
            this.ampliSlider_arr[ i - 1 ].addChild( iconSprite );
            iconSprite.x = - iconSprite.width/2;
            iconSprite.y = - 45;
        }
    }//end createModeIcons

    private function createFrequencyLabels():void{
        //label showing frequency of mode at bottom of each amplitude slider
        var tFormatGreek = new TextFormat();
        tFormatGreek.align = TextFormatAlign.CENTER;
        //tFormatGreek.font = "Symbol";            //don't need to set the font
        tFormatGreek.size = 15;
        tFormatGreek.color = 0x000000;
        for ( var i:int = 0; i < this.myModel1.nMax; i++ ){
            var freq_txt:TextField = new TextField();
            freq_txt.text = this.myModel1.freqDividedByOmega_arr[ 0 ].toFixed(2);   //need a height to establish layout
            freq_txt.autoSize = TextFieldAutoSize.CENTER;
            freq_txt.setTextFormat( tFormatGreek );
            freq_txt.defaultTextFormat = tFormatGreek;
            this.ampliSlider_arr[ i ].addChild( freq_txt );
            this.freqLabelIndex = this.ampliSlider_arr[i].getChildIndex( freq_txt );
            freq_txt.x = - freq_txt.width/2;
            freq_txt.y = this.ampliSlider_arr[ i ].height - 1.3*this.frequency_txt.height;
        }
    }//end createFrequencyLabels()

    private function setFrequencyLabels():void{
        //must be called whenever number of masses is changed
        for ( var i:int = 0; i < this.myModel1.N; i++ ){
            var freq_txt:TextField = this.ampliSlider_arr[i].getChildAt( this.freqLabelIndex );
            var freqValue_str:String = this.myModel1.freqDividedByOmega_arr[i].toFixed(2);
            freq_txt.text =  freqValue_str + omega_str; //FlexSimStrings.get("freq omega", "{0}w",[freqValue_str]);
            var tFormatArial:TextFormat = new TextFormat();
            tFormatArial.font = "Arial";
            tFormatArial.size = 15;
            tFormatArial.color = 0x000000;
            var nbrChars:int = freq_txt.length;
            freq_txt.setTextFormat( tFormatArial, 0, nbrChars - 1 );
        }
    }


    //Arrange the layout of sliders, called whenever number of masses is changed
    public function locateSlidersAndLabels():void{
        var nbrSliders:int = this.myModel1.N;    //number of mobile masses = number normal modes
        var lengthBetweenWallsInPix:Number =  this.myMainView.myView1.LinPix;
        var horizSpacing:Number = 0.7*lengthBetweenWallsInPix/(nbrSliders + 1);
        var yOffset:int = 60;       //nbr of pixels graphics are shifted down
        var widthOfAllVisibleSliders = ( nbrSliders - 1) * horizSpacing;
        for(var i:int = 0; i < nbrSliders; i++){
            this.ampliSlider_arr[i].visible = true;
            this.ampliSlider_arr[i].x = this.leftEdgeX + 0.5*lengthBetweenWallsInPix - 0.5*widthOfAllVisibleSliders + i*horizSpacing;
            this.ampliSlider_arr[i].y = yOffset;
            this.phaseSlider_arr[i].y =  yOffset + 1.0*this.ampliSlider_arr[i].height - 30;
            //this.phaseSlider_arr[i].visible = false;
            this.phaseSlider_arr[i].x = this.leftEdgeX + 0.5*lengthBetweenWallsInPix - 0.5*widthOfAllVisibleSliders + i*horizSpacing;
        }
        for( i = nbrSliders; i < this.nMax; i++ ){
            this.ampliSlider_arr[i].visible = false;
            //this.phaseSlider_arr[i].visible = false;
        }
        var rightEdgeOfSliders = this.ampliSlider_arr[nbrSliders - 1].x + this.ampliSlider_arr[nbrSliders - 1].width;
        this.setFrequencyLabels();
        this.showPhaseSliders( this.phasesShown );
        var leftEdgeOfSliders:Number = this.leftEdgeX + 0.5*lengthBetweenWallsInPix - 0.5*widthOfAllVisibleSliders - 30;   //-30 to put 30 pix of space between label and leftEdge slider
        this.modeSpectrum_txt.x = leftEdgeOfSliders - 100;
        this.modeSpectrum_txt.y = -10;
        this.modeLabel_txt.y = yOffset - 36;
        this.amplitudeLabel_txt.y = yOffset + 30;
        this.frequency_txt.y = yOffset + 118;
        this.phaseLabel_txt.y = yOffset + 190;
        this.modeLabel_txt.x = leftEdgeOfSliders - this.modeLabel_txt.width;
        this.amplitudeLabel_txt.x = leftEdgeOfSliders - this.amplitudeLabel_txt.width;
        this.frequency_txt.x = leftEdgeOfSliders - this.frequency_txt.width;
        this.phaseLabel_txt.x = leftEdgeOfSliders - 1.5*this.phaseLabel_txt.width;
        this.plusPi_txt.y = this.phaseSlider_arr[0].y - 0.3*this.plusPi_txt.height;
        this.zero_txt.y = this.phaseSlider_arr[0].y + 0.5*this.phaseSlider_arr[0].height - 0.5*this.zero_txt.height;
        this.minusPi_txt.y = this.phaseSlider_arr[0].y + this.phaseSlider_arr[0].height - this.minusPi_txt.height;
        var xOffset:Number = 10;
        this.plusPi_txt.x = leftEdgeOfSliders + xOffset - this.plusPi_txt.width;
        this.zero_txt.x = leftEdgeOfSliders + xOffset - this.zero_txt.width;
        this.minusPi_txt.x = this.plusPi_txt.x; //leftEdgeOfSliders + xOffset - this.minusPi_txt.width;

        this.container.x = 0;
        this.myPolarizationPanel.x = rightEdgeOfSliders;
        this.drawBoundingBox();

    } //end positionSliders();

    public function drawBoundingBox():void{
        //var g:Graphics = this.container.graphics;
        var g:Graphics = this.boundingBox.graphics;
        g.clear();
        g.lineStyle( 5, 0x999999, 1 );  //gray color
        var xPos:Number = Math.min( modeLabel_txt.x, frequency_txt.x ) - 15;
        var yPos:Number = this.container.y - 20;//this.modeSpectrum_txt.y; // - this.modeSpectrum_txt.height; //
        var rightEdgeOfSliders:Number = this.ampliSlider_arr[myModel1.N - 1].x + this.ampliSlider_arr[myModel1.N - 1].width;
        //Next line necessary for correct start-up. On first start-up, this.myPolarizationPanel.width = 0
        var polarizationPanelWidth:Number = Math.max( 97, this.myPolarizationPanel.width );
        var w:int = rightEdgeOfSliders + polarizationPanelWidth + 20 - xPos;
        //trace("SliderArrayPanel.myPolarizationPanel.width = "+this.myPolarizationPanel.width) ;
        var h:int;
        if(this.phasesShown && this.container.visible ){
            h = 40 + phaseSlider_arr[0].y + phaseSlider_arr[1].height - container.y;
        } else{
            h = -15 + ampliSlider_arr[0].y + ampliSlider_arr[1].height - container.y;
        }
        if( !this.container.visible ){
            h = 50;
        }
        g.drawRoundRect( xPos, yPos, w, h, 20 );

        //position showHideButton
        this.showHideButton.x = xPos + this.showHideButton.width;
        this.showHideButton.y = yPos + this.showHideButton.height;
        //Locate Main Label in top left of bounding box;
        //this.modeSpectrum_txt.x = xPos;
        //this.modeSpectrum_txt.x = yPos;
    }

    public function resetSliders():void{
        var amplitude:Number;
        var phase:Number;
        for(var j:int = 1; j <= this.myModel1.N; j++ ){
            amplitude = this.myModel1.getModeAmpli( j );
            phase = this.myModel1.getModePhase( j );
            this.ampliSlider_arr[ j - 1 ].setSliderWithoutAction( amplitude );
            this.phaseSlider_arr[ j - 1 ].setSliderWithoutAction( phase );
            //trace("SliderArrayPanel.resetSliders. slider = "+ j + "   phase =" + phase);
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
        this.drawBoundingBox();
    }

    //show() hide() functions required by ShowHideButton
    public function show():void{
        this.container.visible = true;
        this.myPolarizationPanel.visible = true;
        this.modeSpectrum_txt.visible = false;
        this.drawBoundingBox();
    }

    public function hide():void{
        this.container.visible = false;
        this.myPolarizationPanel.visible = false;
        this.modeSpectrum_txt.visible = true;
        this.drawBoundingBox();
    }

    public function update():void{
        if( this.myModel1.modesChanged ){
            this.resetSliders();
            this.myModel1.modesChanged = false;
        }
    }
}//end class
}//end package
