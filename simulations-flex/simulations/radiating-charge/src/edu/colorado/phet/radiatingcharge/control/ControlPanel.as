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
import edu.colorado.phet.radiatingcharge.util.SpriteUIComponent;
import edu.colorado.phet.radiatingcharge.view.MainView;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.ComboBox;
import mx.core.UIComponent;
import mx.events.DropdownEvent;

//Control Panel for Radiating Charge sim
public class ControlPanel extends Canvas {
    private var myMainView:MainView;
    private var myFieldModel:FieldModel;
    private var background: VBox;
    private var pauseButton:NiceButton2;
    private var stopButton:NiceButton2;
    private var centerChargeButton:NiceButton2;
    private var restartButton:NiceButton2;
    private var restartButton_UI:SpriteUIComponent;
    public var myComboBox: ComboBox;
    private var choiceList_arr:Array;
    private var amplitudeSlider:HorizontalSlider;
    private var frequencySlider:HorizontalSlider;
    public var speedSlider:HorizontalSlider;    //must be public to easily communicate with myFieldModel
    private var amplitudeSlider_UI:SpriteUIComponent;
    private var frequencySlider_UI:SpriteUIComponent;
    private var speedSlider_UI:SpriteUIComponent;

    //internationalized strings
    private var pause_str:String;
    private var unPause_str:String;
    private var start_str:String;
    private var stop_str:String;
    private var restart_str:String;
    private var centerCharge_str:String;
    private var c_str:String;
    //Drop-down menu choices
    private var userChoice_str:String;
    private var linear_str:String;
    private var sinusoid_str:String;
    private var circular_str:String;
    private var sawTooth_str:String;
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
            setStyle( "paddingRight", 10 );
            setStyle( "paddingLeft", 10 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }

        this.pauseButton = new NiceButton2( 100, 25, pause_str, pauseUnPause, 0x00ff00, 0x000000 );
        this.stopButton = new NiceButton2( 100, 25, stop_str, stopCharge, 0x00ff00, 0x000000 )
        this.centerChargeButton = new NiceButton2( 130, 25, centerCharge_str, centerCharge, 0x00ff00, 0x000000 )
        this.restartButton = new NiceButton2( 100, 25, restart_str, restart, 0x00ff00, 0x000000 );
        this.myComboBox = new ComboBox();
        this.choiceList_arr = new Array( );
        choiceList_arr = [ userChoice_str, linear_str, sinusoid_str, circular_str, sawTooth_str, random_str ];
        myComboBox.dataProvider = choiceList_arr;
        myComboBox.addEventListener( DropdownEvent.CLOSE, comboBoxListener );
        var sliderWidth:Number = 100;
        amplitudeSlider = new HorizontalSlider( setAmplitude, sliderWidth, 0, 10 );
        frequencySlider = new HorizontalSlider( setFrequency, sliderWidth, 0, 3 );
        var c:Number = myFieldModel.getSpeedOfLight();
        speedSlider = new HorizontalSlider( setSpeed, sliderWidth, 0.1, 0.95 );
        amplitudeSlider.setLabelText( amplitude_str );
        amplitudeSlider.removeReadout();
        frequencySlider.setLabelText( frequency_str );
        frequencySlider.removeReadout();
        speedSlider.setLabelText( speed_str );
        speedSlider.setUnitsText( c_str );
        speedSlider.setReadoutPrecision( 2 );

        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( pauseButton, true ) );
        this.background.addChild( new SpriteUIComponent( stopButton, true ) );
        this.background.addChild( new SpriteUIComponent( centerChargeButton, true ) );
        this.background.addChild( myComboBox );
        this.amplitudeSlider_UI = new SpriteUIComponent( amplitudeSlider, true )
        this.background.addChild( amplitudeSlider_UI );
        this.frequencySlider_UI = new SpriteUIComponent( frequencySlider, true )
        this.background.addChild( frequencySlider_UI );
        this.speedSlider_UI = new SpriteUIComponent( speedSlider, true );
        this.background.addChild( speedSlider_UI );
        this.restartButton_UI = new SpriteUIComponent( restartButton, true );
        this.background.addChild( restartButton_UI );
        this.setSliderVisiblity();

    }//end init()

    private function initializeStrings():void{
        pause_str = FlexSimStrings.get( "pause", "Pause" );
        unPause_str = FlexSimStrings.get( "unPause", "UnPause" );
        start_str = FlexSimStrings.get( "start", "Start" );
        stop_str = FlexSimStrings.get( "stop", "Stop" );
        restart_str = FlexSimStrings.get( "restart", "Restart" );
        centerCharge_str = FlexSimStrings.get("centerCharge", "Center Charge");
        userChoice_str = FlexSimStrings.get( "userControlled", "User Controlled" );
        linear_str = FlexSimStrings.get( "linear", "Linear" );
        sinusoid_str = FlexSimStrings.get( "sinusoid", "Sinusoid" );
        circular_str = FlexSimStrings.get( "circular", "Circular" );
        sawTooth_str = FlexSimStrings.get( "sawTooth", "Sawtooth" );
        random_str = FlexSimStrings.get( "random", "Random" );
        amplitude_str = FlexSimStrings.get( "amplitude", "amplitude" );
        frequency_str = FlexSimStrings.get( "frequency", "frequency" );
        speed_str = FlexSimStrings.get( "speed", "speed" );
        c_str = FlexSimStrings.get( "c", "c");
    }

    private function pauseUnPause():void{
        if( myFieldModel.paused ){
            this.myFieldModel.paused = false;
        }else{
            this.myFieldModel.paused = true;
        }
    }//end pauseUnPause

    private function stopCharge():void{
        this.myFieldModel.stopCharge();
        this.setSliderVisiblity();
    }

    private function centerCharge():void{
        this.myFieldModel.paused = false;
        this.myFieldModel.centerCharge();
        this.setSliderVisiblity();
    }

    //restart() used in linear motion mode
    private function restart():void{
        this.myFieldModel.restartCharge();
    }

    private function comboBoxListener( evt: DropdownEvent ):void{
        var choice:String = evt.currentTarget.selectedItem;
        if( choice == userChoice_str ){
            this.myFieldModel.setMotion( 0 );
        }else if( choice == linear_str ){

            if( isNaN(this.speedSlider.getVal()) ){
                this.myFieldModel.setBeta( 0.5 );
                this.speedSlider.setSliderWithoutAction( 0.5 );
            }
            this.myFieldModel.setMotion( 1 );
        }else if( choice == sinusoid_str ){ this.myFieldModel.setMotion( 2 );
        }else if( choice == circular_str ){ this.myFieldModel.setMotion( 3 );
        }else if( choice == sawTooth_str ){ this.myFieldModel.setMotion( 4 );
        }else if( choice == random_str ){ this.myFieldModel.setMotion( 5 );
        }else {
            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
        }
        this.setSliderVisiblity()
    }//end comboBoxListener

    private function setSliderVisiblity():void{
        amplitudeSlider_UI.visible = false;
        frequencySlider_UI.visible = false;
        speedSlider_UI.visible = false;
        restartButton_UI.visible = false;
        amplitudeSlider_UI.includeInLayout = false;
        frequencySlider_UI.includeInLayout = false;
        speedSlider_UI.includeInLayout = false;
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
        } else if( choice == 2 || choice == 3 || choice == 4 ){   //sinusoidal or circular or sawtooth
            amplitudeSlider_UI.visible = true;
            frequencySlider_UI.visible = true;
            amplitudeSlider_UI.includeInLayout = true;
            frequencySlider_UI.includeInLayout = true;
            amplitudeSlider.setSliderWithoutAction( myFieldModel.amplitude );
            frequencySlider.setSliderWithoutAction( myFieldModel.frequency );
        } else if( choice == 6 ){     //random walk
            //do nothing
        }
    }//end setSliderVisibility()

    private function setAmplitude():void{
        var ampl:Number = amplitudeSlider.getVal();
        myFieldModel.setAmplitude( ampl );
    }

    private function setFrequency():void{
        var freq:Number = frequencySlider.getVal();
        myFieldModel.setFrequency( freq );
    }

    private function setSpeed():void{
        var beta:Number = speedSlider.getVal();
        myFieldModel.setBeta( beta );
    }
} //end class
} //end package
