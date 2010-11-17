package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.collisionlab.view.MainView;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import fl.controls.CheckBox;
import fl.controls.RadioButton;
import fl.controls.RadioButtonGroup;
import fl.controls.Slider;
import fl.events.*;
import fl.managers.StyleManager;

import flash.display.*;
import flash.events.*;
import flash.text.*;

//List of strings in this class for internationalization:
//This class ControlPanel associated with library movieclip controlPanel
//Here are all the dynamic text strings and their english values
//controlPanel.oneD_rb.label = "1 Dimension"
//controlPanel.twoD_rb.label = "2 Dimensions"
//controlPanel.resetButton_sp.label_txt  = "Reset All"  //This is instance of class NiceButton
//controlPanel.showVelocities_cb.label = "Show vlocities"
//controlPanel.showCM_cb.label = "Show C.M."
//controlPanel.reflectingBorder_cb.label = "Reflecting Border"
//controlPane.momentaDiagram
//controlPanel.showPaths_cb.label = "Show Paths"
//controlPanel.sound_cb.label = "Sound"
//controlPanel.timeLabel = "time"
//controlPanel.slowLabel = "slow"
//controlPanel.fastLabel = "fast"
//controlPanel.elasticityLabel = "elasticity"
//controlPanel.elasticityValueLabel = numeric value is set by code, do not internalize this string
//controlPanel.zeroPercentLabel = "0%"
//controlPanel.oneHundredPercentLabel = "100%"

//This class is associated with Flash Library Symbol controlPanel, so "this" is the controlPanel Library Symbol

public class ControlPanel extends Sprite {
    private var myModel: Model;
    private var myMainView: MainView;
    private var nbrBalls;
    private var tFormat: TextFormat;
    private var resetButton: NiceButton;
    public var showCMOn: Boolean;

    private static var groupName: String = "pad";


    public function ControlPanel( myModel: Model, myMainView: MainView ) {
        this.myModel = myModel;
        this.myMainView = myMainView;
        this.myMainView.addChild( this );
        this.nbrBalls = this.myModel.nbrBalls;
        this.tFormat = new TextFormat();
        //this.changeNbrBallButtons = new ChangeNbrBallButtons();
        this.initialize();
        this.initializeComponents();
        //this.initializeStrings();
    }//end of constructor

    public function initialize(): void {
        //this.changeNbrBallButtons.addBallButton.addEventListener(MouseEvent.MOUSE_DOWN, addBall);
        //this.changeNbrBallButtons.removeBallButton.addEventListener(MouseEvent.MOUSE_DOWN, removeBall);
        //this.changeNbrBallButtons.addBallButton.buttonMode = true;
        //this.changeNbrBallButtons.removeBallButton.buttonMode = true;
        this.resetButton = new NiceButton( this.sub_resetButton_sp, 80, resetAll );
        //var nbrString:String = String(this.nbrBalls);
        //this.changeNbrBallButtons.nbrReadout.text = nbrString;
        this.sub_elasticityValueLabel.text = "1.00";
        //this.background.border.buttonMode = true;
        //if(this.myModel.nbrBalls == 1){
        //this.changeNbrBallButtons.removeBallButton.visible = false;
        //}
        this.showCMOn = true;
        Util.makePanelDraggableWithBorder( this, this.sub_background.border );
    }

    public function initializeComponents(): void {
        this.tFormat.size = 12;
        this.tFormat.font = "Arial";
        StyleManager.setStyle( "textFormat", tFormat );

        initializeStrings();

        // make the radio buttons have different groups across different tabs TODO: fix this, not working?!? egad!
        var oneDimensionRadioButton: RadioButton = this.sub_oneD_rb;
        var twoDimensionRadioButton: RadioButton = this.sub_twoD_rb;
        var group: RadioButtonGroup = new RadioButtonGroup( groupName );
        trace( groupName );
        groupName += "Pad";
        oneDimensionRadioButton.group = group;
        twoDimensionRadioButton.group = group;

        this.sub_oneD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_twoD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showVelocities_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showMomentumVectors_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showCM_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_reflectingBorder_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showPaths_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_oneD_rb.addEventListener( MouseEvent.CLICK, oneDModeOn );
        this.sub_twoD_rb.addEventListener( MouseEvent.CLICK, oneDModeOff );
        this.sub_showVelocities_cb.addEventListener( MouseEvent.CLICK, showVelocityArrows );
        this.sub_showMomentumVectors_cb.addEventListener( MouseEvent.CLICK, showMomentumArrows );
        this.sub_showCM_cb.addEventListener( MouseEvent.CLICK, showCM );
        this.sub_reflectingBorder_cb.addEventListener( MouseEvent.CLICK, borderOnOrOff );
        this.sub_showMomenta_cb.addEventListener( MouseEvent.CLICK, momentaDiagramOnOrOff );
        this.sub_showPaths_cb.addEventListener( MouseEvent.CLICK, showOrErasePaths );
        this.sub_sound_cb.addEventListener( MouseEvent.CLICK, soundOnOrOff );
        //this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
        this.sub_elasticitySlider.addEventListener( SliderEvent.CHANGE, setElasticity );

    }


    //<string key="ControlPanel.showCenterOfMass" value="Show C.M."/>
    //    <string key="ControlPanel.reflectingBorder" value="Reflecting Border"/>
    //    <string key="ControlPanel.showPaths" value="Show Paths"/>
    //    <string key="ControlPanel.sound" value="Sound"/>

    public function initializeStrings(): void {
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.1d", "1 Dimension", sub_oneD_txt, sub_oneD_rb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.2d", "2 Dimensions", sub_twoD_txt, sub_twoD_rb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showVelocities", "Velocity Vectors", sub_showVelocities_label, sub_showVelocities_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showMomentumVectors", "Momentum Vectors", sub_showMomentumVectors_label, sub_showMomentumVectors_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showCenterOfMass", "Center of Mass", sub_showCM_label, sub_showCM_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.reflectingBorder", "Reflecting Border", sub_reflectingBorder_label, sub_reflectingBorder_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.momentaDiagram", "Momenta Diagram", sub_showMomenta_label, sub_showMomenta_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showPaths", "Show Paths", sub_showPaths_label, sub_showPaths_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.sound", "Sound", sub_sound_label, sub_sound_cb );

        resetButton.setLabel( SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.resetAll", "Reset All" ) );
        // this.timeLabel.text = SimStrings.get("ControlPanel.timeRate","Time Rate");
        //this.slowLabel.text = SimStrings.get("ControlPanel.slow","slow");
        //this.fastLabel.text = SimStrings.get("ControlPanel.fast","fast");
        this.sub_elasticityLabel.text = SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.elasticity", "Elasticity" );
        this.sub_zeroPercentLabel.text = SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.zeroPercent", "0%" );
        this.sub_oneHundredPercentLabel.text = SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.oneHundredPercent", "100%" );

        //TODO: JO: needs resizing and extracting labels of the components out
    }//end of initializeStrings()

    public function oneDModeOn( evt: MouseEvent ): void {
        this.myModel.setOneDMode( true );
        this.myMainView.myTableView.myTrajectories.setBorderHeight();
        this.myMainView.myTableView.myTrajectories.erasePaths();

        this.myMainView.myTableView.reDrawBorder();
    }

    public function oneDModeOff( evt: MouseEvent ): void {
        this.myModel.setOneDMode( false );
        this.myMainView.myTableView.myTrajectories.setBorderHeight();
        this.myMainView.myTableView.reDrawBorder();
        this.myMainView.myTableView.myTrajectories.erasePaths();
    }

    public function momentaDiagramOnOrOff( evt: MouseEvent ): void {
        //trace(this.showCMOn = evt.target.selected);
        //var momentaDiagramOnOrOff:Boolean = evt.target.selected;
        this.myMainView.momentumView.visible = evt.target.selected;
    }

    private function resetAll(): void {
        this.myModel.resetAll();
        this.myMainView.myTableView.reDrawBorder();
        this.myMainView.myDataTable.checkBallNbrLimits();
        this.myMainView.myTableView.playButtons.resetAllCalled();
        this.sub_twoD_rb.selected = true;
        this.sub_showVelocities_cb.selected = true;
        this.myMainView.myTableView.showArrowsOnBallImages( true );
        this.sub_showMomentumVectors_cb.selected = false;
        this.myMainView.myTableView.showPArrowsOnBallImages( false );
        this.sub_showCM_cb.selected = true;
        this.myMainView.myTableView.CM.visible = true;
        this.sub_reflectingBorder_cb.selected = true;
        //this.myModel.setReflectingBorder(true); //done in Model.resetAll();
        this.sub_showMomenta_cb.selected = false;
        this.myMainView.momentumView.visible = false;
        this.sub_showPaths_cb.selected = false;
        this.myMainView.myTableView.myTrajectories.pathsOff();
        this.sub_sound_cb.selected = false;
        this.myModel.soundOn = false;
        //var nbrBalls_str:String = String(this.myModel.nbrBalls);
        //this.changeNbrBallButtons.nbrReadout.text = nbrBalls_str;
        //this.nbrBalls = this.myModel.nbrBalls;
        //this.changeNbrBallButtons.addBallButton.visible = true;
        //if(this.nbrBalls == 1){
        //this.changeNbrBallButtons.removeBallButton.visible = false;
        //this.myMainView.myTableView.CM.visible = false;
        //}
        //this.timeRateSlider.value = 0.5;
        //this.myModel.setTimeRate(0.5);
        this.myMainView.myTableView.timeRate_slider.value = this.myModel.timeRate;
        this.sub_elasticitySlider.value = 1;
        this.myModel.setElasticity( 1 );
        this.sub_elasticityValueLabel.text = this.myModel.e.toFixed( 2 );
    }

    public function showVelocityArrows( evt: MouseEvent ): void {
        this.myMainView.myTableView.showArrowsOnBallImages( evt.target.selected );
    }

    private function showMomentumArrows( evt: MouseEvent ): void {
        //trace("show Momentum Arrows is " + evt.target.selected);
        this.myMainView.myTableView.showPArrowsOnBallImages( evt.target.selected );
    }

    private function showCM( evt: MouseEvent ): void {
        this.showCMOn = evt.target.selected;
        this.sub_cmIcon.visible = evt.target.selected;
        if ( myModel.nbrBalls > 1 ) {
            this.myMainView.myTableView.CM.visible = evt.target.selected;
        }
        else {
            this.myMainView.myTableView.CM.visible = false;
        }
        //trace("this.showCMOn: "+this.showCMOn);
    }

    private function borderOnOrOff( evt: MouseEvent ): void {
        this.myModel.setReflectingBorder( evt.target.selected );
        this.myMainView.myTableView.drawBorder();
        //trace("ControlPanel.borderOnOrOff: " + evt.target.selected);
    }

    public function showOrErasePaths( evt: MouseEvent ): void {
        //trace("ControlPanel.showOrErasePaths.evt.target.selected: "+evt.target.selected);
        if ( evt.target.selected ) {
            this.myMainView.myTableView.myTrajectories.pathsOn();
        }
        else {
            this.myMainView.myTableView.myTrajectories.pathsOff();
        }
    }

    public function soundOnOrOff( evt: MouseEvent ): void {
        this.myModel.soundOn = evt.target.selected;
    }

    //Time Rate slider moved to TableView
    //public function setTimeRate(evt:SliderEvent):void{
    //trace("time slider: "+evt.target.value);
    //this.myModel.setTimeRate(evt.target.value);
    //}

    public function setElasticity( evt: SliderEvent ): void {
        trace( "elasticity = " + evt.target.value )
        this.myModel.setElasticity( evt.target.value );
        var e_str: String = this.myModel.e.toFixed( 2 );//String(evt.target.value);
        this.sub_elasticityValueLabel.text = e_str;
        //trace("e slider: "+evt.target.value);
    }

    //may not be necessary, since this is a controller, not a view
    public function update(): void {

    }

    /******************************
     * All of these should be implemented by subclasses
     */

    public function get sub_resetButton_sp(): MovieClip { throw new Error( "abstract" ); }

    public function get sub_background(): MovieClip { throw new Error( "abstract" ); }

    public function get sub_cmIcon(): CenterOfMass { throw new Error( "abstract" ); }

    public function get sub_oneD_rb(): RadioButton { throw new Error( "abstract" ); }

    public function get sub_twoD_rb(): RadioButton { throw new Error( "abstract" ); }

    public function get sub_showVelocities_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_showMomentumVectors_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_showCM_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_reflectingBorder_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_showPaths_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_showMomenta_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_sound_cb(): CheckBox { throw new Error( "abstract" ); }

    public function get sub_elasticitySlider(): Slider { throw new Error( "abstract" ); }

    public function get sub_oneD_txt(): TextField { throw new Error( "abstract" ); }

    public function get sub_twoD_txt(): TextField { throw new Error( "abstract" ); }

    public function get sub_showVelocities_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_showMomentumVectors_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_showCM_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_reflectingBorder_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_showMomenta_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_showPaths_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_sound_label(): TextField { throw new Error( "abstract" ); }

    public function get sub_elasticityValueLabel(): TextField { throw new Error( "abstract" ); }

    public function get sub_elasticityLabel(): TextField { throw new Error( "abstract" ); }

    public function get sub_zeroPercentLabel(): TextField { throw new Error( "abstract" ); }

    public function get sub_oneHundredPercentLabel(): TextField { throw new Error( "abstract" ); }

}//end of class
}//end of package