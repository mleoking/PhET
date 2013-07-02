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
import mx.controls.CheckBox;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.events.ItemClickEvent;

/**
 * Control Panel for Trig Lab sim
 * Control panel must be flex canvas to use flex auto-layout
 */

public class ControlPanel extends Canvas {
    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var background: VBox;
    private var radioButtonPanel: VBox;
    private var checkBoxPanel: VBox;

//    private var cos_cb: CheckBox;
//    private var sin_cb: CheckBox;
//    private var tan_cb: CheckBox;

    private var cosSinTan_nrbg: NiceRadioButtonGroup;
    private var cos_nrb: NiceRadioButton;
    private var sin_nrb: NiceRadioButton;
    private var tan_nrb: NiceRadioButton;

//    private var showLabels_cb: CheckBox;
//    private var showLabels_lbl: NiceLabel;
//    private var showGrid_cb: CheckBox;
//    private var showGrid_lbl: NiceLabel;
    private var showLabels_ncb: NiceCheckBox;
    private var showGrid_ncb: NiceCheckBox;
    private var specialAngles_ncb: NiceCheckBox;
    private var resetButton: NiceButton2;

    //private var showLabelsPanel: HBox;
    //private var showGridPanel: HBox;

    //internationalized strings
    private var cos_str: String;
    private var sin_str: String;
    private var tan_str: String;
    private var showLabels_str: String;
    private  var showGrid_str: String;
    private var specialAngles_str: String;
    private var resetAll_str: String;




    public function ControlPanel( mainView:MainView, model:TrigModel ) {
        super();
        this.myMainView = mainView;
        this.myTrigModel = model;
        this.init();
    }

    private function init():void {
        this.initializeStrings();
        this.background = new VBox();
        this.radioButtonPanel = new VBox();
        this.checkBoxPanel = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x55ff55 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );  //0x009900
            setStyle( "cornerRadius", 15 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 15 );
            setStyle( "paddingLeft", 15 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }
        with ( this.radioButtonPanel ) {
            setStyle( "backgroundColor", 0x55ff55 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );  //0x009900
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 0 );
            setStyle( "paddingRight", 15 );
            setStyle( "paddingLeft", 15 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "left" );
        }
        with ( this.checkBoxPanel ) {
            setStyle( "backgroundColor", 0x55ff55 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );  //0x009900
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 0 );
            setStyle( "paddingRight", 15 );
            setStyle( "paddingLeft", 15 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "left" );
        }

        //set up radio buttons
        this.cosSinTan_nrbg = new NiceRadioButtonGroup();
        this.cos_nrb = new NiceRadioButton( cos_str,  true );
        this.sin_nrb = new NiceRadioButton( sin_str,  false );
        this.tan_nrb = new NiceRadioButton( tan_str,  false );
        this.initializeNiceRadioButton( cos_nrb );
        this.initializeNiceRadioButton( sin_nrb );
        this.initializeNiceRadioButton( tan_nrb );
        this.cosSinTan_nrbg.setListener( this );
        this.cosSinTan_nrbg.selectButton( cos_nrb );

        //set up checkboxes
        //this.showLabelsPanel = new HBox();
        this.showLabels_ncb = new NiceCheckBox( showLabels_str, 20 );
        //this.showLabels_cb = new CheckBox();
        //showLabels_cb.addEventListener( Event.CHANGE, showLabelsCheckBoxListener );
        this.showLabels_ncb.checkBox.addEventListener( Event.CHANGE, showLabelsCheckBoxListener );
        //this.showGridPanel = new HBox();
        this.showGrid_ncb = new NiceCheckBox( showGrid_str, 20 );
        this.showGrid_ncb.checkBox.addEventListener( Event.CHANGE, showGridCheckBoxListener );
        this.specialAngles_ncb = new NiceCheckBox( specialAngles_str, 20 );
        this.specialAngles_ncb.checkBox.addEventListener( Event.CHANGE, specialAngleCheckBoxListener );
//        this.showGrid_cb = new CheckBox();
//        showGrid_cb.addEventListener( Event.CHANGE, showGridCheckBoxListener );
//        this.showGrid_lbl = new NiceLabel( 20, showGrid_str );

        //set up reset button
        this.resetButton = new NiceButton2( 100, 30, resetAll_str, resetAll );


        //layout controls
        this.addChild( background );
        this.background.addChild( radioButtonPanel );
        this.background.addChild( checkBoxPanel );
        this.radioButtonPanel.addChild( new SpriteUIComponent( cos_nrb ) );
        this.radioButtonPanel.addChild( new SpriteUIComponent( sin_nrb ) );
        this.radioButtonPanel.addChild( new SpriteUIComponent( tan_nrb ) );
        this.checkBoxPanel.addChild( showLabels_ncb );
        this.checkBoxPanel.addChild( showGrid_ncb );
        this.checkBoxPanel.addChild( specialAngles_ncb );
        this.background.addChild( new SpriteUIComponent( resetButton, true ));

        this.resetAll();

    }//end init()



    private function initializeStrings():void{
        cos_str = FlexSimStrings.get( "cos", "cos" );
        sin_str = FlexSimStrings.get( "sin", "sin" );
        tan_str = FlexSimStrings.get( "tan", "tan" );
        showLabels_str = FlexSimStrings.get( "labels", "Labels" );
        showGrid_str = FlexSimStrings.get( "grid", "Grid" );
        specialAngles_str = FlexSimStrings.get( "specialAngles", "Special Angles" );
        resetAll_str = FlexSimStrings.get( "resetAll", "Reset All" );
    }

    private function initializeNiceRadioButton( nrb: NiceRadioButton ):void{
        nrb.group = cosSinTan_nrbg;
        nrb.label.setFontColor( 0x000000 );
        nrb.label.setFontSize( 25 );
        nrb.setColorsOfDeselectedIcon( 0xbbbbbb, 0xffffff );  // outer rim, center dot
        nrb.setColorsOfSelectedIcon( 0xffffff, 0x000000 );
        nrb.setLabelColors( 0x000000, 0x000000 );
    }

    public function niceRadioGroupListener( selectedButtonIndex: int ):void{
        var choice:int = selectedButtonIndex;
        //0 = cos, 1 = sin, 2 = tan
        this.myMainView.myGraphView.selectWhichGraphToShow( choice );
        this.myMainView.myReadoutView.setVisibilityOfTrigReadout( choice );
        this.myMainView.myUnitCircleView.trigMode = choice;
    }//end NiceRadioGroupListener

    private function showLabelsCheckBoxListener( evt: Event ):void{
        var selected:Boolean = evt.target.selected;
        this.myMainView.myUnitCircleView.setLabelsVisibility( selected );
    }

    private function showGridCheckBoxListener( evt: Event ):void{
        var selected:Boolean = evt.target.selected;
        this.myMainView.myUnitCircleView.setGridLinesVisibility( selected );
    }



    private function specialAngleCheckBoxListener( evt: Event ):void{
        var selected:Boolean = evt.target.selected;
        this.myTrigModel.specialAnglesMode = selected;
    }

//    private function cosCheckBoxListener( evt: Event ):void{
//        var selected:Boolean = evt.target.selected;
//        myMainView.myGraphView.showCos = selected;
//
//    }
//    private function sinCheckBoxListener( evt: Event ):void{
//        var selected:Boolean = evt.target.selected;
//        myMainView.myGraphView.showSin = selected;
//
//    }
//    private function tanCheckBoxListener( evt: Event ):void{
//        var selected:Boolean = evt.target.selected;
//        myMainView.myGraphView.showTan = selected;
//
//    }


    public function resetAll():void{
        myMainView.myUnitCircleView.setGridLinesVisibility( false );
        myMainView.myUnitCircleView.setLabelsVisibility( false );
        myTrigModel.specialAnglesMode = false;
        showGrid_ncb.checkBox.selected = false;
        showLabels_ncb.checkBox.selected = false;
        specialAngles_ncb.checkBox.selected = false;
//        showGrid_cb.selected = false;
//        showLabels_cb.selected = false;
        myTrigModel.smallAngle = 0;
        niceRadioGroupListener( 0 );  //reset readouts to cosine function
        cosSinTan_nrbg.selectButton( cos_nrb );
    }






} //end class
} //end package
