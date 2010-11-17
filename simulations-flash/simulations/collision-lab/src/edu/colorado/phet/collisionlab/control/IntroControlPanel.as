package edu.colorado.phet.collisionlab.control {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.view.MainView;

import fl.controls.CheckBox;
import fl.controls.RadioButton;
import fl.controls.Slider;

import flash.display.MovieClip;
import flash.text.TextField;

//This class is associated with Flash Library Symbol introControlPanel, so "this" is the controlPanel Library Symbol

public class IntroControlPanel extends ControlPanel {
    public function IntroControlPanel( myModel: Model, myMainView: MainView ) {
        super( myModel, myMainView );
    }

    override public function get sub_resetButton_sp(): MovieClip { return resetButton_sp; }

    override public function get sub_background(): MovieClip { return background; }

    override public function get sub_cmIcon(): CenterOfMass { return cmIcon; }

    override public function get sub_oneD_rb(): RadioButton { return oneD_rb; }

    override public function get sub_twoD_rb(): RadioButton { return twoD_rb; }

    override public function get sub_showVelocities_cb(): CheckBox { return showVelocities_cb; }

    override public function get sub_showMomentumVectors_cb(): CheckBox { return showMomentumVectors_cb; }

    override public function get sub_showCM_cb(): CheckBox { return showCM_cb; }

    override public function get sub_reflectingBorder_cb(): CheckBox { return reflectingBorder_cb; }

    override public function get sub_showPaths_cb(): CheckBox { return showPaths_cb; }

    override public function get sub_showMomenta_cb(): CheckBox { return showMomenta_cb; }

    override public function get sub_sound_cb(): CheckBox { return sound_cb; }

    override public function get sub_elasticitySlider(): Slider { return elasticitySlider; }

    override public function get sub_oneD_txt(): TextField { return oneD_txt; }

    override public function get sub_twoD_txt(): TextField { return twoD_txt; }

    override public function get sub_showVelocities_label(): TextField { return showVelocities_label; }

    override public function get sub_showMomentumVectors_label(): TextField { return showMomentumVectors_label; }

    override public function get sub_showCM_label(): TextField { return showCM_label; }

    override public function get sub_reflectingBorder_label(): TextField { return reflectingBorder_label; }

    override public function get sub_showMomenta_label(): TextField { return showMomenta_label; }

    override public function get sub_showPaths_label(): TextField { return showPaths_label; }

    override public function get sub_sound_label(): TextField { return sound_label; }

    override public function get sub_elasticityValueLabel(): TextField { return elasticityValueLabel; }

    override public function get sub_elasticityLabel(): TextField { return elasticityLabel; }

    override public function get sub_zeroPercentLabel(): TextField { return zeroPercentLabel; }

    override public function get sub_oneHundredPercentLabel(): TextField { return oneHundredPercentLabel; }
}
}