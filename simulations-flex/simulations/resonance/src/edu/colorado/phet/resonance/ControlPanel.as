package edu.colorado.phet.resonance {

import flash.display.*;
import flash.text.*;

import mx.containers.Canvas;

public class ControlPanel extends Sprite {

    private var myMainView: MainView;
    private var shakerModel: ShakerModel;
    private var background: Sprite;
    private var backgroundBorder: Sprite;
    private var dampingSlider: HorizontalSlider;
    private var maxValueOfB: Number
    private var nbrResonatorsSlider: HorizontalSlider;
    private var maxNbrResonators: int;

    private var selectedResonatorNbr: int;	//index number of currently selected resonator
    private var rNbr_txt: TextField;			//label showing which resonator is selected
    private var tFormat1: TextFormat;
    private var freq_txt: TextField;			//frequency of currently selected resonator
    private var mSlider: HorizontalSlider;	//mass slider
    private var kSlider: HorizontalSlider;	//spring constant slider
    private var resetButton: NiceButton2;

    public function ControlPanel( myMainView: MainView, model: ShakerModel ) {
        this.myMainView = myMainView;
        this.shakerModel = model;
        this.init();
    }//end of constructor

    public function init(): void {
        this.background = new Sprite();
        this.backgroundBorder = new Sprite();
        this.addChild( this.background );
        this.addChild( this.backgroundBorder );
        this.drawPanel();

        //HorizontalSlider(action:Function, lengthInPix:int, minVal:Number, maxVal:Number, detented:Boolean = false, nbrTics:int = 0)
        this.dampingSlider = new HorizontalSlider( setDamping, 100, 0.05, 1 );
        this.dampingSlider.setLabelText( "damping" );
        this.maxValueOfB = 5;
        this.dampingSlider.setScale( this.maxValueOfB );
        this.dampingSlider.setReadoutPrecision( 2 );
        this.dampingSlider.setVal( 2.5 );
        this.addChild( dampingSlider );
        this.dampingSlider.x = this.width / 2 - 0.5 * this.dampingSlider.width;
        this.dampingSlider.y = 45;

        //HorizontalSlider(action:Function, lengthInPix:int, minVal:Number, maxVal:Number, detented:Boolean = false, nbrTics:int = 0)
        this.nbrResonatorsSlider = new HorizontalSlider( setNbrResonators, 100, 1, 10, true, 10 );
        this.nbrResonatorsSlider.setLabelText( "resonators" );
        this.nbrResonatorsSlider.setScale( 1 );
        this.nbrResonatorsSlider.setReadoutPrecision( 0 );
        this.nbrResonatorsSlider.setVal( 10 );
        this.addChild( nbrResonatorsSlider );
        this.nbrResonatorsSlider.x = this.width / 2 - 0.5 * this.nbrResonatorsSlider.width;
        this.nbrResonatorsSlider.y = 120;

        this.rNbr_txt = new TextField();
        this.addChild( this.rNbr_txt );
        this.makeResonatorIndexTextField();
        this.rNbr_txt.y = 160; //-1.5*this.rNbr_txt.height;

        this.mSlider = new HorizontalSlider( setMass, 100, 0.2, 5 );
        this.mSlider.setLabelText( "mass" );
        this.mSlider.setReadoutPrecision( 2 );
        this.addChild( this.mSlider );
        this.mSlider.x = this.width / 2 - 0.5 * this.mSlider.width;
        this.mSlider.y = 230;

        this.kSlider = new HorizontalSlider( setK, 100, 20, 500 );
        this.kSlider.setLabelText( "spring constant" );
        this.kSlider.setReadoutPrecision( 0 );
        this.addChild( this.kSlider );
        this.kSlider.x = this.width / 2 - 0.5 * this.kSlider.width;
        this.kSlider.y = 310;

        this.freq_txt = new TextField();	//static label
        this.addChild( this.freq_txt );
        this.makeFreqTextField();
        this.freq_txt.y = 350; //-1.5*this.freq_txt.height;

        //function NiceButton2(myButtonWidth:Number, myButtonHeight:Number, labelText:String, buttonFunction:Function)
        this.resetButton = new NiceButton2( 80, 28, "Reset", resetResonators );
        this.addChild( this.resetButton );
        this.resetButton.x = this.background.width / 2; // - 0.5*this.resetButton.width;
        this.resetButton.y = 400;

        this.setResonatorIndex( 1 );
    }

    //add time rate adjuster here

    public function drawPanel(): void {
        var bG: Graphics = this.background.graphics;
        var panelW: Number = 150;
        var panelH: Number = 450;
        bG.clear();
        bG.lineStyle( 0, 0x0000ff, 1 );
        bG.beginFill( 0x00ff00 );
        bG.drawRoundRect( 0, 0, panelW, panelH, 30 );
        bG.endFill();
        var bbG: Graphics = this.background.graphics;
        bbG.clear();
        bbG.lineStyle( 5, 0x0000ff, 1 );
        bbG.beginFill( 0x00ff00 );
        bbG.drawRoundRect( 0, 0, panelW, panelH, 30 );
        bbG.endFill();
    }

    private function makeResonatorIndexTextField(): void {

        this.rNbr_txt.selectable = true;
        this.rNbr_txt.type = TextFieldType.INPUT;
        this.rNbr_txt..border = true;
        this.rNbr_txt.background = true;
        this.rNbr_txt.backgroundColor = 0xffffff;
        this.rNbr_txt.autoSize = TextFieldAutoSize.CENTER;
        this.rNbr_txt.restrict = "0-9";

        this.tFormat1 = new TextFormat();	//format of label
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 18;
        this.rNbr_txt.defaultTextFormat = this.tFormat1;
        this.rNbr_txt.text = "1";
        this.rNbr_txt.width = 20;
        this.rNbr_txt.x = this.width / 2 - this.rNbr_txt.width / 2;

    }

    private function makeFreqTextField(): void {
        this.freq_txt.selectable = false;
        this.freq_txt.type = TextFieldType.DYNAMIC;
        this.freq_txt.autoSize = TextFieldAutoSize.CENTER;
        this.freq_txt.defaultTextFormat = this.tFormat1;
        this.freq_txt.text = "f0 = 22 Hz";
        this.freq_txt.x = this.width / 2 - this.freq_txt.width / 2;
    }

    private function setFreqTextField(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var f0: Number = this.shakerModel.resonatorModel_arr[indx].getF0();
        var f0_str: String = f0.toFixed( 1 );
        this.freq_txt.text = "f = " + f0_str + " Hz";
        this.freq_txt.x = this.width / 2 - this.freq_txt.width / 2;
    }


    public function setResonatorIndex( rNbr: int ): void {
        this.selectedResonatorNbr = rNbr;
        var rNbr_str: String = rNbr.toString();
        this.rNbr_txt.text = rNbr_str;
        var m: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getM();
        //trace("ControlPanel.setResonatorIndex. m = "+m);
        this.mSlider.setVal( m );
        var k: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getK();
        //trace("ControlPanel.setResonatorIndex. k = "+k);
        this.kSlider.setVal( k );
    }

    public function setDamping(): void {
        var b: Number = this.maxValueOfB * this.dampingSlider.getVal()
        this.shakerModel.setB( b );
    }

    public function setNbrResonators(): void {
        var nbrR: int = this.nbrResonatorsSlider.getVal();
        //trace("ControlPanel.setNbrResonators called. nbrR = " + nbrR);
        this.myMainView.setNbrResonators( nbrR );
    }

    public function setMass(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var m: Number = this.mSlider.getVal();
        this.shakerModel.resonatorModel_arr[indx].setM( m );
        this.setFreqTextField();
        //trace("ControlPanel.setMass() mass = "+ m);
    }

    public function setK(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var k: Number = this.kSlider.getVal();
        this.shakerModel.resonatorModel_arr[indx].setK( k );
        this.setFreqTextField();
        //trace("ControlPanel.setK() k = "+ k);
    }

    private function resetResonators(): void {
        this.shakerModel.resetInitialResonatorArray();
        this.setResonatorIndex( this.selectedResonatorNbr );
        //trace("ControlPanel.resetResonators() called.");
    }

}//end of class

}//end of package