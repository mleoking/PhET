package edu.colorado.phet.resonance {

import edu.colorado.phet.flexcommon.model.NumericProperty;

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.CheckBox;
import mx.controls.ComboBox;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.events.ListEvent;

public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var shakerModel: ShakerModel;
    private var background: VBox;
    private var radioButtonBox: HBox;
    private var innerBckgrnd: VBox;
    //private var dampingSlider: HSlider;
    private var dampingSlider: HorizontalSlider;
    private var nbrResonatorsSlider: HSlider;
    private var presets_cbx:ComboBox;
    private var gravityOnOff_rbg: RadioButtonGroup;

    private var gravity_lbl: Label;
    private var ruler_lbl: Label;
    private var resonatorNbr_lbl: Label;
    private var mSlider: HorizontalSlider;  //NumericSlider;
    private var kSlider: HorizontalSlider;  //HSlider;
    private var freq_lbl: Label;
    private var showRulerCheckBox: CheckBox;
    private var resetAllButton: Button;
    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    //private var massProperty: NumericProperty

    //internationalized strings
    public var numberOfResonators_str: String;
    public var damping_str: String;
    public var dampingUnits_str
    public var gravity_str: String;
    public var on_str: String;
    public var off_str: String;
    public var resonator_str: String;
    public var mass_str: String;
    public var massUnits_str: String;
    public var springConstant_str: String;
    public var springConstantUnits_str: String;
    public var frequencyEquals_str: String;
    public var hz_str: String;
    public var showRuler_str:String;
    public var resetAll_str: String;
    public var choose_str:String;
    public var sameMass_str:String;
    public var sameSpring_str:String;
    public var mixedMAndK_str:String;
    public var sameF_str:String;


    public function ControlPanel( myMainView: MainView, model: ShakerModel ) {
        super();
        this.myMainView = myMainView;
        this.shakerModel = model;
        this.init();
    }//end of constructor

    public function addSprite( s: Sprite ): void {
        this.addChild( new SpriteUIComponent( s ) );
    }

    public function init(): void {

        this.initializeStrings();

        this.background = new VBox();
        this.background.setStyle( "backgroundColor", 0x66ff66 );
        //this.background.percentWidth = 100;
        //this.background.percentHeight = 100;
        this.background.setStyle( "borderStyle", "solid" )
        this.background.setStyle( "borderColor", 0x009900 );
        this.background.setStyle( "cornerRadius", 15 );
        this.background.setStyle( "borderThickness", 8 );
        this.background.setStyle( "paddingTop", 20 );
        this.background.setStyle( "paddingBottom", 20 );
        this.background.setStyle( "paddingRight", 7 );
        this.background.setStyle( "paddingLeft", 7 );
        this.background.setStyle( "verticalGap", 5 );
        with ( this.background ) {
            setStyle( "horizontalAlign", "center" );
        }

        this.innerBckgrnd = new VBox();
        with ( this.innerBckgrnd ) {
            setStyle( "backgroundColor", 0xdddd00 );
            //percentWidth = 100;
            //percentHeight = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 8 );
            setStyle( "borderThickness", 3 );
            setStyle( "paddingTop", 5 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 5 );
            setStyle("horizontalAlign" , "center");
        }


        //HorizontalSlider(action:Function, lengthInPix:int, minVal:Number, maxVal:Number, textEditable:Boolean = false, detented:Boolean = false, nbrTics:int = 0)
        this.dampingSlider = new HorizontalSlider( setDamping, 150, 0.05, 5, true ); //new HSlider();
        this.dampingSlider.setLabelText( damping_str );
        this.dampingSlider.setUnitsText( dampingUnits_str );

//        this.formatSlider( this.dampingSlider );
//        with ( this.dampingSlider ) {
//            minimum = 0.05;
//            maximum = 5;
//            //labels = ["", this.damping_str, ""];
//        }
//
//        this.dampingSlider.addEventListener( Event.CHANGE, setDamping );

        this.nbrResonatorsSlider = new HSlider();
        this.formatSlider( this.nbrResonatorsSlider );
        with ( this.nbrResonatorsSlider ) {
            minimum = 1;
            maximum = 10;
            //labels = ["", this.numberOfResonators_str, ""];
            snapInterval = 1;
            tickInterval = 1;
        }

        this.nbrResonatorsSlider.addEventListener( Event.CHANGE, onChangeNbrResonators );

        this.presets_cbx = new ComboBox();
        this.presets_cbx.dataProvider = [choose_str , sameSpring_str , sameMass_str , mixedMAndK_str , sameF_str];
        this.presets_cbx.addEventListener( ListEvent.CHANGE, selectPreset );

        this.radioButtonBox = new HBox();

        this.gravity_lbl = new Label();
        this.gravity_lbl.text = this.gravity_str;
        this.gravity_lbl.setStyle( "fontSize", 14 );

        this.gravityOnOff_rbg = new RadioButtonGroup();
        var rb1: RadioButton = new RadioButton();
        var rb2: RadioButton = new RadioButton();
        rb1.group = gravityOnOff_rbg;
        rb2.group = gravityOnOff_rbg;
        rb1.label = this.on_str;
        rb2.label = this.off_str;
        rb1.value = 1;
        rb2.value = 0;
        rb1.selected = false;
        rb2.selected = true;
        rb1.setStyle( "fontSize", 14 );
        rb2.setStyle( "fontSize", 14 );
        rb1.setStyle( "horizontalGap", 0 );
        rb2.setStyle( "horizontalGap", 0 );

        this.gravityOnOff_rbg.addEventListener( Event.CHANGE, clickGravity );

        this.resonatorNbr_lbl = new Label();

        with ( this.resonatorNbr_lbl ) {
            text = this.resonator_str;
            setStyle( "fontFamily", "Arial" );
            setStyle( "fontSize", 14 );
            setStyle( "color", 0x000000 );
            percentWidth = 90;
            setStyle( "textAlign", "center" );
        }


        //massProperty = new NumericProperty("mass", "kg", 1);
        this.mSlider = new HorizontalSlider( setMass, 120, 0.1, 5.0, true );//NumericSlider(massProperty);
        this.mSlider.setLabelText( mass_str );
        this.mSlider.setUnitsText( massUnits_str );
        //this.mSlider.percentWidth = 100;
        //this.formatSlider( this.mSlider );
//        with ( this.mSlider ) {
//            minimum = 0.1;
//            maximum = 5.0;
//            labels = ["", this.mass_str, ""];
//            // This doesn't work: setStyle("labelPlacement", "bottom");
//        }
        //this.mSlider.addEventListener( Event.CHANGE, onChangeM );
//        massProperty.addListener( function():void{
//            setMass();
//        });

        this.kSlider = new HorizontalSlider( setK, 120, 10, 1200, true );//HSlider();
        this.kSlider.setLabelText( springConstant_str );
        this.kSlider.setUnitsText( springConstantUnits_str );
        //this.formatSlider( this.kSlider );
//        with ( this.kSlider ) {
//            minimum = 10;
//            maximum = 1200;
//            labels = ["", this.springConstant_str, ""];
//        }

        this.kSlider.addEventListener( Event.CHANGE, onChangeK );

        this.freq_lbl = new Label();
        with ( this.freq_lbl ) {
            text = this.frequencyEquals_str;
            setStyle( "fontFamily", "Arial" );
            setStyle( "fontSize", 14 );
            percentWidth = 90;
            setStyle( "textAlign", "center" );
        }

        this.showRulerCheckBox = new CheckBox();
        this.showRulerCheckBox.addEventListener( Event.CHANGE, clickRuler );

        this.resetAllButton = new Button();
        with ( this.resetAllButton ) {
            label = this.resetAll_str;
            buttonMode = true;
        }
        this.resetAllButton.addEventListener( MouseEvent.MOUSE_UP, resetAll );

        this.addChild( this.background );
        this.background.addChild( new SpriteUIComponent(dampingSlider, true) );

        this.background.addChild(presets_cbx);
        this.background.addChild( nbrResonatorsSlider );

        this.innerBckgrnd.addChild( this.resonatorNbr_lbl );
        this.innerBckgrnd.addChild( new SpriteUIComponent(this.mSlider, true) );
        this.innerBckgrnd.addChild( new SpriteUIComponent(this.kSlider, true) );
        this.innerBckgrnd.addChild( this.freq_lbl );
        this.background.addChild( innerBckgrnd );
        this.background.addChild( radioButtonBox );
        this.radioButtonBox.addChild( gravity_lbl );
        this.radioButtonBox.addChild( rb1 );
        this.radioButtonBox.addChild( rb2 );
        this.background.addChild( this.showRulerCheckBox );
        this.background.addChild( this.resetAllButton );

    } //end of init()

    private function initializeStrings(): void {
        numberOfResonators_str = "Number of Resonators";
        damping_str = "damping constant b";
        dampingUnits_str = "N m/s";
        gravity_str = "Gravity";
        on_str = "on";
        off_str = "off";
        resonator_str = "Resonator";
        mass_str = "mass";
        massUnits_str = "kg";
        springConstant_str = "spring constant";
        springConstantUnits_str = " N/m ";
        frequencyEquals_str = "frequency = ";
        hz_str = "Hz";
        showRuler_str = "Show Ruler";
        resetAll_str = "Reset All";
        choose_str = "Choose..";
        sameMass_str = "same mass";
        sameSpring_str = "same spring";
        mixedMAndK_str = "mixed m and k";
        sameF_str = "same frequency";
    }

    private function setDamping():void{
        var b: Number = this.dampingSlider.getVal();
        //trace("ShakerView.changeA() amplitude is   " + amplitude);
        this.shakerModel.setB( b );
    }

    public function setDampingExternally( b: Number ) {
        this.shakerModel.setB( b );
        this.dampingSlider.setVal( b );
    }
    

    function formatSlider( mySlider: HSlider ): void {
        mySlider.buttonMode = true;
        mySlider.liveDragging = true;
        mySlider.percentWidth = 100;
        mySlider.showDataTip = false;
        //mySlider.setStyle( "labelOffset", 25 );
        setStyle( "invertThumbDirection", true );
        //setStyle( "dataTipOffset", -50 );  //this does not work.  Why not?
        setStyle( "fontFamily", "Arial" );
    }

    public function setResonatorIndex( rNbr: int ): void {
        this.selectedResonatorNbr = rNbr;
        var rNbr_str: String = rNbr.toFixed( 0 );
        this.resonatorNbr_lbl.text = this.resonator_str + " " + rNbr_str;
        var m: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getM();
        //trace("ControlPanel.setResonatorIndex. m = "+m);
        this.mSlider.setVal( m ); // massProperty.value = m;
        //this.mSlider.value = m;
        var k: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getK();
        //trace("ControlPanel.setResonatorIndex. k = "+k);
        this.kSlider.setVal( k );//this.kSlider.value = k;
        this.setFreqLabel();
        this.shakerModel.view.setResonatorLabelColor( rNbr, 0xffff00 );
    }

    private function setFreqLabel(): void {
        var rNbr: int = this.selectedResonatorNbr;
        var resFreq: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getF0();
        var resFreq_str: String = resFreq.toFixed( 2 );
        this.freq_lbl.text = this.frequencyEquals_str + resFreq_str + " " + hz_str;
    }

//    public function setDamping( evt: Event ): void {
//        var b: Number = this.dampingSlider.value;
//        this.shakerModel.setB( b );
//    }

//    public function setDampingExternally( b: Number ) {
//        this.shakerModel.setB( b );
//        this.dampingSlider.value = b;
//    }

    public function setPresetComboBoxExternally( idx: int):void{
         this.presets_cbx.selectedIndex = idx;
    }

    private function onChangeNbrResonators( evt: Event ): void {
        var nbrR: int = this.nbrResonatorsSlider.value;
        if ( nbrR < this.selectedResonatorNbr ) {
            this.setResonatorIndex( nbrR );
        }
        //trace("ControlPanel.setNbrResonators called. nbrR = " + nbrR);
        //this.setNbrResonators( nbrR );
        this.myMainView.setNbrResonators( nbrR );
    }

    private function selectPreset(evt: Event ){
         var itemNbr: int = evt.target.selectedIndex;
        this.shakerModel.setResonatorArray(itemNbr);
        this.setResonatorIndex(this.selectedResonatorNbr);
    }

    public function setNbrResonatorsExternally( nbrR: int ): void {
        this.nbrResonatorsSlider.value = nbrR;
        this.setNbrResonators( nbrR );
    }

    private function clickGravity( evt: Event ): void {
        var val: Object = this.gravityOnOff_rbg.selectedValue;
        if ( val == 1 ) {
            this.shakerModel.setG( 5 );
            //trace( "1" );
        }
        else {
            this.shakerModel.setG( 0 );
            //trace( "2" );
        }
    }

    private function clickRuler( evt:Event ):void{
        var val: Object = this.showRulerCheckBox.selectedValue;

    }

    public function setGravityExternally(onOrOff:Boolean):void{
        if(onOrOff){
            this.shakerModel.setG( 5 );
            this.gravityOnOff_rbg.selectedValue = 1;
        } else{
            this.shakerModel.setG( 0 );
            this.gravityOnOff_rbg.selectedValue = 0;
        }
    }

    //who is calling this?  I want to delete it.
    public function setNbrResonators( nbrR: int ): void {
        this.myMainView.setNbrResonators( nbrR );
    }

    public function setMass(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var m: Number = this.mSlider.getVal(); //massProperty.value;
        this.shakerModel.resonatorModel_arr[indx].setM( m );
        this.setFreqLabel();
        //trace("ControlPanel.setMass() mass = "+ m);
    }

    private function onChangeK( evt: Event ): void {
        this.setK();
    }

    public function setK(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var k: Number = this.kSlider.getVal(); //value;
        this.shakerModel.resonatorModel_arr[indx].setK( k );
        this.setFreqLabel();
        //trace("ControlPanel.setK() k = "+ k);
    }

    private function resetResonators( evt: MouseEvent ): void {
        this.shakerModel.resetInitialResonatorArray();
        //this.setResonatorIndex( this.selectedResonatorNbr );
        //trace("ControlPanel.resetResonators() called.");

    }

    private function resetAll( evt: MouseEvent ): void {
        this.resetResonators( evt );
        this.myMainView.initializeAll();
    }

}//end of class

}//end of package