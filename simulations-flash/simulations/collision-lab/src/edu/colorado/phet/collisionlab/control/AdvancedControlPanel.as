package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.view.MainView;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import fl.controls.CheckBox;
import fl.controls.RadioButton;
import fl.controls.Slider;

import flash.display.MovieClip;
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;

//This class is associated with Flash Library Symbol controlPanel, so "this" is the controlPanel Library Symbol

public class AdvancedControlPanel extends ControlPanel {
    public function AdvancedControlPanel( myModel: Model, myMainView: MainView ) {
        super( myModel, myMainView );
    }


    override protected function resetAll(): void {
        super.resetAll();

        this.sub_twoD_rb.selected = true;
        this.sub_reflectingBorder_cb.selected = true;
        this.sub_showCM_cb.selected = false;
        this.sub_showPaths_cb.selected = false;
        this.sub_showValues_cb.selected = false;
    }

    override public function initializeComponents(): void {
        super.initializeComponents();

        this.sub_showCM_cb.selected = false;

        this.sub_oneD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_twoD_rb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_oneD_rb.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {switchToOneDimension()} );
        this.sub_twoD_rb.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {switchToTwoDimensions()} );

        this.sub_reflectingBorder_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_reflectingBorder_cb.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            setBorderExists( e.target.selected );
        } );

        this.sub_showPaths_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showPaths_cb.addEventListener( MouseEvent.CLICK, showOrErasePaths );

        this.sub_showValues_cb.textField.autoSize = TextFieldAutoSize.LEFT;
        this.sub_showValues_cb.addEventListener( MouseEvent.CLICK, showValues );
    }


    override public function initializeStrings(): void {
        super.initializeStrings();

        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.1d", "1 Dimension", sub_oneD_txt, sub_oneD_rb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.2d", "2 Dimensions", sub_twoD_txt, sub_twoD_rb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.reflectingBorder", "Reflecting Border", sub_reflectingBorder_label, sub_reflectingBorder_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.showPaths", "Show Paths", sub_showPaths_label, sub_showPaths_cb );
        TextFieldUtils.initLabelButtonI18NLeft( "ControlPanel.showValues", "Show Values", sub_showValues_label, sub_showValues_cb );
    }

    override public function get sub_resetButton_sp(): MovieClip { return resetButton_sp; }

    override public function get sub_background(): MovieClip { return background; }

    override public function get sub_cmIcon(): CenterOfMass { return cmIcon; }

    public function get sub_oneD_rb(): RadioButton { return oneD_rb; }

    public function get sub_twoD_rb(): RadioButton { return twoD_rb; }

    override public function get sub_showVelocities_cb(): CheckBox { return showVelocities_cb; }

    override public function get sub_showMomentumVectors_cb(): CheckBox { return showMomentumVectors_cb; }

    override public function get sub_showCM_cb(): CheckBox { return showCM_cb; }

    public function get sub_reflectingBorder_cb(): CheckBox { return reflectingBorder_cb; }

    public function get sub_showPaths_cb(): CheckBox { return showPaths_cb; }

    override public function get sub_showMomenta_cb(): CheckBox { return showMomenta_cb; }

    override public function get sub_sound_cb(): CheckBox { return sound_cb; }

    override public function get sub_elasticitySlider(): Slider { return elasticitySlider; }

    public function get sub_oneD_txt(): TextField { return oneD_txt; }

    public function get sub_twoD_txt(): TextField { return twoD_txt; }

    override public function get sub_showVelocities_label(): TextField { return showVelocities_label; }

    override public function get sub_showMomentumVectors_label(): TextField { return showMomentumVectors_label; }

    override public function get sub_showCM_label(): TextField { return showCM_label; }

    public function get sub_reflectingBorder_label(): TextField { return reflectingBorder_label; }

    override public function get sub_showMomenta_label(): TextField { return showMomenta_label; }

    public function get sub_showPaths_label(): TextField { return showPaths_label; }

    override public function get sub_sound_label(): TextField { return sound_label; }

    //override public function get sub_elasticityValueLabel(): TextField { return elasticityValueLabel; }

    override public function get sub_elasticityLabel(): TextField { return elasticityLabel; }

    override public function get sub_zeroPercentLabel(): TextField { return zeroPercentLabel; }

    override public function get sub_oneHundredPercentLabel(): TextField { return oneHundredPercentLabel; }

    public function get sub_showValues_label(): TextField { return showValues_label; }

    public function get sub_showValues_cb(): CheckBox { return showValues_cb; }

    override public function get kineticEnergyCheckBox(): CheckBox { return kineticEnergy_cb; }

    override public function get kineticEnergyCheckBoxLabel(): TextField { return kineticEnergy_label; }
}
}