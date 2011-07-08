package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.NiceComponents.HorizontalSlider;
import edu.colorado.phet.normalmodes.NiceComponents.NiceButton2;
import edu.colorado.phet.normalmodes.NiceComponents.NiceLabel;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.model.Model2;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.*;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.CheckBox;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;

public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var myModel1: Model1;
    private var myModel2: Model2;

//    private var radioButtonBox: HBox;
//    private var rulerCheckBoxBox: HBox;
    private var background: VBox;
    private var nbrMassesSlider: HorizontalSlider;
    private var resetPositionsButton: NiceButton2;

    //Type of Mode radio buttons
    private var innerBckgrnd1: VBox;
    private var modeTypeLabel: NiceLabel;
    private var modeTypeHBox: HBox;
    private var directionOfMode_rbg: RadioButtonGroup;
    private var rightLeftModeButton: RadioButton;
    private var upDownModeButton: RadioButton;
    private var horizArrow: TwoHeadedArrow;
    private var vertArrow: TwoHeadedArrow;

    private var innerBckgrnd3:HBox;
    private var showPhasesCheckBox: CheckBox;
    private var showPhasesLabel: NiceLabel;
    private var innerBckgrnd4:HBox;
    private var showSpringsCheckBox: CheckBox;
    private var showSpringsLabel: NiceLabel;
    private var oneDMode: Boolean;       //true if in 1D mode

    private var resetAllButton: NiceButton2;
//    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    //internationalized strings
    public var numberOfMasses_str: String;
    public var resetPositions_str: String;
    public var polarization_str: String;
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
            setStyle( "horizontalAlign", "center" );
        }

        this.innerBckgrnd1 = new VBox();
        with ( this.innerBckgrnd1 ) {
            setStyle( "backgroundColor", 0xdddd00 );
            percentWidth = 100;
            //percentHeight = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 6 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 0 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 3 );
            setStyle( "paddingLeft", 8 );
            setStyle( "verticalGap", 0 );
            setStyle( "horizontalAlign" , "left" );
        }

        this.nbrMassesSlider = new HorizontalSlider( setNbrMasses, 120, 1, 10, false, true, 10, false );
        this.nbrMassesSlider.setLabelText( this.numberOfMasses_str );
        //NiceButton2( myButtonWidth: Number, myButtonHeight: Number, labelText: String, buttonFunction: Function, bodyColor:Number = 0x00ff00 , fontColor:Number = 0x000000)
        this.resetPositionsButton = new NiceButton2( 120, 30, resetPositions_str, resetPositions, 0xff0000, 0xffffff );
        this.modeTypeLabel = new NiceLabel( 12, polarization_str );
        //Set up rightLeft vs. upDown radio button box
        this.modeTypeHBox = new HBox();
        this.directionOfMode_rbg = new RadioButtonGroup();
        this.rightLeftModeButton = new RadioButton();
        this.upDownModeButton = new RadioButton();
        this.horizArrow = new TwoHeadedArrow();
        this.horizArrow.height = 10;
        this.horizArrow.width = 20;
        this.horizArrow.y = -0.5*this.horizArrow.height;   //I don't understand why this must be negative.
        this.horizArrow.x = 5;                              //and why this is positive
        this.vertArrow = new TwoHeadedArrow();
        this.vertArrow.height = 10;
        this.vertArrow.width = 20;
        this.vertArrow.rotation = -90;
        this.vertArrow.x = 5;

        this.rightLeftModeButton.group = directionOfMode_rbg;
        this.upDownModeButton.group = directionOfMode_rbg;
        this.rightLeftModeButton.value = 1;
        this.upDownModeButton.value = 0;
        this.rightLeftModeButton.selected = false;
        this.upDownModeButton.selected = true;
        this.directionOfMode_rbg.addEventListener( Event.CHANGE, clickLongOrTrans );

        this.innerBckgrnd3 = new HBox();
        this.innerBckgrnd3.setStyle( "horizontalGap", 0 );
        this.showPhasesCheckBox = new CheckBox();
        this.showPhasesCheckBox.addEventListener( Event.CHANGE, clickShowPhases );
        this.showPhasesLabel = new NiceLabel( 12, showPhases_str );

        this.innerBckgrnd4 = new HBox();
        this.innerBckgrnd4.setStyle( "horizontalGap", 0 );
        this.showSpringsCheckBox = new CheckBox();
        this.showSpringsCheckBox.selected = true;
        this.showSpringsCheckBox.addEventListener( Event.CHANGE, clickShowSprings );
        this.showSpringsLabel = new NiceLabel( 12, this.showSprings_str );

        //Layout of components
        this.addChild( this.background );
        this.background.addChild( new SpriteUIComponent( this.nbrMassesSlider, true ));

        //Mode type radio buttons
        this.background.addChild( this.innerBckgrnd1 );
        this.innerBckgrnd1.addChild( new SpriteUIComponent( this.modeTypeLabel));
        this.innerBckgrnd1.addChild( this.modeTypeHBox );
        this.modeTypeHBox.addChild( this.rightLeftModeButton );
        this.modeTypeHBox.addChild( new SpriteUIComponent( this.horizArrow, true) );
        this.modeTypeHBox.addChild( this.upDownModeButton );
        this.modeTypeHBox.addChild( new SpriteUIComponent( this.vertArrow, true) );

        this.background.addChild( innerBckgrnd4 );
        this.innerBckgrnd4.addChild( showSpringsCheckBox );
        this.innerBckgrnd4.addChild( new SpriteUIComponent( showSpringsLabel, true ) );

        this.background.addChild( this.innerBckgrnd3 );
        this.innerBckgrnd3.addChild( showPhasesCheckBox );
        this.innerBckgrnd3.addChild( new SpriteUIComponent( showPhasesLabel, true ) );

        this.background.addChild( new SpriteUIComponent( this.resetPositionsButton, true ));

        this.oneDMode = this.myMainView.oneDMode;
        //this.background.addChild( new SpriteUIComponent(this.resetAllButton, true) );
    } //end of init()

    public function initializeStrings(): void {
        numberOfMasses_str = FlexSimStrings.get("numberOfMasses", "Number of Masses");
        resetPositions_str = FlexSimStrings.get("resetPositions", "Reset Positions");
        polarization_str = FlexSimStrings.get("polarization:", "Polarizaton:");
        resetAll_str = FlexSimStrings.get("resetAll", "Reset All");
        oneD_str = FlexSimStrings.get("oneD", "1D");
        twoD_str = FlexSimStrings.get("twoD", "2D");
        showPhases_str = FlexSimStrings.get("showPhases", "Show Phases");
        showSprings_str = FlexSimStrings.get("showSprings", "Show Springs");
    }

    private function setNbrMasses():void{
        var nbrM:Number = this.nbrMassesSlider.getVal();
        if( this.myMainView.oneDMode ){
            this.myModel1.setN( nbrM );
            this.myMainView.mySliderArrayPanel.locateSlidersAndLabels();
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
        this.resetPositions();
    }

    private function resetPositions():void{
        //Doesn't matter if in 1D or 2D mode, want all modes zeroed.
        this.myModel1.initializeKinematicArrays();
        this.myModel1.zeroModeArrays();
        this.myModel2.initializeKinematicArrays();
        this.myModel2.zeroModeArrays();
    }

    private function clickLongOrTrans( evt: Event ): void {
        var val: Object = this.directionOfMode_rbg.selectedValue;
        if ( val == 1 ) {
            this.myModel1.setTorL( "L" );
            this.myModel2.xModes = true;
            this.myMainView.myButtonArrayPanel.showVerticalPolarization( false );
        }
        else {
            this.myModel1.setTorL( "T" );
            this.myModel2.xModes =  false;
            this.myMainView.myButtonArrayPanel.showVerticalPolarization( true );
        }
    }

    private function clickShowPhases( evt:Event ):void{
        var shown: Boolean = this.showPhasesCheckBox.selected;
        this.myMainView.mySliderArrayPanel.showPhaseSliders(shown);
        //trace( "ControlPanel.clickShowPhases = " + shown);
    }

    public function showPhasesVisible( tOrF:Boolean ):void{
        this.innerBckgrnd3.visible = tOrF;
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