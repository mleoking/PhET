package edu.colorado.phet.normalmodes {
import edu.colorado.phet.normalmodes.*;

import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.NumericProperty;
import edu.colorado.phet.normalmodes.NiceComponents.HorizontalSlider;
import edu.colorado.phet.normalmodes.NiceComponents.NiceButton2;
import edu.colorado.phet.normalmodes.SpriteUIComponent;

import flash.display.*;
import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.CheckBox;
import mx.controls.ComboBox;
import mx.controls.HSlider;
import mx.controls.HorizontalList;
import mx.controls.Label;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.core.UIComponent;
import mx.events.ListEvent;

public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var myModel1: Model1;
    private var myModel2: Model2;
    private var background: VBox;
//    private var radioButtonBox: HBox;
//    private var rulerCheckBoxBox: HBox;
    private var innerBckgrnd: VBox;
    private var nbrMassesSlider: HorizontalSlider;
    private var resetPositionsButton: NiceButton2;
    private var radioButtonVBox1: VBox;
    private var longTransMode_rbg: RadioButtonGroup;
    private var longitudinalModeButton: RadioButton;
    private var transverseModeButton: RadioButton;
    private var radioButtonHBox:HBox;
    private var oneDtwoDMode_rbg: RadioButtonGroup;
    private var oneDModeButton: RadioButton;
    private var twoDModeButton: RadioButton;
    private var showPhasesCheckBox: CheckBox;
    private var showSpringsCheckBox: CheckBox;
    private var oneDMode: Boolean;       //true if in 1D mode

    private var resetAllButton: NiceButton2;
//    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    //internationalized strings
    public var numberOfMasses_str: String;
    public var resetPositions_str: String;
    public var longitudinal_str: String;
    public var transverse_str: String;
    public var resetAll_str: String;
    public var oneD_str: String;
    public var twoD_str: String;
    public var showPhases_str:String;
    public var showSprings_str:String;


    public function ControlPanel( myMainView: MainView, model1: Model1, model2: Model2 ) {
        super();
        this.myMainView = myMainView;
        this.myModel1 = model1;
        this.myModel2 = model2;
        this.init();

    }//end of constructor

    public function addSprite( s: Sprite ): void {
        this.addChild( new SpriteUIComponent( s ) );
    }

    public function init(): void {

        this.initializeStrings();

        this.background = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x66ff66 );
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 15 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "verticalAlign", "center" );
        }

        this.innerBckgrnd = new VBox();
        with ( this.innerBckgrnd ) {
            setStyle( "backgroundColor", 0xdddd00 );
            percentWidth = 100;
            //percentHeight = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 8 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 5 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 15 );
            setStyle( "paddingLeft", 15 );
            setStyle( "verticalGap", 5 );
            setStyle( "horizontalAlign" , "center" );
        }

        this.nbrMassesSlider = new HorizontalSlider( setNbrMasses, 120, 1, 10, false, true, 10, false );
        this.nbrMassesSlider.setLabelText( this.numberOfMasses_str );
        //NiceButton2( myButtonWidth: Number, myButtonHeight: Number, labelText: String, buttonFunction: Function, bodyColor:Number = 0x00ff00 , fontColor:Number = 0x000000)
        this.resetPositionsButton = new NiceButton2( 120, 30, resetPositions_str, resetPositions, 0xff0000, 0xffffff );

        //Set up longitudinal or transverse radio button box
        this.radioButtonVBox1 = new VBox();
        this.longTransMode_rbg = new RadioButtonGroup();
        this.longitudinalModeButton = new RadioButton();
        this.transverseModeButton = new RadioButton();
        this.transverseModeButton.setStyle( "paddingTop", -5 );
        this.longitudinalModeButton.group = longTransMode_rbg;
        this.transverseModeButton.group = longTransMode_rbg;
        this.longitudinalModeButton.label = this.longitudinal_str;
        this.transverseModeButton.label = this.transverse_str;
        this.longitudinalModeButton.value = 1;
        this.transverseModeButton.value = 0;
        this.longitudinalModeButton.selected = true;
        this.transverseModeButton.selected = false;
        this.longTransMode_rbg.addEventListener( Event.CHANGE, clickLongOrTrans );

         //1D or 2D radio button box
        this.radioButtonHBox = new HBox();
        this.oneDtwoDMode_rbg = new RadioButtonGroup();
        this.oneDModeButton = new RadioButton();
        this.twoDModeButton = new RadioButton();
        this.twoDModeButton.setStyle( "paddingLeft", -5 );
        this.oneDModeButton.group = oneDtwoDMode_rbg;
        this.twoDModeButton.group = oneDtwoDMode_rbg;
        this.oneDModeButton.label = this.oneD_str;
        this.twoDModeButton.label = this.twoD_str;
        this.oneDModeButton.value = 1;
        this.twoDModeButton.value = 2;
        this.oneDModeButton.selected = true;
        this.twoDModeButton.selected = false;
        this.oneDtwoDMode_rbg.addEventListener( Event.CHANGE, click1DOr2D );

        this.showPhasesCheckBox = new CheckBox();
        this.showPhasesCheckBox.label = this.showPhases_str;
        this.showPhasesCheckBox.addEventListener( Event.CHANGE, clickShowPhases );

        this.showSpringsCheckBox = new CheckBox();
        this.showSpringsCheckBox.label = this.showSprings_str;
        this.showSpringsCheckBox.selected = true;
        this.showSpringsCheckBox.addEventListener( Event.CHANGE, clickShowSprings );

        this.addChild( this.background );
        this.background.addChild( new SpriteUIComponent( this.nbrMassesSlider, true ));
        this.background.addChild( new SpriteUIComponent( this.resetPositionsButton, true ));
        this.background.addChild( this.radioButtonVBox1 );
        this.radioButtonVBox1.addChild( this.longitudinalModeButton );
        this.radioButtonVBox1.addChild( this.transverseModeButton );
        this.background.addChild( this.radioButtonHBox );
        this.radioButtonHBox.addChild( this.oneDModeButton );
        this.radioButtonHBox.addChild( this.twoDModeButton );
        this.background.addChild( showPhasesCheckBox );
        this.background.addChild( showSpringsCheckBox );
        this.oneDMode = this.myMainView.oneDMode;
        //this.background.addChild( new SpriteUIComponent(this.resetAllButton, true) );
    } //end of init()

    private function initializeStrings(): void {
        numberOfMasses_str = "Number of Masses";//FlexSimStrings.get("numberOfResonators", "Number of Resonators");
        resetPositions_str = "Reset Positions";
        longitudinal_str = "longitudinal";
        transverse_str = "transverse";
        resetAll_str = "Reset All";
        oneD_str = "1D";
        twoD_str = "2D";
        showPhases_str = "Show Phases";
        showSprings_str = "Show Springs";
    }

    private function setNbrMasses():void{
        var nbrM:Number = this.nbrMassesSlider.getVal();
        if( this.myMainView.oneDMode ){
            this.myModel1.setN( nbrM );
            this.myMainView.mySliderArrayPanel.locateSliders();
        } else{
            this.myModel2.setN( nbrM );
        }
    }

    public function setNbrMassesExternally( nbrM: int ): void {
        this.nbrMassesSlider.setVal( nbrM );
        if( this.myMainView.oneDMode ){
           this.myModel1.setN( nbrM );
        }else{
           this.myModel2.setN( nbrM );
        }
    }

    private function resetPositions():void{
        if(this.oneDMode){
            this.myModel1.initializeKinematicArrays();
            this.myModel1.zeroModeArrays();
        }else{
            this.myModel2.initializeKinematicArrays();
            this.myModel2.zeroModeArrays();
        }
    }

    private function clickLongOrTrans( evt: Event ): void {
        var val: Object = this.longTransMode_rbg.selectedValue;
        if ( val == 1 ) {
            this.myModel1.setTorL( "L" );
            this.myModel2.xModes = true;
        }
        else {
            this.myModel1.setTorL( "T" );
            this.myModel2.xModes =  false;
        }
    }

    private function click1DOr2D( evt: Event ):void {
       var val: Object = this.oneDtwoDMode_rbg.selectedValue;
        if ( val == 1 ) {
            this.myMainView.set1DOr2D( 1 );
        }
        else if ( val == 2 ) {
            this.myMainView.set1DOr2D( 2 );
        }
        this.oneDMode = this.myMainView.oneDMode;
    }

    private function clickShowPhases( evt:Event ):void{
        var shown: Boolean = this.showPhasesCheckBox.selected;
        this.myMainView.mySliderArrayPanel.showPhaseSliders(shown);
        //trace( "ControlPanel.clickShowPhases = " + shown);
    }

    private function clickShowSprings( evt:Event ):void{
        var shown: Boolean = this.showSpringsCheckBox.selected;
        this.myMainView.myView2.springsVisible = shown;
        this.myMainView.myView1.springsVisible = shown;
        //trace( "ControlPanel.clickShowSprings = " + shown);
    }

    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        //this.setSelectedResonatorNbr();
    }

    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        //this.setSelectedResonatorNbr();
    }


    private function resetAll( evt: MouseEvent ): void {
       //this.resetResonators( evt );
       this.myMainView.initializeAll();
    }


}//end of class

}//end of package