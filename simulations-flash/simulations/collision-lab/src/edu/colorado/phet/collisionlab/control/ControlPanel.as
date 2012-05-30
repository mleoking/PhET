package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.collisionlab.view.BallImage;
import edu.colorado.phet.collisionlab.view.MainView;
import edu.colorado.phet.flashcommon.AboutDialog;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import fl.controls.CheckBox;
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

    public function ControlPanel( myModel: Model, myMainView: MainView ) {
        this.myModel = myModel;
        this.myMainView = myMainView;
        this.myMainView.addChild( this );
        this.nbrBalls = this.myModel.nbrBalls;
        this.tFormat = new TextFormat();
        this.initialize();
        this.initializeComponents();
    }

    public function initialize(): void {
        this.resetButton = new NiceButton( this.sub_resetButton_sp, 80, resetAll );
        this.showCMOn = false;
        Util.makePanelDraggableWithBorder( this, this.sub_background.border );
    }

    public function initializeComponents(): void {
        this.tFormat.size = 12;
        this.tFormat.font = "Arial";
        StyleManager.setStyle( "textFormat", tFormat );

        initializeStrings();


        this.sub_showVelocities_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showMomentumVectors_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showCM_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showVelocities_cb.addEventListener( MouseEvent.CLICK, showVelocityArrows );
        this.sub_showMomentumVectors_cb.addEventListener( MouseEvent.CLICK, showMomentumArrows );
        this.sub_showCM_cb.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            showCM( e.target.selected );
        } );
        this.sub_showMomenta_cb.addEventListener( MouseEvent.CLICK, momentaDiagramOnOrOff );
        this.sub_sound_cb.addEventListener( MouseEvent.CLICK, soundOnOrOff );
        this.sub_elasticitySlider.addEventListener( SliderEvent.CHANGE, setElasticity );

        kineticEnergyCheckBox.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            showKineticEnergy( e.target.selected );
        } );

    }

    public function initializeStrings(): void {
        // hook together the buttons (check boxes in this case) with their labels
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.showVelocities", "Velocity Vectors", sub_showVelocities_label, sub_showVelocities_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.showMomentumVectors", "Momentum Vectors", sub_showMomentumVectors_label, sub_showMomentumVectors_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.showCenterOfMass", "Center of Mass", sub_showCM_label, sub_showCM_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.momentaDiagram", "Momenta Diagram", sub_showMomenta_label, sub_showMomenta_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.sound", "Sound", sub_sound_label, sub_sound_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.kineticEnergy", "Kinetic Energy", kineticEnergyCheckBoxLabel, kineticEnergyCheckBox );

        resetButton.setLabel( SimStrings.get( "ControlPanel.resetAll", "Reset All" ) );
        updateElasticityValueLabel();
        this.sub_zeroPercentLabel.text = SimStrings.get( "ControlPanel.zeroPercent", "Sticky" );
        this.sub_oneHundredPercentLabel.text = SimStrings.get( "ControlPanel.oneHundredPercent", "Bouncy" );

        //TODO: JO: needs resizing and extracting labels of the components out
    }//end of initializeStrings()

    public function switchToOneDimension(): void {
        this.myModel.setOneDMode( true );
        this.myMainView.myTableView.myTrajectories.setBorderHeight();
        this.myMainView.myTableView.myTrajectories.erasePaths();

        this.myMainView.myTableView.reDrawBorder();

        this.myMainView.momentumView.update();
        this.myMainView.momentumView.drawGrid();
    }

    public function switchToTwoDimensions(): void {
        this.myModel.setOneDMode( false );
        this.myMainView.myTableView.myTrajectories.setBorderHeight();
        this.myMainView.myTableView.reDrawBorder();
        this.myMainView.myTableView.myTrajectories.erasePaths();

        this.myMainView.momentumView.update();
        this.myMainView.momentumView.drawGrid();
    }

    public function momentaDiagramOnOrOff( evt: MouseEvent ): void {
        this.myMainView.momentumView.visible = evt.target.selected;
    }

    protected function resetAll(): void {
        this.myModel.resetAll();
        this.myMainView.reset();
        this.myMainView.myTableView.reset();
        this.myMainView.myDataTable.reset();
        this.sub_showVelocities_cb.selected = true;
        this.sub_showMomentumVectors_cb.selected = false;
        this.sub_showMomenta_cb.selected = false;
        this.myMainView.momentumView.visible = false;
        this.sub_sound_cb.selected = false;
        this.myModel.soundOn = false;
        this.sub_elasticitySlider.value = 1;
        this.myModel.setElasticity( 1 );
        updateElasticityValueLabel();

        myMainView.module.resetAll(); // TODO: convert to where this is the main reset
    }

    private function updateElasticityValueLabel(): void {
        this.sub_elasticityLabel.text = SimStrings.get( "ControlPanel.elasticityReadout", "Elasticity {0}%", [Math.round( this.myModel.e * 100 ).toString()] );
    }

    public function showVelocityArrows( evt: MouseEvent ): void {
        this.myMainView.myTableView.showArrowsOnBallImages( evt.target.selected );
    }

    private function showMomentumArrows( evt: MouseEvent ): void {
        this.myMainView.myTableView.showPArrowsOnBallImages( evt.target.selected );
    }

    public function showCM( visible: Boolean ): void {
        this.showCMOn = visible;
        this.sub_cmIcon.visible = visible;
        if ( myModel.nbrBalls > 1 ) {
            this.myMainView.myTableView.CM.visible = visible;
        }
        else {
            this.myMainView.myTableView.CM.visible = false;
        }
        //trace("this.showCMOn: "+this.showCMOn);
    }

    public function showKineticEnergy( visible: Boolean ): void {
        this.myMainView.myTableView.setTotalKEVisible( visible );
    }

    public function setBorderExists( visible: Boolean ): void {
        this.myModel.setReflectingBorder( visible );
        this.myMainView.myTableView.drawBorder();
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

    public function showValues( evt: MouseEvent ): void {
        for each ( var ball: BallImage in this.myMainView.myTableView.ballImage_arr ) {
            ball.setShowValues( evt.target.selected );
        }
    }

    public function soundOnOrOff( evt: MouseEvent ): void {
        this.myModel.soundOn = evt.target.selected;
    }

    public function setElasticity( evt: SliderEvent ): void {
        var elasticity: Number = evt.target.value;
        trace( "elasticity = " + elasticity );
        this.myModel.setElasticity( elasticity );

        // disable the "back" button if we go under 100% elasticity
        if( elasticity < 1 ) {
            myMainView.myTableView.playButtons.updateBackEnabled( false );
        }
        updateElasticityValueLabel();
    }

    //may not be necessary, since this is a controller, not a view
    public function update(): void {

    }

    /******************************
     * All of these should be implemented by subclasses
     */

    public function get sub_resetButton_sp(): MovieClip {
        throw new Error( "abstract" );
    }

    public function get sub_background(): MovieClip {
        throw new Error( "abstract" );
    }

    public function get sub_cmIcon(): CenterOfMass {
        throw new Error( "abstract" );
    }

    //public function get sub_oneD_rb(): RadioButton { throw new Error( "abstract" ); }

    //public function get sub_twoD_rb(): RadioButton { throw new Error( "abstract" ); }

    public function get sub_showVelocities_cb(): CheckBox {
        throw new Error( "abstract" );
    }

    public function get sub_showMomentumVectors_cb(): CheckBox {
        throw new Error( "abstract" );
    }

    public function get sub_showCM_cb(): CheckBox {
        throw new Error( "abstract" );
    }

    public function get sub_showMomenta_cb(): CheckBox {
        throw new Error( "abstract" );
    }

    public function get sub_sound_cb(): CheckBox {
        throw new Error( "abstract" );
    }

    public function get sub_elasticitySlider(): Slider {
        throw new Error( "abstract" );
    }

    //public function get sub_oneD_txt(): TextField { throw new Error( "abstract" ); }

    //public function get sub_twoD_txt(): TextField { throw new Error( "abstract" ); }

    public function get sub_showVelocities_label(): TextField {
        throw new Error( "abstract" );
    }

    public function get sub_showMomentumVectors_label(): TextField {
        throw new Error( "abstract" );
    }

    public function get sub_showCM_label(): TextField {
        throw new Error( "abstract" );
    }

    public function get sub_showMomenta_label(): TextField {
        throw new Error( "abstract" );
    }

    public function get sub_sound_label(): TextField {
        throw new Error( "abstract" );
    }

    //public function get sub_elasticityValueLabel(): TextField { throw new Error( "abstract" ); }

    public function get sub_elasticityLabel(): TextField {
        throw new Error( "abstract" );
    }

    public function get sub_zeroPercentLabel(): TextField {
        throw new Error( "abstract" );
    }

    public function get sub_oneHundredPercentLabel(): TextField {
        throw new Error( "abstract" );
    }

    public function get kineticEnergyCheckBox(): CheckBox {
        throw new Error( "abstract" );
    }

    public function get kineticEnergyCheckBoxLabel(): TextField {
        throw new Error( "abstract" );
    }

}//end of class
}//end of package