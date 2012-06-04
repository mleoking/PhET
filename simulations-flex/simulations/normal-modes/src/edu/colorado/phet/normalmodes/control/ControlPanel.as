package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flashcommon.controls.HorizontalSlider;
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
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
    private var myModel:Object; //Model1 or Model1, can change with setModel();

//    private var radioButtonBox: HBox;
//    private var rulerCheckBoxBox: HBox;
    private var background: VBox;
    private var nbrMassesSlider: HorizontalSlider;
    private var startStopButton: NiceButton2;
    public var mySloMoStepControl: SloMoStepControl;
    private var resetPositionsButton: NiceButton2;
    private var paused:Boolean;
    private var zeroPositionsButton: NiceButton2;

    //Polarization radio buttons
    /*
    private var innerBckgrnd1: VBox;
    private var polarizationLabel: NiceLabel;
    private var modeTypeHBox: HBox;
    private var directionOfMode_rbg: RadioButtonGroup;
    private var horizPolarizationButton: RadioButton;
    private var vertPolarizationButton: RadioButton;
    private var horizArrow: TwoHeadedArrow;
    private var vertArrow: TwoHeadedArrow;
    */
    private var innerBckgrnd2:HBox;
    private var innerBckgrnd3:HBox;
    private var showPhasesCheckBox: CheckBox;
    private var showPhasesLabel: NiceLabel;
    private var innerBckgrnd4:HBox;
    private var showSpringsCheckBox: CheckBox;
    private var showSpringsLabel: NiceLabel;
    private var indexOfShowPhasesControl:int;
    //private var oneDMode: Boolean;       //true if in 1D mode

    private var resetAllButton: NiceButton2;
//    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    //internationalized strings
    public var numberOfMasses_str: String;
    public var resetPositions_str:String;
    public var zeroPositions_str: String;
    public var polarization_str: String;
    public var start_str:String;
    public var stop_str:String;
    public var showPhases_str:String;
    public var showSprings_str:String;


    public function ControlPanel( myMainView: MainView, model1: Object ) {
        super();
        this.myMainView = myMainView;
        this.myModel = model1;
        this.init();
    }//end of constructor

    public function addSprite( s: Sprite ): void {
        this.addChild( new SpriteUIComponent( s ) );
    }

    public function init(): void {

        this.initializeStrings();

        this.background = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x88ff88 );
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 15 );
            setStyle( "paddingRight", 10 );
            setStyle( "paddingLeft", 10 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }

        /*
        this.innerBckgrnd1 = new VBox();
        with ( this.innerBckgrnd1 ) {
            setStyle( "backgroundColor", 0x88ff88 );   //0xdddd00
            percentWidth = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 6 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 0 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 3 );
            setStyle( "paddingLeft", 8 );
            setStyle( "verticalGap", 0 );
            setStyle( "horizontalAlign" , "center" );
        }
        */

        this.nbrMassesSlider = new HorizontalSlider( setNbrMasses, 120, 1, 10, false, true, 10, false );
        this.nbrMassesSlider.drawKnob( 0x8888ff, 0x0000cc );
        this.nbrMassesSlider.setLabelText( this.numberOfMasses_str );
        //NiceButton2( myButtonWidth: Number, myButtonHeight: Number, labelText: String, buttonFunction: Function, bodyColor:Number = 0x00ff00 , fontColor:Number = 0x000000)
        this.paused = true;
        this.startStopButton = new NiceButton2( 100, 25, start_str, startStop, 0x00ff00, 0x000000 );
        this.mySloMoStepControl = new SloMoStepControl( this, myModel );
        this.resetPositionsButton = new NiceButton2( 120, 25, resetPositions_str, resetPositions, 0xffff00, 0x000000  )
        this.zeroPositionsButton = new NiceButton2( 120, 25, zeroPositions_str, zeroPositions, 0xff0000, 0xffffff );
        //this.polarizationLabel = new NiceLabel( 12, polarization_str );

        //Set up polarization radio button box
        /*
        this.modeTypeHBox = new HBox();
        this.directionOfMode_rbg = new RadioButtonGroup();
        this.horizPolarizationButton = new RadioButton();
        this.vertPolarizationButton = new RadioButton();
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

        this.horizPolarizationButton.group = directionOfMode_rbg;
        this.vertPolarizationButton.group = directionOfMode_rbg;
        this.horizPolarizationButton.value = 1;
        this.vertPolarizationButton.value = 0;
        this.horizPolarizationButton.selected = false;
        this.vertPolarizationButton.selected = true;
        this.directionOfMode_rbg.addEventListener( Event.CHANGE, setPolarization );
        */
        this.innerBckgrnd2 = new HBox();
        this.innerBckgrnd2.setStyle( "horizontalGap", 0 );
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

        this.background.addChild( new SpriteUIComponent( this.startStopButton, true ));
        this.background.addChild( this.innerBckgrnd2 );
        this.innerBckgrnd2.addChild( this.mySloMoStepControl);      //SloMoStepControl is a UIComponent, does not need wrapper
        this.background.addChild( new SpriteUIComponent( this.resetPositionsButton, true ));
        this.background.addChild( new SpriteUIComponent( this.zeroPositionsButton, true ));

        this.background.addChild( new SpriteUIComponent( this.nbrMassesSlider, true ));

        this.background.addChild( innerBckgrnd4 );
        this.innerBckgrnd4.addChild( showSpringsCheckBox );
        this.innerBckgrnd4.addChild( new SpriteUIComponent( showSpringsLabel, true ) );

        this.background.addChild( this.innerBckgrnd3 );
        this.innerBckgrnd3.addChild( showPhasesCheckBox );
        this.innerBckgrnd3.addChild( new SpriteUIComponent( showPhasesLabel, true ) );
        indexOfShowPhasesControl = this.background.getChildIndex( this.innerBckgrnd3 );

        //Polarization type radio buttons
        //this.background.addChild( this.innerBckgrnd1 );
        //this.innerBckgrnd1.addChild( new SpriteUIComponent( this.polarizationLabel));
        //this.innerBckgrnd1.addChild( this.modeTypeHBox );
        //this.modeTypeHBox.addChild( this.horizPolarizationButton );
        //this.modeTypeHBox.addChild( new SpriteUIComponent( this.horizArrow, true) );
        //this.modeTypeHBox.addChild( this.vertPolarizationButton );
        //this.modeTypeHBox.addChild( new SpriteUIComponent( this.vertArrow, true) );

        //this.oneDMode = this.myMainView.oneDMode;
        //this.background.addChild( new SpriteUIComponent(this.resetAllButton, true) );
    } //end of init()

    public function initializeStrings(): void {
        start_str = FlexSimStrings.get( "start", "Start" );
        stop_str = FlexSimStrings.get( "stop", "Stop" );
        resetPositions_str = FlexSimStrings.get( "resetPositions", "Initial Positions" );
        zeroPositions_str = FlexSimStrings.get( "zeroPositions", "Zero Positions");
        numberOfMasses_str = FlexSimStrings.get( "numberOfMasses", "Number of Masses");
        polarization_str = FlexSimStrings.get( "polarization:", "Polarizaton:");
        showPhases_str = FlexSimStrings.get( "showPhases", "Show Phases");
        showSprings_str = FlexSimStrings.get( "showSprings", "Show Springs");
    }

    private function setNbrMasses():void{
        var nbrM:Number = this.nbrMassesSlider.getVal();
        this.myModel.setN ( nbrM );
        if( this.myMainView.oneDMode ){
            this.myMainView.mySliderArrayPanel.locateSlidersAndLabels();
        }
    }

    public function setShowPhasesControl():void{
        if( this.myMainView.oneDMode ){
            //this.background.addChildAt( innerBckgrnd3, indexOfShowPhasesControl );
            this.background.addChild( innerBckgrnd3 );
        }else{
            //this.background.removeChildAt( indexOfShowPhasesControl );
            this.background.removeChild( innerBckgrnd3 );
        }
    }

    public function setNbrMassesExternally( nbrM: int ): void {
        this.nbrMassesSlider.setVal( nbrM );
        this.myModel.setN( nbrM );
        this.zeroPositions();
    }

    public function setNbrMassesExternallyWithNoAction( nbrM: int ):void{
        this.nbrMassesSlider.setSliderWithoutAction( nbrM );
    }

    public function setModel( currentModel: Object ):void{
        this.myModel = currentModel;
    }

    private function startStop():void{
        if(this.myModel.paused){     //if paused, unPause sim,
            //this.paused = false;
            this.myModel.unPauseSim();
            this.startStopButton.setLabel( stop_str );
            this.startStopButton.setBodyColor( 0xff0000 );  //red
            this.startStopButton.setFontColor( 0xffffff );  //white
        }else{
            //this.paused = true;
            this.myModel.pauseSim();
            this.startStopButton.setLabel( start_str );
            this.startStopButton.setBodyColor( 0x00ff00 );  //green
            this.startStopButton.setFontColor( 0x000000 );  //black
        }
    }//end startStop()

    public function initializeStartStopButton():void{
        if(!this.myModel.paused){     //if not paused, set button to say STOP
            this.startStopButton.setLabel( stop_str );
            this.startStopButton.setBodyColor( 0xff0000 );  //red
            this.startStopButton.setFontColor( 0xffffff );  //white
        }else{      //else if paused, set button to say START
            this.startStopButton.setLabel( start_str );
            this.startStopButton.setBodyColor( 0x00ff00 );  //green
            this.startStopButton.setFontColor( 0x000000 );  //black
        }
    }

    private function resetPositions():void{
        this.myModel.t = 0;
        //this.myModel.updateViews();
        this.myModel.pauseSim();
        this.startStopButton.setLabel( start_str );
        this.startStopButton.setBodyColor( 0x00ff00 );  //green
        this.startStopButton.setFontColor( 0x000000 );  //black
        //this.paused = true;
    }

    private function zeroPositions():void{
        //Doesn't matter if in 1D or 2D mode, want all modes zeroed.
        this.myModel.initializeKinematicArrays();
        this.myModel.zeroModeArrays();
        //this.myModel.pauseSim();
    }

    /*
    private function setPolarization( evt: Event ): void {
        var val: Object = this.directionOfMode_rbg.selectedValue;
        if ( val == 1 ) {
            //this.myModel1.setTorL( "L" );
            this.myModel.xModes = true;
            //this.myMainView.myButtonArrayPanel.showVerticalPolarization( false );
        }
        else {
            //this.myModel1.setTorL( "T" );
            this.myModel.xModes =  false;
            //this.myMainView.myButtonArrayPanel.showVerticalPolarization( true );
        }
    }
    */

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
        //var nbrM:Number = this.nbrMassesSlider.getVal();
        //this.myModel.setN ( nbrM );
        this.myModel.updateViews();
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