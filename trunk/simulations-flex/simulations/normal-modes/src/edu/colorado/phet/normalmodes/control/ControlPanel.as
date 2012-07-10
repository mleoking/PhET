package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flashcommon.controls.HorizontalSlider;
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.model.Model2;
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

/**
 * Control panel view for both 1D and 2D tabs.
 * The two control panels for the two tabs differ only in that the 1D Control Panel has a "Show phases" checkbox.
 * Depending on the tab (1D or 2D), the control panel interacts with either Model1 or Model2
 */
public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var myModel:Object;                     //Model1 (1D) or Model2 (2D), can change with setModel();
    private var background: VBox;
    private var nbrMassesSlider: HorizontalSlider;   //slider to set number of masses
    private var startStopButton: NiceButton2;
    public var mySloMoStepControl: SloMoStepControl;
    private var resetPositionsButton: NiceButton2;   //button to reset all masses to initial positions set by user
    private var paused:Boolean;                      //true if sim paused
    private var zeroPositionsButton: NiceButton2;    //button to reset all masses to equilibrium positions

    private var innerBckgrnd2:HBox;
    private var innerBckgrnd3:HBox;
    private var showPhasesCheckBox: CheckBox;
    private var showPhasesLabel: NiceLabel;
    private var innerBckgrnd4:HBox;
    private var showSpringsCheckBox: CheckBox;
    private var showSpringsLabel: NiceLabel;

    //private var resetAllButton: NiceButton2;

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

        this.nbrMassesSlider = new HorizontalSlider( setNbrMasses, 120, 1, 10, false, true, 10, false );
        this.nbrMassesSlider.drawKnob( 0x8888ff, 0x0000cc );
        this.nbrMassesSlider.setLabelText( this.numberOfMasses_str );
        this.paused = true;
        this.startStopButton = new NiceButton2( 100, 25, start_str, startStop, 0x00ff00, 0x000000 );
        this.mySloMoStepControl = new SloMoStepControl( this, myModel );
        this.resetPositionsButton = new NiceButton2( 120, 25, resetPositions_str, resetPositions, 0xffff00, 0x000000  )
        this.zeroPositionsButton = new NiceButton2( 120, 25, zeroPositions_str, zeroPositions, 0xff0000, 0xffffff );

        this.innerBckgrnd2 = new HBox();
        this.innerBckgrnd2.setStyle( "horizontalGap", 0 );
        this.innerBckgrnd3 = new HBox();
        this.innerBckgrnd3.setStyle( "horizontalGap", 0 );
        this.innerBckgrnd4 = new HBox();
        this.innerBckgrnd4.setStyle( "horizontalGap", 0 );

        this.showPhasesCheckBox = new CheckBox();
        this.showPhasesCheckBox.addEventListener( Event.CHANGE, clickShowPhases );
        this.showPhasesLabel = new NiceLabel( 12, showPhases_str );
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

    /*If in 1D mode, control panel has a Show Phases checkbox.*/
    public function setShowPhasesControl():void{
        if( this.myMainView.oneDMode ){
            this.background.addChild( innerBckgrnd3 );
        }else{
            this.background.removeChild( innerBckgrnd3 );
        }
    }

    /*Programmatic control of number-of-masses slider*/
    public function setNbrMassesExternally( nbrM: int ): void {
        this.nbrMassesSlider.setVal( nbrM );
        this.myModel.setN( nbrM );
        this.zeroPositions();
    }

    /*Programmatic control of number-of-masses slider without updating model*/
    public function setNbrMassesExternallyWithNoAction( nbrM: int ):void{
        this.nbrMassesSlider.setSliderWithoutAction( nbrM );
    }

    /*Set the model for the control panel: 1D or 2D*/
    public function setModel( currentModel: Object ):void{
        this.myModel = currentModel;
    }

    private function startStop():void{
        if(this.myModel.paused){     //if paused, unPause sim,
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
        this.myModel.pauseSim();
        this.startStopButton.setLabel( start_str );
        this.startStopButton.setBodyColor( 0x00ff00 );  //green
        this.startStopButton.setFontColor( 0x000000 );  //black
    }

    private function zeroPositions():void{
        //Doesn't matter if in 1D or 2D mode, want all modes zeroed.
        this.myModel.initializeKinematicArrays();
        this.myModel.zeroModeArrays();
    }


    private function clickShowPhases( evt:Event ):void{
        var shown: Boolean = this.showPhasesCheckBox.selected;
        this.myMainView.mySliderArrayPanel.showPhaseSliders(shown);
    }

    public function showPhasesVisible( tOrF:Boolean ):void{
        this.innerBckgrnd3.visible = tOrF;
    }

    private function clickShowSprings( evt:Event ):void{
        var shown: Boolean = this.showSpringsCheckBox.selected;
        this.myMainView.myView2.springsVisible = shown;
        this.myMainView.myView1.springsVisible = shown;
        this.myModel.updateViews();
    }

    /*Usused*/
    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        //this.setSelectedResonatorNbr();
    }

    /*Unused*/
    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        //this.setSelectedResonatorNbr();
    }


    private function resetAll( evt: MouseEvent ): void {
       this.myMainView.initializeAll();
    }


}//end of class

}//end of package