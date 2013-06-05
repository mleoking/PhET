/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/11/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.control {
import edu.colorado.phet.flashcommon.controls.HorizontalSlider;
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flashcommon.controls.NiceRadioButton;
import edu.colorado.phet.flashcommon.controls.NiceRadioButtonGroup;
import edu.colorado.phet.flashcommon.controls.PlayPauseButton;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.triglab.view.MainView;

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
 * Control panel must be flex canvas to use flex auto-layout
 */

public class ControlPanel extends Canvas {
    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var background: VBox;
    private var cos_cb: CheckBox;
    private var sin_cb: CheckBox;
    private var tan_cb: CheckBox;
    //private var firstPanel: VBox;            //main control panel, contains radio buttons for motion type

//    private var stopButton:NiceButton2;
//    private var resetAllButton:NiceButton2;
//    private var restartButton:NiceButton2;
//    private var restartButton_UI:SpriteUIComponent;
//    private var bumpButton:NiceButton2;
//    private var bumpButton_UI:SpriteUIComponent;
//
//    private var radioGroupVBox: VBox;        //inside main control panel
//
//    private var presetMotion_nrbg: NiceRadioButtonGroup;      //Flex component radio button are too inflexible and buggy for use
//    private var manualNoFricton_nrb: NiceRadioButton;
//    private var manualWithFricton_nrb: NiceRadioButton;
//    private var linear_nrb: NiceRadioButton;




    //internationalized strings
    private var cos_str:String;
    private var sin_str:String;
    private var tan_str:String;




    public function ControlPanel( mainView:MainView, model:TrigModel ) {
        super();
        this.myMainView = mainView;
        this.myTrigModel = model;
        //this.myFieldModel.registerView( this );
        this.init();
    }

    private function init():void {
        this.initializeStrings();
        this.background = new VBox();
        //this.firstPanel = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x000000 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x00ff00 );  //0x009900
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 3 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }
        this.cos_cb = new CheckBox();
        this.sin_cb = new CheckBox();
        this.tan_cb = new CheckBox();
        cos_cb.addEventListener( Event.CHANGE, cosCheckBoxListener );
        var cosCheckBoxLabel: NiceLabel = new NiceLabel( 15, cos_str, false, cos_cb );

        //layout controls
        this.addChild( background );
        this.background.addChild( cos_cb );
        this.background.addChild( sin_cb );
        this.background.addChild( tan_cb );
//        background.addChild( firstPanel );
//        firstPanel.addChild( new SpriteUIComponent( playPauseButton, true ) );
////        firstPanel.addChild( new SpriteUIComponent( pauseButton, true ) );
//        firstPanel.addChild( new SpriteUIComponent( stopButton, true ) );
//        firstPanel.addChild( radioGroupVBox );
//        //radio buttons
//        this.firstPanel.addChild( radioGroupVBox );

//        //nice radio buttons
//        this.radioGroupVBox.addChild( new SpriteUIComponent( manualWithFricton_nrb ) );
//        this.radioGroupVBox.addChild( new SpriteUIComponent( manualNoFricton_nrb ) );
//        this.radioGroupVBox.addChild( new SpriteUIComponent( linear_nrb ) );
//        this.radioGroupVBox.addChild( new SpriteUIComponent( sinusoidal_nrb ) );
//        this.radioGroupVBox.addChild( new SpriteUIComponent( circular_nrb ) );
//        this.radioGroupVBox.addChild( new SpriteUIComponent( bump_nrb ) );

    }//end init()



    private function initializeStrings():void{
        cos_str = FlexSimStrings.get( "cos", "cos" );
        sin_str = FlexSimStrings.get( "sin", "sin" );
        tan_str = FlexSimStrings.get( "tan", "tan" );
    }

    private function cosCheckBoxListener( evt: Event ):void{
        var selected:Boolean = evt.target.selected;
//        speedIndicatorContainer.visible = selected;
//        myMainView.myVelocityArrowView.velocityArrow.visible = selected;
//        setVisibilityOfControls();
        //trace("ControlPanel.showVelocityListener selected = " + selected );
    }
//    private function initializeNiceRadioButton( nrb: NiceRadioButton ):void{
//        nrb.group = presetMotion_nrbg;
//        nrb.label.setFontColor( 0xffffff );
//    }





    public function resetAll():void{

    }


//    public function niceRadioGroupListener( selectedButtonIndex: int ):void{
//        this.myTrigModel.paused = false;
//        this.playPauseButton.paused = false;
////        this.pauseButton.setLabel( pause_str );
//        var choice:int = selectedButtonIndex;
//        //trace("ControlPanel.radioGroupListener  event = " + evt );
//        //trace("ControlPanel.radioGroupListener  selectedValue = " + presetMotion_rgb.selectedValue );
//        if( choice == 0 ){
//            //do nothing
//        }else if( choice == 1 ){
//        }else if( choice == 2 ){
//            if( isNaN(this.speedSlider.getVal()) ){
//                this.speedSlider.setSliderWithoutAction( 0.5 );
//            }
//        }else if( choice == 3 ){      //sinusoidal motion
//            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
//        }else if( choice == 4 ){      //circular motion
//            this.amplitudeSlider.minVal = this.minAmplitudeOscillatory;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeOscillatory;
//        }else if( choice == 5 ){
////            if( isNaN(this.durationSlider.getVal()) ){
////                this.myFieldModel.bumpDuration = 0.5;
////                this.durationSlider.setSliderWithoutAction( 0.5 );
////            }
//            this.amplitudeSlider.minVal = this.minAmplitudeBump;
//            this.amplitudeSlider.maxVal = this.maxAmplitudeBump;
//        }else if( choice == 6 ){ this.myTrigModel.setTypeOfMotion( 6 );
//        }else {
//            trace( "ERROR: ControlPanel.comboBoxListener() received invalid choice.") ;
//        }
//        this.myTrigModel.setTypeOfMotion( int( choice ) );
//        this.setVisibilityOfControls();
//    }//end NiceRadioGroupListener





} //end class
} //end package
