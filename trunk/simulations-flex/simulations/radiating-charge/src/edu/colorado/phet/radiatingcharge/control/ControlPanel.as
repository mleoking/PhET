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
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.radiatingcharge.model.FieldModel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.radiatingcharge.view.MainView;

import flash.display.Sprite;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.CheckBox;
import mx.controls.ComboBox;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.core.UIComponent;
import mx.events.DropdownEvent;
import mx.events.FlexEvent;
import mx.events.ItemClickEvent;
import mx.skins.halo.HaloBorder;

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

    private var radioGroupVBox: VBox;
    public var presetMotion_rgb: RadioButtonGroup;
    private var manual_rb: RadioButton;
    private var friction_rb: RadioButton;
    private var linear_rb: RadioButton;
    private var sinusoidal_rb: RadioButton;
    private var circular_rb : RadioButton;
    private var bump_rb: RadioButton;

    //Show Velocity controls

//    public var myComboBox: ComboBox;
//    private var choiceList_arr:Array;

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
    private var speedOfLightArrow: Sprite;
    private var speedOLightArrow_UI: SpriteUIComponent;
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
    //private var moreSpeedLessRez_str:String;
    //Drop-down menu choices
    private var userChoice_str:String;
    private var frictionManual_str:String;
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
    //show velocity label
    private var showVelocity_cb: CheckBox;
    private var showVelocity_str: String;


    public function ControlPanel( mainView:MainView, model:FieldModel ) {
        super();
        this.myMainView = mainView;
        this.myFieldModel = model;
        this.init();
    }

    private function init():void {
        this.initializeStrings();

        this.background = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x000000 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x00ff00 );  //0x009900
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 15 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }

        this.pauseButton = new NiceButton2( 80, 25, pause_str, pauseUnPause, 0xffff00, 0x000000 );
        this.stopButton = new NiceButton2( 80, 25, stop_str, stopCharge, 0xff0000, 0xffffff )
        this.resetButton = new NiceButton2( 80, 25, reset_str, resetCharge, 0xffa500, 0x000000 )
        this.restartButton = new NiceButton2( 80, 25, restart_str, restart, 0x00ff00, 0x000000 );
        this.bumpButton = new NiceButton2( 80, 25, bump_str, bumpCharge, 0x00ff00, 0x000000 );

//        this.myComboBox = new ComboBox();
//        myComboBox.width = 100;
//        this.choiceList_arr = new Array( );
//        choiceList_arr = [ userChoice_str, linear_str, sinusoid_str, circular_str, bump_str, random_str ];
//        myComboBox.dataProvider = choiceList_arr;
//        myComboBox.addEventListener( DropdownEvent.CLOSE, comboBoxListener );

        radioGroupVBox = new VBox();
        radioGroupVBox.setStyle( "align", "left" );
        radioGroupVBox.setStyle( "verticalPadding", 0 );
        presetMotion_rgb = new RadioButtonGroup();
        manual_rb = new RadioButton();
        friction_rb = new RadioButton();
        linear_rb = new RadioButton();
        sinusoidal_rb = new RadioButton();
        circular_rb = new RadioButton();
        bump_rb = new RadioButton();

        initializeRadioButton( friction_rb, frictionManual_str, 0, true );
        initializeRadioButton( manual_rb, userChoice_str, 1, false );
        initializeRadioButton( linear_rb, linear_str, 2, false );
        initializeRadioButton( sinusoidal_rb, sinusoid_str, 3, false );
        initializeRadioButton( circular_rb, circular_str, 4, false );
        initializeRadioButton( bump_rb, bump_str, 5, false );


        presetMotion_rgb.addEventListener( Event.CHANGE, radioGroupListener );

        var sliderWidth: Number = 100;
        minAmplitudeOscillatory = 0;
        maxAmplitudeOscillatory = 10;
        minAmplitudeBump = 1;
        maxAmplitudeBump = 10;
        amplitudeSlider = new HorizontalSlider( setAmplitude, sliderWidth, minAmplitudeOscillatory, maxAmplitudeOscillatory );
        frequencySlider = new HorizontalSlider( setFrequency, sliderWidth, 0, 3 );
        durationSlider = new HorizontalSlider( setDuration, sliderWidth, 0.01, 1 );
        amplitudeSlider.setTextColor( 0xffffff );       //white text against black panel background
        frequencySlider.setTextColor( 0xffffff );
        durationSlider.setTextColor( 0xffffff );
        amplitudeSlider.drawKnob( 0x00ff00, 0x009900 );  //lighter color than default
        frequencySlider.drawKnob( 0x00ff00, 0x009900 );
        durationSlider.drawKnob( 0x00ff00, 0x009900 );
        var c: Number = myFieldModel.getSpeedOfLight();
        speedSlider = new HorizontalSlider( setSpeed, sliderWidth, 0.1, 0.95 );

        amplitudeSlider.setLabelText( amplitude_str );
        amplitudeSlider.removeReadout();
        frequencySlider.setLabelText( frequency_str );
        frequencySlider.removeReadout();
        speedSlider.setLabelText( speed_str );
        speedSlider.setUnitsText( c_str );
        speedSlider.setReadoutPrecision( 2 );
        speedSlider.setTextColor( 0xffffff );
        speedSlider.drawKnob( 0x00ff00, 0x009900 );
        durationSlider.setLabelText( duration_str );
        //durationSlider.setUnitsText( s_str );
        durationSlider.removeReadout();
//        moreSpeedCheckBox = new CheckBox();
//        moreSpeedCheckBox.label = moreSpeedLessRez_str;
//        moreSpeedCheckBox.addEventListener( Event.CHANGE, radioButtonListener );

        var showVelocityVBox: VBox = new VBox();
        showVelocityVBox.setStyle ( "verticalGap", 10 );
        var showVelocityHBox: HBox = new HBox();
        showVelocity_cb = new CheckBox();

        showVelocity_cb.addEventListener( Event.CHANGE, showVelocityListener );
        var showVelocityLabel: NiceLabel = new NiceLabel( 15, showVelocity_str, false, showVelocity_cb );
        showVelocityLabel.setFontColor( 0xffffff );
        //showVelocityLabel.drawBounds();

        //layout controls
        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( pauseButton, true ) );
        this.background.addChild( new SpriteUIComponent( stopButton, true ) );
        //this.background.addChild( new SpriteUIComponent( resetButton, true ) );

        //this.background.addChild( myComboBox );
        this.background.addChild( radioGroupVBox );
        this.radioGroupVBox.addChild( friction_rb );
        this.radioGroupVBox.addChild( manual_rb );
        this.radioGroupVBox.addChild( linear_rb );
        this.radioGroupVBox.addChild( sinusoidal_rb );
        this.radioGroupVBox.addChild( circular_rb );
        this.radioGroupVBox.addChild( bump_rb );

        this.amplitudeSlider_UI = new SpriteUIComponent( amplitudeSlider, true );
        this.background.addChild( amplitudeSlider_UI );
        this.frequencySlider_UI = new SpriteUIComponent( frequencySlider, true );
        this.background.addChild( frequencySlider_UI );
        this.speedSlider_UI = new SpriteUIComponent( speedSlider, true );
        this.background.addChild( speedSlider_UI );
        this.durationSlider_UI = new SpriteUIComponent( durationSlider, true );
        this.background.addChild( durationSlider_UI );
        this.restartButton_UI = new SpriteUIComponent( restartButton, true );
        this.background.addChild( restartButton_UI );
        this.bumpButton_UI = new SpriteUIComponent( bumpButton, true );
        this.background.addChild( bumpButton_UI );


        this.background.addChild( showVelocityVBox );
        showVelocityVBox.addChild( showVelocityHBox );
        showVelocityHBox.addChild( showVelocity_cb );
        showVelocityHBox.addChild( new SpriteUIComponent( showVelocityLabel, true ) );
        speedOfLightArrow = myMainView.myVelocityArrowView.speedOfLightArrow;
        speedOLightArrow_UI = new SpriteUIComponent( speedOfLightArrow );
        showVelocityVBox.addChild( speedOLightArrow_UI );
        myMainView.myVelocityArrowView.velocityArrow.visible = showVelocity_cb.selected;       //start with velocity arrow invisible


        this.setVisibilityOfControls();

        this.addChild( new SpriteUIComponent( resetButton ) );
        resetButton.x = 0.1 * myMainView.stageW;
        resetButton.y = 0.9 * myMainView.stageH;
//        this.background.addChild( moreSpeedCheckBox );
        //this.checkRadioButtonBounds();
    }//end init()

    //Testing only
//    private function checkRadioButtonBounds():void{
//        trace("ControlPanel line 225.  radioButton height = " + this.bump_rb.height );
//    }

    private function initializeStrings():void{
        pause_str = FlexSimStrings.get( "pause", "Pause" );
        unPause_str = FlexSimStrings.get( "unPause", "UnPause" );
        stop_str = FlexSimStrings.get( "stop", "Stop" );
        restart_str = FlexSimStrings.get( "restart", "Restart" );
        reset_str = FlexSimStrings.get("reset", "Reset");
        userChoice_str = FlexSimStrings.get( "noFrictionManual", "No Friction" );
        frictionManual_str = FlexSimStrings.get( "frictionManual", "Manual");
        linear_str = FlexSimStrings.get( "linear", "Linear" );
        sinusoid_str = FlexSimStrings.get( "sinusoid", "Sinusoid" );
        circular_str = FlexSimStrings.get( "circular", "Circular" );
        bump_str = FlexSimStrings.get( "bump", "Bump" );
        random_str = FlexSimStrings.get( "random", "Random" );
        amplitude_str = FlexSimStrings.get( "amplitude", "amplitude" );
        frequency_str = FlexSimStrings.get( "frequency", "frequency" );
        speed_str = FlexSimStrings.get( "speed", "speed" );
        c_str = FlexSimStrings.get( "speedOfLight", "c");
        duration_str = FlexSimStrings.get( "duration", "duration" );
        showVelocity_str = FlexSimStrings.get ("showVelocity", "Show velocity" );
        //moreSpeedLessRez_str = FlexSimStrings.get("moreSpeedLessRez", "less rez")
    }

    private function initializeRadioButton( rb:RadioButton, lbl:String,  value: int, selected: Boolean ):void{
        rb.group = presetMotion_rgb;
        rb.labelPlacement = "right";
        rb.label = lbl;// + "   ";
        rb.value = value;
        rb.selected = selected;
        rb.setStyle( "color", 0xffffff );
        rb.setStyle( "textRollOverColor", 0xffff00 );
        rb.setStyle( "textSelectedColor", 0xffff00 );
        rb.setStyle( "iconColor", 0xffffff );
        rb.setStyle( "fontSize", 16 );

        //rb.setStyle( "disabledIconColor", 0x000000 );   //doesn't work
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
        this.setVisibilityOfControls();
    }

    public function resetCharge():void{
        this.myFieldModel.paused = false;
        this.myFieldModel.centerCharge();
        this.setVisibilityOfControls();
    }

    //restart() used in linear motion mode
    private function restart():void{
        this.myFieldModel.restartCharge();
    }

    private function bumpCharge():void{
        this.myFieldModel.bumpCharge( durationSlider.getVal() );
    }
    //OBSOLETE function
//    private function comboBoxListener( evt: DropdownEvent ):void{
//        this.myFieldModel.paused = false;
//        this.pauseButton.setLabel( pause_str );
//        var choice:String = evt.currentTarget.selectedItem;
//        if( choice == userChoice_str ){
//            this.myFieldModel.setTypeOfMotion( 0 );
//        }else if( choice == frictionManual_str ){
//            this.myFieldModel.setTypeOfMotion( 1 );
//        }else if( choice == linear_str ){
//            if( isNaN(this.speedSlider.getVal()) ){
//                this.myFieldModel.setBeta( 0.5 );
//                this.speedSlider.setSliderWithoutAction( 0.5 );
//            }
//            this.myFieldModel.setTypeOfMotion( 2 );
//        }else if( choice == sinusoid_str ){
//            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
//            this.myFieldModel.setTypeOfMotion( 3 );
//        }else if( choice == circular_str ){
//            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
//            this.myFieldModel.setTypeOfMotion( 4 );
//        }else if( choice == bump_str ){
////            if( isNaN(this.durationSlider.getVal()) ){
////                this.myFieldModel.bumpDuration = 0.5;
////                this.durationSlider.setSliderWithoutAction( 0.5 );
////            }
//            this.amplitudeSlider.minVal = this.minAmplitudeBump;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
//            this.myFieldModel.setTypeOfMotion( 5 );
//        }else if( choice == random_str ){ this.myFieldModel.setTypeOfMotion( 6 );
//        }else {
//            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
//        }
//        this.setVisibilityOfControls();
//    }//end comboBoxListener

    private function radioGroupListener( evt: Event ):void{
        this.myFieldModel.paused = false;
        this.pauseButton.setLabel( pause_str );
        var choice:Object = presetMotion_rgb.selectedValue;
        //trace("ControlPanel.radioGroupListener  selectedValue = " + presetMotion_rgb.selectedValue );
        //this.myFieldModel.setTypeOfMotion( int( choice ) );
        if( choice == 0 ){
            //this.myFieldModel.setTypeOfMotion( 0 );
        }else if( choice == 1 ){
            //this.myFieldModel.setTypeOfMotion( 1 );
        }else if( choice == 2 ){
            if( isNaN(this.speedSlider.getVal()) ){
                //this.myFieldModel.setBeta( 0.5 );
                this.speedSlider.setSliderWithoutAction( 0.5 );
            }
            //this.myFieldModel.setTypeOfMotion( 2 );
        }else if( choice == 3 ){
            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
            //this.myFieldModel.setTypeOfMotion( 3 );
        }else if( choice == 4 ){
            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
            //this.myFieldModel.setTypeOfMotion( 4 );
        }else if( choice == 5 ){
//            if( isNaN(this.durationSlider.getVal()) ){
//                this.myFieldModel.bumpDuration = 0.5;
//                this.durationSlider.setSliderWithoutAction( 0.5 );
//            }
            this.amplitudeSlider.minVal = this.minAmplitudeBump;
            this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
            //this.myFieldModel.setTypeOfMotion( 5 );
        }else if( choice == 6 ){ this.myFieldModel.setTypeOfMotion( 6 );
        }else {
            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
        }
        this.myFieldModel.setTypeOfMotion( int( choice ) );
        this.setVisibilityOfControls();
    }//end comboBoxListener

//    private function radioButtonListener( evt: Event ) :void{
//        //trace( "ControlPanel.radioButtonListener  state = "+evt.currentTarget.selected );
//        if( evt.currentTarget.selected ) {
//            this.myMainView.topCanvas.setResolution( "LOW" );
//        }else{
//            this.myMainView.topCanvas.setResolution( "HIGH" );
//        }
//
//    }

    private function showVelocityListener( evt: Event ):void{
        var selected:Boolean = evt.target.selected;
        speedOfLightArrow.visible = selected;
        myMainView.myVelocityArrowView.velocityArrow.visible = selected;
        //myMainView.myVelocityArrowView.setVisibilityOfVelocityArrow( selected );
        setVisibilityOfControls();
        //trace("ControlPanel.showVelocityListener selected = " + selected );
    }

//    private function toggleShowVelocityCheckBox():void{
//        var selected: Boolean = showVelocity_cb.selected;
//        if( selected ){
//            showVelocity_cb.selected = false;
//        }else{
//            showVelocity_cb.selected = true;
//        }
//        showVelocity_cb.dispatchEvent( new Event(Event.CHANGE) );
//    }

    private function setVisibilityOfControls():void{
        //Begin with controls invisible and then make selected controls visible
        amplitudeSlider_UI.visible = false;
        frequencySlider_UI.visible = false;
        durationSlider_UI.visible = false;
        speedSlider_UI.visible = false;
        restartButton_UI.visible = false;
        bumpButton_UI.visible = false;
        speedOLightArrow_UI.visible = false;
        amplitudeSlider_UI.includeInLayout = false;
        frequencySlider_UI.includeInLayout = false;
        durationSlider_UI.includeInLayout = false;
        speedSlider_UI.includeInLayout = false;
        bumpButton_UI.includeInLayout = false;
        restartButton_UI.includeInLayout = false;
        speedOLightArrow_UI.includeInLayout = false;
        //var choice:int = myComboBox.selectedIndex;
        var choice: Object = presetMotion_rgb.selectedValue;
        if( choice == 0 || choice == 1){      //user-controlled
            //do nothing
        }else if( choice == 2 ){  //linear
            speedSlider_UI.visible = true;
            restartButton_UI.visible = true;
            speedSlider_UI.includeInLayout = true;
            restartButton_UI.includeInLayout = true;
            speedSlider.setSliderWithoutAction( myFieldModel.getBeta() );
        } else if( choice == 3 || choice == 4 ){   //sinusoidal or circular or sawtooth
            amplitudeSlider_UI.visible = true;
            frequencySlider_UI.visible = true;
            amplitudeSlider_UI.includeInLayout = true;
            frequencySlider_UI.includeInLayout = true;
            amplitudeSlider.setSliderWithoutAction( myFieldModel.amplitude );
            frequencySlider.setSliderWithoutAction( myFieldModel.frequency );
        } else if( choice == 5 ){ //bump
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
        } else if( choice == 6 ){//random walk
            //do nothing
        }

        var velocityArrowShown: Boolean = this.showVelocity_cb.selected;
        //trace("ControlPanel.setSliderVisibility  velocityArrowShown =  "+velocityArrowShown );
        if( velocityArrowShown ) {
            speedOLightArrow_UI.visible = true;
            speedOLightArrow_UI.includeInLayout = true;
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
