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
    private var myModel: Model;
    private var background: HBox;
//    private var radioButtonBox: HBox;
//    private var rulerCheckBoxBox: HBox;
    private var innerBckgrnd: VBox;

    private var nbrMassesSlider: HorizontalSlider;
    private var resetPositionsButton: NiceButton2;
    private var radioButtonVBox: VBox;
    private var longTransMode_rbg: RadioButtonGroup;
    private var longitudinalModeButton: RadioButton;
    private var transverseModeButton: RadioButton;

    private var resetAllButton: NiceButton2;
//    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    //internationalized strings
    public var numberOfMasses_str: String;
    public var resetPositions_str: String;
    public var longitudinal_str: String;
    public var transverse_str: String;
    public var resetAll_str: String;


    public function ControlPanel( myMainView: MainView, model: Model ) {
        super();
        this.myMainView = myMainView;
        this.myModel = model;
        this.init();

    }//end of constructor

    public function addSprite( s: Sprite ): void {
        this.addChild( new SpriteUIComponent( s ) );
    }

    public function init(): void {

        this.initializeStrings();

        this.background = new HBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x66ff66 );
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 5 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 20 );
            setStyle( "paddingLeft", 20 );
            setStyle( "horizontalGap", 25 );
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
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign" , "center" );
        }

        this.nbrMassesSlider = new HorizontalSlider( setNbrMasses, 120, 1, 10, false, true, 10, false );
        this.nbrMassesSlider.setLabelText( this.numberOfMasses_str );
        //NiceButton2( myButtonWidth: Number, myButtonHeight: Number, labelText: String, buttonFunction: Function, bodyColor:Number = 0x00ff00 , fontColor:Number = 0x000000)
        this.resetPositionsButton = new NiceButton2( 120, 30, resetPositions_str, resetPositions, 0xff0000, 0xffffff );

        //Set up longitudinal or transverse radio button box
        this.radioButtonVBox = new VBox();
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
        this. transverseModeButton.selected = false;
        this.longTransMode_rbg.addEventListener( Event.CHANGE, clickLongOrTrans );

        this.addChild( this.background );
        this.background.addChild( new SpriteUIComponent( this.nbrMassesSlider, true ));
        this.background.addChild( new SpriteUIComponent( this.resetPositionsButton, true ));
        this.background.addChild( this.radioButtonVBox );
        this.radioButtonVBox.addChild( this.longitudinalModeButton );
        this.radioButtonVBox.addChild( this.transverseModeButton );
        //this.background.addChild( new SpriteUIComponent(this.resetAllButton, true) );
    } //end of init()

    private function initializeStrings(): void {
        numberOfMasses_str = "Number of Masses";//FlexSimStrings.get("numberOfResonators", "Number of Resonators");
        resetPositions_str = "Reset Positions";
        longitudinal_str = "longitudinal";
        transverse_str = "transverse";
        resetAll_str = "Reset All";
    }

    private function setNbrMasses():void{
        var nbrM:Number = this.nbrMassesSlider.getVal();
        this.myModel.setN( nbrM );
        this.myMainView.mySliderArrayPanel.locateSliders();
    }

    public function setNbrMassesExternally( nbrM: int ): void {
        this.nbrMassesSlider.setVal( nbrM );
        this.myModel.setN( nbrM );
    }

    private function resetPositions():void{
        this.myModel.initializeKinematicArrays();
        this.myModel.zeroModeArrays();
    }

    private function clickLongOrTrans( evt: Event ): void {
        var val: Object = this.longTransMode_rbg.selectedValue;
        if ( val == 1 ) {
            this.myModel.setTorL( "L" );
        }
        else {
            this.myModel.setTorL( "T" );
        }
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

//        function formatSlider( mySlider: HSlider ): void {
//        mySlider.buttonMode = true;
//        mySlider.liveDragging = true;
//        mySlider.percentWidth = 100;
//        mySlider.showDataTip = false;
//        //mySlider.setStyle( "labelOffset", 25 );
//        setStyle( "invertThumbDirection", true );
//        //setStyle( "dataTipOffset", -50 );  //this does not work.  Why not?
//        setStyle( "fontFamily", "Arial" );
//    }

}//end of class

}//end of package