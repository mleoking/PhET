/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/11/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.control {
import edu.colorado.phet.flashcommon.controls.HorizontalSlider;
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flashcommon.controls.NiceRadioButton;
import edu.colorado.phet.flashcommon.controls.NiceRadioButtonGroup;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.radiatingcharge.model.FieldModel;
import edu.colorado.phet.radiatingcharge.view.MainView;

import flash.display.Sprite;
import flash.events.Event;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.CheckBox;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.events.ItemClickEvent;

/**
 * Control Panel for Radiating Charge sim
 */

public class ControlPanel extends Canvas {
    private var myMainView:MainView;
    private var myFieldModel:FieldModel;
    private var background:VBox;
    private var firstPanel: VBox;            //main control panel, contains radio buttons for motion type
    private var secondPanel: VBox;
    private var pauseButton:NiceButton2;
    private var stopButton:NiceButton2;
    private var resetAllButton:NiceButton2;
    private var restartButton:NiceButton2;
    private var restartButton_UI:SpriteUIComponent;
    private var bumpButton:NiceButton2;
    private var bumpButton_UI:SpriteUIComponent;

    private var radioGroupVBox: VBox;        //inside main control panel
//    public var presetMotion_rgb: RadioButtonGroup;
//    private var manualNoFricton_rb: RadioButton;
//    private var manualWithFricton_rb: RadioButton;
//    private var linear_rb: RadioButton;
//    private var sinusoidal_rb: RadioButton;
//    private var circular_rb : RadioButton;
//    private var bump_rb: RadioButton;

    private var presetMotion_nrbg: NiceRadioButtonGroup;
    private var manualNoFricton_nrb: NiceRadioButton;
    private var manualWithFricton_nrb: NiceRadioButton;
    private var linear_nrb: NiceRadioButton;
    private var sinusoidal_nrb: NiceRadioButton;
    private var circular_nrb : NiceRadioButton;
    private var bump_nrb: NiceRadioButton;

    private var selectedMotionControlsVBox: VBox;   //inside main control panel
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

    //Show Velocity controls
    private var speedIndicatorVBox:VBox;            //outside main control panel
    private var speedIndicatorContainer: Sprite;
    private var speedIndicatorContainer_UI: SpriteUIComponent;
    private var showVelocity_cb: CheckBox;
    private var showVelocity_str: String;

    //internationalized strings
    private var pause_str:String;
    private var play_str:String;

    //private var start_str:String;
    private var stop_str:String;
    private var restart_str:String;
    private var reset_str:String;
    private var c_str:String;
    private var duration_str:String;

    //radio button choices
    private var manualWithFricton_str:String;
    private var manualNoFricton_str:String;
    private var linear_str:String;
    private var sinusoid_str:String;
    private var circular_str:String;
    private var bump_str:String;

    //slider labels
    private var amplitude_str:String;
    private var frequency_str:String;
    private var speed_str:String;



    public function ControlPanel( mainView:MainView, model:FieldModel ) {
        super();
        this.myMainView = mainView;
        this.myFieldModel = model;
        //this.myFieldModel.registerView( this );
        this.init();
    }

    private function init():void {
        this.initializeStrings();
        this.background = new VBox();
        this.firstPanel = new VBox();
        with( background ){
            setStyle( "verticalGap", 10 );
        }
        with ( this.firstPanel ) {
            setStyle( "backgroundColor", 0x000000 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x00ff00 );  //0x009900
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }

        var secondPanel: VBox = new VBox();
        with(secondPanel){
            setStyle( "backgroundColor", 0x000000 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x00ff00 );  //0x009900
            setStyle( "cornerRadius", 7 );
            setStyle( "borderThickness", 3 );
            setStyle( "paddingTop", 2 );
            setStyle( "paddingBottom", 2 );
            setStyle( "paddingRight", 2 );
            setStyle( "paddingLeft", 2 );
            setStyle( "verticalGap", 0 );
            setStyle( "horizontalAlign", "center" );
        }

        this.pauseButton = new NiceButton2( 80, 25, pause_str, pauseUnPause, 0xffff00, 0x000000 );
        this.stopButton = new NiceButton2( 80, 25, stop_str, stopCharge, 0xff0000, 0xffffff )
        this.restartButton = new NiceButton2( 80, 25, restart_str, restart, 0x00ff00, 0x000000 );
        this.bumpButton = new NiceButton2( 80, 25, bump_str, bumpCharge, 0x00ff00, 0x000000 );
        this.resetAllButton = new NiceButton2( 80, 25, reset_str, resetAll, 0xffa500, 0x000000 )

        radioGroupVBox = new VBox();
        radioGroupVBox.setStyle( "align", "left" );
        radioGroupVBox.setStyle( "verticalPadding", 0 );
//        presetMotion_rgb = new RadioButtonGroup();
//        manualNoFricton_rb = new RadioButton();
//        manualWithFricton_rb = new RadioButton();
//        linear_rb = new RadioButton();
//        sinusoidal_rb = new RadioButton();
//        circular_rb = new RadioButton();
//        bump_rb = new RadioButton();

        presetMotion_nrbg = new NiceRadioButtonGroup();
        manualWithFricton_nrb = new NiceRadioButton( manualWithFricton_str, true );
        manualNoFricton_nrb = new NiceRadioButton( manualNoFricton_str, false );
        linear_nrb = new NiceRadioButton( linear_str, false );
        sinusoidal_nrb = new NiceRadioButton( sinusoid_str, false );
        circular_nrb = new NiceRadioButton( circular_str, false );
        bump_nrb = new NiceRadioButton( bump_str, false );

//        initializeRadioButton( manualWithFricton_rb, manualWithFricton_str, 0, true );
//        initializeRadioButton( manualNoFricton_rb, manualNoFricton_str, 1, false );
//        initializeRadioButton( linear_rb, linear_str, 2, false );
//        initializeRadioButton( sinusoidal_rb, sinusoid_str, 3, false );
//        initializeRadioButton( circular_rb, circular_str, 4, false );
//        initializeRadioButton( bump_rb, bump_str, 5, false );

        initializeNiceRadioButton( manualWithFricton_nrb );
        initializeNiceRadioButton( manualNoFricton_nrb );
        initializeNiceRadioButton( linear_nrb );
        initializeNiceRadioButton( sinusoidal_nrb );
        initializeNiceRadioButton( circular_nrb );
        initializeNiceRadioButton( bump_nrb );

        /**
         * NOTE: Despite what flex documentation says, the itemClick event is only dispatched when an clicking on a *new* item.
         * Repeated clicking on the same radio button does not produce repeated itemClick events, which is my desired behavior.
         * I know of no way to get that desired behavior.
         */

//        presetMotion_rgb.addEventListener( ItemClickEvent.ITEM_CLICK, radioGroupListener );   //works same as Event.CHANGE
        presetMotion_nrbg.setListener( this );

        selectedMotionControlsVBox = new VBox();
        with(selectedMotionControlsVBox){
            setStyle( "backgroundColor", 0x000000 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x00ff00 );  //0x009900
            setStyle( "cornerRadius", 5 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 3 );
            setStyle( "paddingBottom", 3 );
            setStyle( "paddingRight", 3 );
            setStyle( "paddingLeft", 3 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }
        var sliderWidth: Number = 100;
        minAmplitudeOscillatory = 0;
        maxAmplitudeOscillatory = 10;
        minAmplitudeBump = 1;
        maxAmplitudeBump = 10;
        amplitudeSlider = new HorizontalSlider( setAmplitude, sliderWidth, minAmplitudeOscillatory, maxAmplitudeOscillatory );
        frequencySlider = new HorizontalSlider( setFrequency, sliderWidth, 0, 3 );
        durationSlider = new HorizontalSlider( setDuration, sliderWidth, 0.01, 1 );
        amplitudeSlider.setTextColor( 0xffffff );       //white text against black panel firstPanel
        frequencySlider.setTextColor( 0xffffff );
        durationSlider.setTextColor( 0xffffff );
        amplitudeSlider.drawKnob( 0x00ff00, 0x009900 );  //lighter color than default
        frequencySlider.drawKnob( 0x00ff00, 0x009900 );
        durationSlider.drawKnob( 0x00ff00, 0x009900 );
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
        durationSlider.removeReadout();


        var showVelocityHBox: HBox = new HBox();
        showVelocity_cb = new CheckBox();

        showVelocity_cb.addEventListener( Event.CHANGE, showVelocityListener );
        var showVelocityLabel: NiceLabel = new NiceLabel( 15, showVelocity_str, false, showVelocity_cb );
        showVelocityLabel.setFontColor( 0xffffff );
        //showVelocityLabel.drawBounds();

        //layout controls
        this.addChild( background );
        background.addChild( firstPanel );
        firstPanel.addChild( new SpriteUIComponent( pauseButton, true ) );
        firstPanel.addChild( new SpriteUIComponent( stopButton, true ) );
        firstPanel.addChild( radioGroupVBox );
        //radio buttons
        this.firstPanel.addChild( radioGroupVBox );
//        this.radioGroupVBox.addChild( manualWithFricton_rb );
//        this.radioGroupVBox.addChild( manualNoFricton_rb );
//        this.radioGroupVBox.addChild( linear_rb );
//        this.radioGroupVBox.addChild( sinusoidal_rb );
//        this.radioGroupVBox.addChild( circular_rb );
//        this.radioGroupVBox.addChild( bump_rb );

        //nice radio buttons
        this.radioGroupVBox.addChild( new SpriteUIComponent( manualWithFricton_nrb ) );
        this.radioGroupVBox.addChild( new SpriteUIComponent( manualNoFricton_nrb ) );
        this.radioGroupVBox.addChild( new SpriteUIComponent( linear_nrb ) );
        this.radioGroupVBox.addChild( new SpriteUIComponent( sinusoidal_nrb ) );
        this.radioGroupVBox.addChild( new SpriteUIComponent( circular_nrb ) );
        this.radioGroupVBox.addChild( new SpriteUIComponent( bump_nrb ) );

        //subcontrols for specific radio button choices
        firstPanel.addChild( selectedMotionControlsVBox );
        amplitudeSlider_UI = new SpriteUIComponent( amplitudeSlider, true );
        selectedMotionControlsVBox.addChild( amplitudeSlider_UI );
        frequencySlider_UI = new SpriteUIComponent( frequencySlider, true );
        selectedMotionControlsVBox.addChild( frequencySlider_UI );
        speedSlider_UI = new SpriteUIComponent( speedSlider, true );
        selectedMotionControlsVBox.addChild( speedSlider_UI );
        durationSlider_UI = new SpriteUIComponent( durationSlider, true );
        selectedMotionControlsVBox.addChild( durationSlider_UI );
        restartButton_UI = new SpriteUIComponent( restartButton, true );
        selectedMotionControlsVBox.addChild( restartButton_UI );
        bumpButton_UI = new SpriteUIComponent( bumpButton, true );
        selectedMotionControlsVBox.addChild( bumpButton_UI );
        //Show Velocity control in separate panel
        background.addChild( secondPanel );
        secondPanel.addChild( showVelocityHBox );
        showVelocityHBox.addChild( showVelocity_cb );
        showVelocityHBox.addChild( new SpriteUIComponent( showVelocityLabel, true ) );
        speedIndicatorContainer = myMainView.myVelocityArrowView.speedIndicatorContainer;
        speedIndicatorContainer_UI = new SpriteUIComponent( speedIndicatorContainer, true );
        secondPanel.addChild( speedIndicatorContainer_UI );
        myMainView.myVelocityArrowView.velocityArrow.visible = showVelocity_cb.selected;       //start with velocity arrow invisible


        setVisibilityOfControls();

        addChild( new SpriteUIComponent( resetAllButton ) );
        resetAllButton.x = 0.1 * myMainView.stageW;
        resetAllButton.y = 0.9 * myMainView.stageH;
        //this.checkRadioButtonBounds();
    }//end init()

    //Testing only
//    private function checkRadioButtonBounds():void{
//        trace("ControlPanel line 225.  radioButton height = " + this.bump_rb.height );
//    }

    private function initializeStrings():void{
        pause_str = FlexSimStrings.get( "pause", "Pause" );
        play_str = FlexSimStrings.get( "play", "Play" );
        stop_str = FlexSimStrings.get( "stopCharge", "Stop Charge" );
        restart_str = FlexSimStrings.get( "restart", "Restart" );
        reset_str = FlexSimStrings.get("reset", "Reset");
        manualNoFricton_str = FlexSimStrings.get( "noFrictionManual", "No Friction" );
        manualWithFricton_str = FlexSimStrings.get( "frictionManual", "Manual");
        linear_str = FlexSimStrings.get( "linear", "Linear" );
        sinusoid_str = FlexSimStrings.get( "sinusoid", "Sinusoid" );
        circular_str = FlexSimStrings.get( "circular", "Circular" );
        bump_str = FlexSimStrings.get( "bump", "Bump" );
        //random_str = FlexSimStrings.get( "random", "Random" );
        amplitude_str = FlexSimStrings.get( "amplitude", "amplitude" );
        frequency_str = FlexSimStrings.get( "frequency", "frequency" );
        speed_str = FlexSimStrings.get( "speed", "speed" );
        c_str = FlexSimStrings.get( "speedOfLight", "c");
        duration_str = FlexSimStrings.get( "duration", "duration" );
        showVelocity_str = FlexSimStrings.get ("showVelocity", "Show velocity  " );
    }

//    private function initializeRadioButton( rb:RadioButton, lbl:String,  value: int, selected: Boolean ):void{
//        rb.group = presetMotion_rgb;
//        rb.labelPlacement = "right";
//        rb.label = lbl;// + "   ";
//        rb.value = value;
//        rb.selected = selected;
//        rb.setStyle( "color", 0xffffff );
//        rb.setStyle( "textRollOverColor", 0xffff00 );
//        rb.setStyle( "textSelectedColor", 0xffff00 );
//        rb.setStyle( "iconColor", 0xffffff );
//        rb.setStyle( "fontSize", 16 );
//        //rb.setStyle( "disabledIconColor", 0x000000 );   //doesn't work
//    }

    private function initializeNiceRadioButton( nrb: NiceRadioButton ):void{
        nrb.group = presetMotion_nrbg;
        nrb.label.setFontColor( 0xffffff );
    }

    private function pauseUnPause():void{
        if( myFieldModel.paused ){
            this.myFieldModel.paused = false;
            this.pauseButton.setLabel( pause_str );
        }else{
            this.myFieldModel.paused = true;
            this.pauseButton.setLabel( play_str );
        }
    }//end pauseUnPause


    private function stopCharge():void{
        this.setDefaultRadioButton();
        this.myFieldModel.stopCharge();
        this.setVisibilityOfControls();
    }

    public function resetAll():void{
        this.myFieldModel.paused = false;
        this.pauseButton.setLabel( pause_str );
        this.myFieldModel.centerCharge();
        this.myFieldModel.initializeAmplitudeAndFrequency();
        this.closeShowVelocityPanel();
        this.setVisibilityOfControls();
        this.setDefaultRadioButton();
    }

    //restart() used in linear motion mode
    private function restart():void{
        this.myFieldModel.restartCharge();
    }

    private function bumpCharge():void{
        this.myFieldModel.bumpCharge( durationSlider.getVal() );
    }

    private function setDefaultRadioButton():void{
        presetMotion_nrbg.selectButtonWithoutAction( manualWithFricton_nrb );
    }

//    private function radioGroupListener( evt: ItemClickEvent ):void{
//        this.myFieldModel.paused = false;
//        this.pauseButton.setLabel( pause_str );
//        var choice:Object = presetMotion_rgb.selectedValue;
//        //trace("ControlPanel.radioGroupListener  event = " + evt );
//        //trace("ControlPanel.radioGroupListener  selectedValue = " + presetMotion_rgb.selectedValue );
//        //this.myFieldModel.setTypeOfMotion( int( choice ) );
//        if( choice == 0 ){
//        }else if( choice == 1 ){
//        }else if( choice == 2 ){
//            if( isNaN(this.speedSlider.getVal()) ){
//                //this.myFieldModel.setBeta( 0.5 );
//                this.speedSlider.setSliderWithoutAction( 0.5 );
//            }
//        }else if( choice == 3 ){
//            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
//        }else if( choice == 4 ){
//            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
//        }else if( choice == 5 ){
////            if( isNaN(this.durationSlider.getVal()) ){
////                this.myFieldModel.bumpDuration = 0.5;
////                this.durationSlider.setSliderWithoutAction( 0.5 );
////            }
//            this.amplitudeSlider.minVal = this.minAmplitudeBump;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
//        }else if( choice == 6 ){ this.myFieldModel.setTypeOfMotion( 6 );
//        }else {
//            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
//        }
//        this.myFieldModel.setTypeOfMotion( int( choice ) );
//        this.setVisibilityOfControls();
////        switch( choice ){
////            case 2:
////                if( isNaN(this.speedSlider.getVal()) ){
////                    this.speedSlider.setSliderWithoutAction( 0.5 );
////                }
////                break;
////            case 3:
////                this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
////                this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
////                break;
////            case 4:
////                this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
////                this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
////                break;
////            case 5:
////                this.amplitudeSlider.minVal = this.minAmplitudeBump;
////                this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
////                break;
////        }//end switch( choice )
//    }//end radioGroupListener

    public function niceRadioGroupListener( selectedButtonIndex: int ):void{
        this.myFieldModel.paused = false;
        this.pauseButton.setLabel( pause_str );
        var choice:int = selectedButtonIndex;
        //trace("ControlPanel.radioGroupListener  event = " + evt );
        //trace("ControlPanel.radioGroupListener  selectedValue = " + presetMotion_rgb.selectedValue );
        //this.myFieldModel.setTypeOfMotion( int( choice ) );
        if( choice == 0 ){
            //do nothing
        }else if( choice == 1 ){
        }else if( choice == 2 ){
            if( isNaN(this.speedSlider.getVal()) ){
                //this.myFieldModel.setBeta( 0.5 );
                this.speedSlider.setSliderWithoutAction( 0.5 );
            }
        }else if( choice == 3 ){
            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
        }else if( choice == 4 ){
            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
        }else if( choice == 5 ){
//            if( isNaN(this.durationSlider.getVal()) ){
//                this.myFieldModel.bumpDuration = 0.5;
//                this.durationSlider.setSliderWithoutAction( 0.5 );
//            }
            this.amplitudeSlider.minVal = this.minAmplitudeBump;
            this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
        }else if( choice == 6 ){ this.myFieldModel.setTypeOfMotion( 6 );
        }else {
            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
        }
        this.myFieldModel.setTypeOfMotion( int( choice ) );
        this.setVisibilityOfControls();
    }//end NiceRadioGroupListener

    private function showVelocityListener( evt: Event ):void{
        var selected:Boolean = evt.target.selected;
        speedIndicatorContainer.visible = selected;
        //speedOfLightArrow.visible = selected;
        myMainView.myVelocityArrowView.velocityArrow.visible = selected;
        //myMainView.myVelocityArrowView.setVisibilityOfVelocityArrow( selected );
        setVisibilityOfControls();
        //trace("ControlPanel.showVelocityListener selected = " + selected );
    }

    //Called from resetAll()
    private function closeShowVelocityPanel():void{
        showVelocity_cb.selected = false;
        speedIndicatorContainer.visible = false;
        myMainView.myVelocityArrowView.velocityArrow.visible = false;
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
        //Begin with all controls invisible and then make selected controls visible
        selectedMotionControlsVBox.visible = false;
        amplitudeSlider_UI.visible = false;
        frequencySlider_UI.visible = false;
        durationSlider_UI.visible = false;
        speedSlider_UI.visible = false;
        restartButton_UI.visible = false;
        bumpButton_UI.visible = false;
        speedIndicatorContainer_UI.visible = false;
        //speedOLightArrow_UI.visible = false;
        selectedMotionControlsVBox.includeInLayout = false;
        amplitudeSlider_UI.includeInLayout = false;
        frequencySlider_UI.includeInLayout = false;
        durationSlider_UI.includeInLayout = false;
        speedSlider_UI.includeInLayout = false;
        bumpButton_UI.includeInLayout = false;
        restartButton_UI.includeInLayout = false;
        speedIndicatorContainer_UI.includeInLayout = false;
        //speedOLightArrow_UI.includeInLayout = false;
        //var choice:int = myComboBox.selectedIndex;
        //var choice: Object = presetMotion_rgb.selectedValue;
        var choice: int = presetMotion_nrbg.selectedIndex;
        if( choice == 0 || choice == 1){      //user-controlled
            //do nothing
        }else if( choice == 2 ){  //linear
            selectedMotionControlsVBox.visible = true;
            speedSlider_UI.visible = true;
            restartButton_UI.visible = true;
            selectedMotionControlsVBox.includeInLayout = true;
            speedSlider_UI.includeInLayout = true;
            restartButton_UI.includeInLayout = true;
            speedSlider.setSliderWithoutAction( myFieldModel.getBeta() );
        } else if( choice == 3 || choice == 4 ){   //sinusoidal or circular or sawtooth
            selectedMotionControlsVBox.visible = true;
            amplitudeSlider_UI.visible = true;
            frequencySlider_UI.visible = true;
            selectedMotionControlsVBox.includeInLayout = true;
            amplitudeSlider_UI.includeInLayout = true;
            frequencySlider_UI.includeInLayout = true;
            amplitudeSlider.setSliderWithoutAction( myFieldModel.amplitude );
            frequencySlider.setSliderWithoutAction( myFieldModel.frequency );
        } else if( choice == 5 ){ //bump
            selectedMotionControlsVBox.visible = true;
            amplitudeSlider_UI.visible = true;
            durationSlider_UI.visible = true;
            bumpButton_UI.visible = true;
            selectedMotionControlsVBox.includeInLayout = true;
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
            speedIndicatorContainer_UI.visible = true;
            speedIndicatorContainer_UI.includeInLayout = true;
            //speedOLightArrow_UI.visible = true;
            //speedOLightArrow_UI.includeInLayout = true;
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

//    public function update():void{
//        if(myFieldModel.motionType == myFieldModel.STOPPING ){
//            if( presetMotion_nrbg.selectedIndex != 0 ) {
//                this.setDefaultRadioButton();
//            }
//            //trace("ControlPanel.update() called.");
//        }
//    }
} //end class
} //end package
