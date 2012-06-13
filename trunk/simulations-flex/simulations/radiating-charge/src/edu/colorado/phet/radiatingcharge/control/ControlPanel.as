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
import mx.events.DropdownEvent;

//Control Panel for Radiating Charge sim
public class ControlPanel extends Canvas {
    private var myMainView:MainView;
    private var myFieldModel:FieldModel;
    private var background: VBox;
    private var pauseButton:NiceButton2;
    private var stopButton:NiceButton2;
    private var centerChargeButton:NiceButton2;
    public var myComboBox: ComboBox;
    private var choiceList_arr:Array;
    private var amplitudeSlider:HorizontalSlider;
    private var frequencySlider:HorizontalSlider;
    private var speedSlider:HorizontalSlider;

    //internationalized strings
    private var start_str:String;
    private var stop_str:String;
    private var pause_str:String;
    private var centerCharge_str:String;
    private var unPause_str:String;
    //Drop-down menu choices
    private var userChoice_str:String;
    private var linear_str:String;
    private var sinusoid_str:String;
    private var circular_str:String;
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
        this.myComboBox = new ComboBox();
        this.choiceList_arr = new Array( 3 );
        choiceList_arr = [ userChoice_str, linear_str, sinusoid_str, circular_str ];
        myComboBox.dataProvider = choiceList_arr;
        myComboBox.addEventListener( DropdownEvent.CLOSE, comboBoxListener );

        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( pauseButton, true ) );
        this.background.addChild( new SpriteUIComponent( stopButton, true ) );
        this.background.addChild( new SpriteUIComponent( centerChargeButton, true ) );
        this.background.addChild( myComboBox );
    }//end init()

    private function initializeStrings():void{
        pause_str = FlexSimStrings.get( "pause", "Pause" );
        unPause_str = FlexSimStrings.get( "unPause", "UnPause" );
        start_str = FlexSimStrings.get( "start", "Start" );
        stop_str = FlexSimStrings.get( "stop", "Stop" );
        centerCharge_str = FlexSimStrings.get("centerCharge", "Center Charge");
        userChoice_str = FlexSimStrings.get( "userControlled", "User Controlled" );
        linear_str = FlexSimStrings.get( "linear", "Linear" );
        sinusoid_str = FlexSimStrings.get( "sinusoid", "Sinusoid" );
        circular_str = FlexSimStrings.get( "circular", "Circular" );
        amplitude_str = FlexSimStrings.get( "amplitude", "amplitude" );
        frequency_str = FlexSimStrings.get( "frequency", "frequency" );
        speed_str = FlexSimStrings.get( "speed", "speed" );
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
    }

    private function centerCharge():void{
        this.myFieldModel.centerCharge();
    }

    private function comboBoxListener( evt: DropdownEvent ):void{
        var choice:String = evt.currentTarget.selectedItem;
        if( choice == userChoice_str ){
            this.myFieldModel.setMotion( 0 );
        }else if( choice == linear_str ){
            this.myFieldModel.setMotion( 1 );
        }else if( choice == sinusoid_str ){
            this.myFieldModel.setMotion( 2 );
        }else if( choice == circular_str ){
            this.myFieldModel.setMotion( 3 );
        }else{
            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
        }

    }//end comboBoxListener
} //end class
} //end package
