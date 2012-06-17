/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/11/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.control {
import edu.colorado.phet.flashcommon.controls.HorizontalSlider;
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.radiatingcharge.model.FieldModel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.radiatingcharge.view.MainView;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.CheckBox;
import mx.controls.ComboBox;
import mx.controls.RadioButton;
import mx.core.UIComponent;
import mx.events.DropdownEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;

//Control Panel for Radiating Charge sim
public class ControlPanel extends Canvas {
    private var myMainView:MainView;
    private var myFieldModel:FieldModel;
    private var background: VBox;
    private var pauseButton:NiceButton2;
    private var stopButton:NiceButton2;
    private var resetButton:NiceButton2;
    private var restartButton:NiceButton2;
    private var restartButton_UI:SpriteUIComponent;
    private var bumpButton:NiceButton2;
    private var bumpButton_UI:SpriteUIComponent;
    public var myComboBox: ComboBox;
    private var choiceList_arr:Array;
    private var amplitudeSlider:HorizontalSlider;   //amplitude of both oscillatory and bumb motions
    private var minAmplitudeOscillatory:Number;     //max and min values for amplitude of oscillation
    private var maxAmplitudeOscillatory:Number;
    private var minAmplitudeBump:Number;            //max and min values for amplitude of bump
    private var maxAmplitudeBump:Number;
    private var frequencySlider:HorizontalSlider;
    public var speedSlider:HorizontalSlider;    //must be public to easily communicate with myFieldModel
    public var durationSlider:HorizontalSlider; //must be public to easily communicate with myFieldModel
    private var amplitudeSlider_UI:SpriteUIComponent;
    private var frequencySlider_UI:SpriteUIComponent;
    private var speedSlider_UI:SpriteUIComponent;
    private var durationSlider_UI:SpriteUIComponent;
    //private var lessRezRadioButton:RadioButton;
    private var moreSpeedCheckBox:CheckBox;

    //internationalized strings
    private var pause_str:String;
    private var unPause_str:String;
    //private var start_str:String;
    private var stop_str:String;
    private var restart_str:String;
    private var reset_str:String;
    private var c_str:String;
    private var duration_str:String;
    //private var s_str:String;
    private var moreSpeedLessRez_str:String;
    //Drop-down menu choices
    private var userChoice_str:String;
    private var linear_str:String;
    private var sinusoid_str:String;
    private var circular_str:String;
    //private var sawTooth_str:String;
    private var bump_str:String;
    private var random_str:String;
    //slider labels
    private var amplitude_str:String;
    private var frequency_str:String;
    private var speed_str:String;


    public function ControlPanel( mainView:MainView, model:FieldModel ) {
        super();
        this.myMainView = mainView;
        this.myFieldModel = model;
        this.init();
    }

    private function init():void{
        this.initializeStrings();

        this.background = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x88ff88 );
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 15 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }

        this.pauseButton = new NiceButton2( 100, 25, pause_str, pauseUnPause, 0xffff00, 0x000000 );
        this.stopButton = new NiceButton2( 100, 25, stop_str, stopCharge, 0xff0000, 0xffffff )
        this.resetButton = new NiceButton2( 100, 25, reset_str, resetCharge, 0xff0000, 0xffffff )
        this.restartButton = new NiceButton2( 100, 25, restart_str, restart, 0x00ff00, 0x000000 );
        this.bumpButton = new NiceButton2( 100, 25, bump_str, bumpCharge, 0x00ff00, 0x000000 );
        this.myComboBox = new ComboBox();
        this.choiceList_arr = new Array( );
        choiceList_arr = [ userChoice_str, linear_str, sinusoid_str, circular_str, bump_str, random_str ];
        myComboBox.dataProvider = choiceList_arr;
        myComboBox.addEventListener( DropdownEvent.CLOSE, comboBoxListener );
        var sliderWidth:Number = 100;
        minAmplitudeOscillatory  = 0;
        maxAmplitudeOscillatory = 10;
        minAmplitudeBump = 1;
        maxAmplitudeBump = 10;
        amplitudeSlider = new HorizontalSlider( setAmplitude, sliderWidth, minAmplitudeOscillatory, maxAmplitudeOscillatory );
        frequencySlider = new HorizontalSlider( setFrequency, sliderWidth, 0, 3 );
        durationSlider = new HorizontalSlider( setDuration, sliderWidth, 0.01, 1 );
        var c:Number = myFieldModel.getSpeedOfLight();
        speedSlider = new HorizontalSlider( setSpeed, sliderWidth, 0.1, 0.95 );
        amplitudeSlider.setLabelText( amplitude_str );
        amplitudeSlider.removeReadout();
        frequencySlider.setLabelText( frequency_str );
        frequencySlider.removeReadout();
        speedSlider.setLabelText( speed_str );
        speedSlider.setUnitsText( c_str );
        speedSlider.setReadoutPrecision( 2 );
        durationSlider.setLabelText( duration_str );
        //durationSlider.setUnitsText( s_str );
        durationSlider.removeReadout();
        moreSpeedCheckBox = new CheckBox();
        moreSpeedCheckBox.label = moreSpeedLessRez_str;
        moreSpeedCheckBox.addEventListener( Event.CHANGE, radioButtonListener );

        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( pauseButton, true ) );
        this.background.addChild( new SpriteUIComponent( stopButton, true ) );
        this.background.addChild( new SpriteUIComponent( resetButton, true ) );
        this.background.addChild( myComboBox );
        this.amplitudeSlider_UI = new SpriteUIComponent( amplitudeSlider, true )
        this.background.addChild( amplitudeSlider_UI );
        this.frequencySlider_UI = new SpriteUIComponent( frequencySlider, true )
        this.background.addChild( frequencySlider_UI );
        this.speedSlider_UI = new SpriteUIComponent( speedSlider, true );
        this.background.addChild( speedSlider_UI );
        this.durationSlider_UI = new SpriteUIComponent( durationSlider, true );
        this.background.addChild( durationSlider_UI );
        this.restartButton_UI = new SpriteUIComponent( restartButton, true );
        this.background.addChild( restartButton_UI );
        this.bumpButton_UI = new SpriteUIComponent( bumpButton, true );
        this.background.addChild( bumpButton_UI );
        this.setSliderVisiblity();
        this.background.addChild( moreSpeedCheckBox );

    }//end init()

    private function initializeStrings():void{
        pause_str = FlexSimStrings.get( "pause", "Pause" );
        unPause_str = FlexSimStrings.get( "unPause", "UnPause" );
        //start_str = FlexSimStrings.get( "start", "Start" );
        stop_str = FlexSimStrings.get( "stop", "Stop" );
        restart_str = FlexSimStrings.get( "restart", "Restart" );
        reset_str = FlexSimStrings.get("reset", "Reset");
        userChoice_str = FlexSimStrings.get( "userControlled", "User Controlled" );
        linear_str = FlexSimStrings.get( "linear", "Linear" );
        sinusoid_str = FlexSimStrings.get( "sinusoid", "Sinusoid" );
        circular_str = FlexSimStrings.get( "circular", "Circular" );
        bump_str = FlexSimStrings.get( "bump", "Bump" );
        random_str = FlexSimStrings.get( "random", "Random" );
        amplitude_str = FlexSimStrings.get( "amplitude", "amplitude" );
        frequency_str = FlexSimStrings.get( "frequency", "frequency" );
        speed_str = FlexSimStrings.get( "speed", "speed" );
        c_str = FlexSimStrings.get( "c", "c");
        duration_str = FlexSimStrings.get( "duration", "duration" );
        //s_str = FlexSimStrings.get( "seconds", "s" );
        moreSpeedLessRez_str = FlexSimStrings.get("moreSpeedLessRez", "More speed, less rez")
    }

    private function pauseUnPause():void{
        if( myFieldModel.paused ){
            this.myFieldModel.paused = false;
            this.pauseButton.setLabel( pause_str );
        }else{
            this.myFieldModel.paused = true;
            this.pauseButton.setLabel( unPause_str );
        }
    }//end pauseUnPause


    private function stopCharge():void{
        this.myFieldModel.stopCharge();
        this.setSliderVisiblity();
    }

    private function resetCharge():void{
        this.myFieldModel.paused = false;
        this.myFieldModel.centerCharge();
        this.setSliderVisiblity();
    }

    //restart() used in linear motion mode
    private function restart():void{
        this.myFieldModel.restartCharge();
    }

    private function bumpCharge():void{
        this.myFieldModel.bumpCharge( durationSlider.getVal() );
    }

    private function comboBoxListener( evt: DropdownEvent ):void{
        this.myFieldModel.paused = false;
        var choice:String = evt.currentTarget.selectedItem;
        if( choice == userChoice_str ){
            this.myFieldModel.setMotion( 0 );
        }else if( choice == linear_str ){
            if( isNaN(this.speedSlider.getVal()) ){
                this.myFieldModel.setBeta( 0.5 );
                this.speedSlider.setSliderWithoutAction( 0.5 );
            }
            this.myFieldModel.setMotion( 1 );
        }else if( choice == sinusoid_str ){
            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
            this.myFieldModel.setMotion( 2 );
        }else if( choice == circular_str ){
            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
            this.myFieldModel.setMotion( 3 );
        }else if( choice == bump_str ){
//            if( isNaN(this.durationSlider.getVal()) ){
//                this.myFieldModel.bumpDuration = 0.5;
//                this.durationSlider.setSliderWithoutAction( 0.5 );
//            }
            this.amplitudeSlider.minVal = this.minAmplitudeBump;
            this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
            this.myFieldModel.setMotion( 4 );
        }else if( choice == random_str ){ this.myFieldModel.setMotion( 5 );
        }else {
            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
        }
        this.setSliderVisiblity();
    }//end comboBoxListener

    private function radioButtonListener( evt: Event ) :void{
        //trace( "ControlPanel.radioButtonListener  state = "+evt.currentTarget.selected );
        if( evt.currentTarget.selected ) {
            this.myMainView.topCanvas.setResolution( "LOW" );
        }else{
            this.myMainView.topCanvas.setResolution( "HIGH" );
        }

    }

    private function setSliderVisiblity():void{
        amplitudeSlider_UI.visible = false;
        frequencySlider_UI.visible = false;
        durationSlider_UI.visible = false;
        speedSlider_UI.visible = false;
        restartButton_UI.visible = false;
        bumpButton_UI.visible = false;
        amplitudeSlider_UI.includeInLayout = false;
        frequencySlider_UI.includeInLayout = false;
        durationSlider_UI.includeInLayout = false;
        speedSlider_UI.includeInLayout = false;
        bumpButton_UI.includeInLayout = false;
        restartButton_UI.includeInLayout = false;
        var choice:int = myComboBox.selectedIndex;
        if( choice == 0 ){      //user-controlled
            //do nothing
        }else if( choice == 1 ){  //linear
            speedSlider_UI.visible = true;
            restartButton_UI.visible = true;
            speedSlider_UI.includeInLayout = true;
            restartButton_UI.includeInLayout = true;
            speedSlider.setSliderWithoutAction( myFieldModel.getBeta() );
        } else if( choice == 2 || choice == 3 ){   //sinusoidal or circular or sawtooth
            amplitudeSlider_UI.visible = true;
            frequencySlider_UI.visible = true;
            amplitudeSlider_UI.includeInLayout = true;
            frequencySlider_UI.includeInLayout = true;
            amplitudeSlider.setSliderWithoutAction( myFieldModel.amplitude );
            frequencySlider.setSliderWithoutAction( myFieldModel.frequency );
        } else if( choice == 4 ){ //bump
            amplitudeSlider_UI.visible = true;
            durationSlider_UI.visible = true;
            bumpButton_UI.visible = true;
            amplitudeSlider_UI.includeInLayout = true;
            durationSlider_UI.includeInLayout = true;
            bumpButton_UI.includeInLayout = true;
            myFieldModel.amplitude = 0.3*amplitudeSlider.maxVal;
            myFieldModel.bumpDuration = 0.3*durationSlider.maxVal;
            amplitudeSlider.setSliderWithoutAction( myFieldModel.amplitude );
            durationSlider.setSliderWithoutAction( myFieldModel.bumpDuration );
        } else if( choice == 5 ){//random walk
            //do nothing
        }
    }//end setSliderVisibility()

    private function setAmplitude():void{
        myFieldModel.amplitude = amplitudeSlider.getVal();
    }

    private function setFrequency():void{
        var freq:Number = frequencySlider.getVal();
        myFieldModel.setFrequency( freq );
    }

    private function setSpeed():void{
        var beta:Number = speedSlider.getVal();
        myFieldModel.setBeta( beta );
    }

    private function setDuration():void{  //duration of bump
        myFieldModel.bumpDuration = durationSlider.getVal();
    }
} //end class
} //end package
