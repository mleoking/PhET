package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.collisionlab.view.MainView;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import fl.controls.RadioButton;
import fl.controls.RadioButtonGroup;
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
        this.resetButton = new NiceButton( this.resetButton_sp, 80, resetAll );
        //var nbrString:String = String(this.nbrBalls);
        //this.changeNbrBallButtons.nbrReadout.text = nbrString;
        this.elasticityValueLabel.text = "1.00";
        //this.background.border.buttonMode = true;
        //if(this.myModel.nbrBalls == 1){
        //this.changeNbrBallButtons.removeBallButton.visible = false;
        //}
        this.showCMOn = true;
        Util.makePanelDraggableWithBorder( this, this.background.border );
    }

    public function initializeComponents(): void {
        this.tFormat.size = 12;
        this.tFormat.font = "Arial";
        StyleManager.setStyle( "textFormat", tFormat );

        initializeStrings();

        // make the radio buttons have different groups across different tabs TODO: fix this, not working?!? egad!
        var oneDimensionRadioButton: RadioButton = this.oneD_rb;
        var twoDimensionRadioButton: RadioButton = this.twoD_rb;
        var group: RadioButtonGroup = new RadioButtonGroup( groupName );
        trace( groupName );
        groupName += "Pad";
        oneDimensionRadioButton.group = group;
        twoDimensionRadioButton.group = group;

        this.oneD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.twoD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.showVelocities_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.showMomentumVectors_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.showCM_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.reflectingBorder_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.showPaths_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.oneD_rb.addEventListener( MouseEvent.CLICK, oneDModeOn );
        this.twoD_rb.addEventListener( MouseEvent.CLICK, oneDModeOff );
        this.showVelocities_cb.addEventListener( MouseEvent.CLICK, showVelocityArrows );
        this.showMomentumVectors_cb.addEventListener( MouseEvent.CLICK, showMomentumArrows );
        this.showCM_cb.addEventListener( MouseEvent.CLICK, showCM );
        this.reflectingBorder_cb.addEventListener( MouseEvent.CLICK, borderOnOrOff );
        this.showMomenta_cb.addEventListener( MouseEvent.CLICK, momentaDiagramOnOrOff );
        this.showPaths_cb.addEventListener( MouseEvent.CLICK, showOrErasePaths );
        this.sound_cb.addEventListener( MouseEvent.CLICK, soundOnOrOff );
        //this.timeRateSlider.addEventListener(SliderEvent.CHANGE, setTimeRate);
        this.elasticitySlider.addEventListener( SliderEvent.CHANGE, setElasticity );

    }


    //<string key="ControlPanel.showCenterOfMass" value="Show C.M."/>
    //    <string key="ControlPanel.reflectingBorder" value="Reflecting Border"/>
    //    <string key="ControlPanel.showPaths" value="Show Paths"/>
    //    <string key="ControlPanel.sound" value="Sound"/>

    public function initializeStrings(): void {
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.1d", "1 Dimension", oneD_txt, oneD_rb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.2d", "2 Dimensions", twoD_txt, twoD_rb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showVelocities", "Velocity Vectors", showVelocities_label, showVelocities_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showMomentumVectors", "Momentum Vectors", showMomentumVectors_label, showMomentumVectors_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showCenterOfMass", "Center of Mass", showCM_label, showCM_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.reflectingBorder", "Reflecting Border", reflectingBorder_label, reflectingBorder_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.momentaDiagram", "Momenta Diagram", showMomenta_label, showMomenta_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.showPaths", "Show Paths", showPaths_label, showPaths_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "edu.colorado.phet.collisionlab.control.ControlPanel.sound", "Sound", sound_label, sound_cb );

        resetButton.setLabel( SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.resetAll", "Reset All" ) );
        // this.timeLabel.text = SimStrings.get("ControlPanel.timeRate","Time Rate");
        //this.slowLabel.text = SimStrings.get("ControlPanel.slow","slow");
        //this.fastLabel.text = SimStrings.get("ControlPanel.fast","fast");
        this.elasticityLabel.text = SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.elasticity", "Elasticity" );
        this.zeroPercentLabel.text = SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.zeroPercent", "0%" );
        this.oneHundredPercentLabel.text = SimStrings.get( "edu.colorado.phet.collisionlab.control.ControlPanel.oneHundredPercent", "100%" );

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
        this.twoD_rb.selected = true;
        this.showVelocities_cb.selected = true;
        this.myMainView.myTableView.showArrowsOnBallImages( true );
        this.showMomentumVectors_cb.selected = false;
        this.myMainView.myTableView.showPArrowsOnBallImages( false );
        this.showCM_cb.selected = true;
        this.myMainView.myTableView.CM.visible = true;
        this.reflectingBorder_cb.selected = true;
        //this.myModel.setReflectingBorder(true); //done in Model.resetAll();
        this.showMomenta_cb.selected = false;
        this.myMainView.momentumView.visible = false;
        this.showPaths_cb.selected = false;
        this.myMainView.myTableView.myTrajectories.pathsOff();
        this.sound_cb.selected = false;
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
        this.elasticitySlider.value = 1;
        this.myModel.setElasticity( 1 );
        this.elasticityValueLabel.text = this.myModel.e.toFixed( 2 );
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
        this.cmIcon.visible = evt.target.selected;
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
        this.elasticityValueLabel.text = e_str;
        //trace("e slider: "+evt.target.value);
    }

    //may not be necessary, since this is a controller, not a view
    public function update(): void {

    }

}//end of class
}//end of package