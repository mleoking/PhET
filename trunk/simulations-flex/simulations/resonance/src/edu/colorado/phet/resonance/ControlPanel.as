package edu.colorado.phet.resonance {

import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.NumericProperty;
import edu.colorado.phet.resonance.HorizontalSlider;

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
import mx.controls.Label;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.core.UIComponent;
import mx.events.ListEvent;

public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var shakerModel: ShakerModel;
    private var background: VBox;
    private var radioButtonBox: HBox;
    private var rulerCheckBoxBox: HBox;
    private var innerBckgrnd: VBox;
    private var resonatorNbrBox: HBox;
    private var dampingSlider: HorizontalSlider;
    private var nbrResonatorsSlider: HorizontalSlider;
    private var presets_cbx:ComboBox;
    private var displayIndxOfPresetsBox:int;
    private var gravityOnOff_rbg: RadioButtonGroup;
    private var rbOn: RadioButton;
    private var rbOff: RadioButton;
    private var gravityLabel: NiceLabel;
    private var onLabel: NiceLabel;
    private var offLabel: NiceLabel;
    private var rulerLabel: NiceLabel;
    private var resonatorLabel: NiceLabel;
    private var resonatorNbrLabel: NiceLabel;
    private var mSlider: HorizontalSlider;
    private var kSlider: HorizontalSlider;
    private var freqLabel: NiceLabel;
    private var showRulerCheckBox: CheckBox;
    private var resetAllButton: NiceButton2;
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
    public var ruler_str:String;
    public var resetAll_str: String;
    public var custom_str:String;
    public var sameMass_str:String;
    public var sameSpring_str:String;
    public var mixedMAndK_str:String;
    public var sameF_str:String;


    public function ControlPanel( myMainView: MainView, model: ShakerModel ) {
        super();
        this.myMainView = myMainView;
        this.shakerModel = model;
        this.init();

        trace("ControlPanel.rbOff.x = "+this.rbOff.x );
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
            setStyle( "cornerRadius", 15 );
            setStyle( "borderThickness", 8 );
            setStyle( "paddingTop", 20 );
            setStyle( "paddingBottom", 20 );
            setStyle( "paddingRight", 10 );
            setStyle( "paddingLeft", 10 );
            setStyle( "verticalGap", 15 );
            setStyle( "horizontalAlign", "center" );
        }

        this.innerBckgrnd = new VBox();
        with ( this.innerBckgrnd ) {
            setStyle( "backgroundColor", 0xdddd00 );
            percentWidth = 100;
            //percentHeight = 100;
            setStyle( "borderStyle", "solid" );
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 8 );
            setStyle( "borderThickness", 3 );
            setStyle( "paddingTop", 5 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 5 );
            setStyle( "paddingLeft", 5 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign" , "center" );
        }

        this.resonatorNbrBox = new HBox();
        this.resonatorNbrBox.setStyle( "horizontalGap", 3 );
        this.resonatorLabel = new NiceLabel();
        this.resonatorNbrLabel = new NiceLabel();
        this.resonatorLabel.setText( this.resonator_str );  //need text immediately to set size of Label, so flex framework properly positions label
        this.resonatorNbrLabel.setText( "1" );
        this.resonatorNbrLabel.makeEditable( "0-9" );
        this.resonatorNbrLabel.label_txt.addEventListener( KeyboardEvent.KEY_DOWN, onHitEnter );
        this.resonatorNbrLabel.label_txt.addEventListener( FocusEvent.FOCUS_OUT, onFocusOut )
        this.resonatorLabel.setBold(true);
        this.resonatorNbrLabel.setBold(true);

        //HorizontalSlider(action:Function, lengthInPix:int, minVal:Number, maxVal:Number, textEditable:Boolean = false, detented:Boolean = false, nbrTics:int = 0, readoutShown:Boolean = true)
        this.nbrResonatorsSlider = new HorizontalSlider( setNbrResonators, 150, 1, 10, false, true, 10, false );
        this.nbrResonatorsSlider.setLabelText( this.numberOfResonators_str );
        //this.nbrResonatorsSlider.switchLabelAndReadoutPositions();

        this.dampingSlider = new HorizontalSlider( setDamping, 150, 0.05, 5, true ); //new HSlider();
        this.dampingSlider.setLabelText( damping_str );
        this.dampingSlider.setUnitsText( dampingUnits_str );

        this.presets_cbx = new ComboBox();
        this.presets_cbx.dataProvider = [custom_str , sameSpring_str , sameMass_str , mixedMAndK_str , sameF_str];
        this.presets_cbx.addEventListener( ListEvent.CHANGE, selectPreset );

        this.radioButtonBox = new HBox();
        this.radioButtonBox.setStyle( "horizontalGap", 0);
        this.rulerCheckBoxBox = new HBox();

        this.gravityLabel = new NiceLabel();
        this.gravityLabel.setFontSize( 14 );
        this.gravityLabel.setText(this.gravity_str);

        this.onLabel = new NiceLabel();
        this.onLabel.setText( this.on_str );
        this.offLabel = new NiceLabel();
        this.offLabel.setText( this.off_str );

        this.gravityOnOff_rbg = new RadioButtonGroup();
        this.rbOn = new RadioButton();
        this.rbOff = new RadioButton();
        this.rbOff.setStyle("paddingLeft", 5 );
        this.rbOn.group = gravityOnOff_rbg;
        this.rbOff.group = gravityOnOff_rbg;
        //rb1.label = this.on_str;
        //rb2.label = this.off_str;
        rbOn.value = 1;
        rbOff.value = 0;
        rbOn.selected = false;
        rbOff.selected = true;

        this.gravityOnOff_rbg.addEventListener( Event.CHANGE, clickGravity );

        this.mSlider = new HorizontalSlider( setMassWithSlider, 120, 0.1, 5.0, true );
        this.mSlider.setLabelText( mass_str );
        this.mSlider.setUnitsText( massUnits_str );

        this.kSlider = new HorizontalSlider( setKWithSlider, 120, 10, 1200, true );
        this.kSlider.setLabelText( springConstant_str );
        this.kSlider.setUnitsText( springConstantUnits_str );
        this.kSlider.setReadoutPrecision( 0 );

        this.freqLabel = new NiceLabel();
        this.freqLabel.setFontSize(12);
        //this.freqLabel.setText(this.frequencyEquals_str + "     " + " " + hz_str + "    ");    //don't know why the extra space right end needed to force layout
        var resFreq_str:String = "1.000";
        this.freqLabel.setText(FlexSimStrings.get("frequencyEqualsXHz", "frequency = {0} Hz", [resFreq_str]));  //used to set correct width of string for proper layout

        this.showRulerCheckBox = new CheckBox();
        this.showRulerCheckBox.addEventListener( Event.CHANGE, clickRuler );

        this.rulerLabel = new NiceLabel();
        this.rulerLabel.setText(this.ruler_str);

        //NiceButton2( myButtonWidth: Number, myButtonHeight: Number, labelText: String, buttonFunction: Function, bodyColor:Number = 0x00ff00, fontColor:Number = 0x000000 )
        this.resetAllButton = new NiceButton2( 100, 28, this.resetAll_str, resetAll, 0xff0000, 0xffffff );
        this.resetAllButton.setLabel( this.resetAll_str );

        this.addChild( this.background );
        this.background.addChild( new SpriteUIComponent( nbrResonatorsSlider, true ));
        this.background.addChild(presets_cbx);
        this.displayIndxOfPresetsBox = this.background.getChildIndex( this.presets_cbx );

        this.innerBckgrnd.addChild( this.resonatorNbrBox );
        this.resonatorNbrBox.addChild( new SpriteUIComponent( this.resonatorLabel, true) ) ;
        this.resonatorNbrBox.addChild( new SpriteUIComponent( this.resonatorNbrLabel, true ) );

        this.innerBckgrnd.addChild( new SpriteUIComponent(this.mSlider, true) );
        this.innerBckgrnd.addChild( new SpriteUIComponent(this.kSlider, true) );
        this.innerBckgrnd.addChild( new SpriteUIComponent(this.freqLabel, true) );
        this.background.addChild( innerBckgrnd );

        this.background.addChild( new SpriteUIComponent(dampingSlider, true) );
        this.background.addChild( radioButtonBox );

        this.radioButtonBox.addChild( new SpriteUIComponent(this.gravityLabel,true ));
        this.radioButtonBox.addChild( this.rbOn );
        //trace( "ControlPanel.rbOn.width = "+ this.rbOn.width);
        this.radioButtonBox.addChild( new SpriteUIComponent(this.onLabel,true ));
        this.radioButtonBox.addChild( this.rbOff );
        this.radioButtonBox.addChild( new SpriteUIComponent(this.offLabel,true ));
        var yOffset:int = -2;           //tweek positions of labels relative to radio buttons.
        this.gravityLabel.y = yOffset;
        this.onLabel.y = yOffset;
        this.offLabel.y = yOffset;

        this.background.addChild( rulerCheckBoxBox );
        this.rulerCheckBoxBox.addChild( this.showRulerCheckBox );
        this.rulerCheckBoxBox.addChild( new SpriteUIComponent(this.rulerLabel) );
        this.rulerLabel.y = -2;  //tweek position of label relative to checkbox
        this.rulerLabel.x = -4;

        this.background.addChild( new SpriteUIComponent(this.resetAllButton, true) );
    } //end of init()

    private function initializeStrings(): void {
        numberOfResonators_str = FlexSimStrings.get("numberOfResonators", "Number of Resonators");
        damping_str = FlexSimStrings.get("dampingConstant", "damping constant");
        dampingUnits_str = FlexSimStrings.get("NperMperS", "N/(m/s)");
        gravity_str = FlexSimStrings.get("gravityColon", "Gravity:");
        on_str = FlexSimStrings.get("on", "on");
        off_str = FlexSimStrings.get("off", "off");
        resonator_str = FlexSimStrings.get("resonator", "Resonator");
        mass_str = FlexSimStrings.get("mass", "mass");
        massUnits_str = FlexSimStrings.get("kg", "kg");
        springConstant_str = FlexSimStrings.get("springConstant", "spring constant");
        springConstantUnits_str = FlexSimStrings.get("NperM", "N/m");
        //See function setFreq() for frequencyEqualsXHz string
        frequencyEquals_str = "frequency = ";     //needed for initial sizing of label in layout
        hz_str = "hz";                            //needed for initial sizing of label in layout
        ruler_str = FlexSimStrings.get("ruler", "Ruler");
        resetAll_str = FlexSimStrings.get("resetAll", "Reset All");
        custom_str = FlexSimStrings.get("custom", "Custom");
        sameMass_str = FlexSimStrings.get("sameMass", "same mass m");
        sameSpring_str = FlexSimStrings.get("sameSpring", "same spring k");
        mixedMAndK_str = FlexSimStrings.get("mixedMAndK", "mixed m and k");
        sameF_str = FlexSimStrings.get("sameFrequency", "same frequency");
    }

    private function setDamping():void{
        var b: Number = this.dampingSlider.getVal();
        this.shakerModel.setB( b );
        this.setFreqLabel();
    }

    public function setDampingExternally( b: Number ) {
        this.shakerModel.setB( b );
        this.dampingSlider.setVal( b );
    }

    //set index of selected resonator displayed in control panel
    public function setResonatorIndex( rNbr: int ): void {
        this.selectedResonatorNbr = rNbr;
        var rNbr_str: String = rNbr.toFixed( 0 );
        //this.resonatorNbr_lbl.text = this.resonator_str + " " + rNbr_str;
        //this.resonatorNbrLabel.setText( this.resonator_str + " " + rNbr_str ) ;
        this.resonatorNbrLabel.setText(  rNbr_str ) ;
        var m: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getM();
        //trace("ControlPanel.setResonatorIndex. m = "+m);
        this.mSlider.setSliderWithoutAction( m );
        var k: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getK();
        //trace("ControlPanel.setResonatorIndex. k = "+k);
        this.kSlider.setSliderWithoutAction( k );
        this.setFreqLabel();
        this.shakerModel.view.setResonatorLabelColor( rNbr, 0xffff00 );
    }

    public function getSelectedResonatorIndx():int{
        return this.selectedResonatorNbr;
    }


    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        this.setSelectedResonatorNbr();
    }

    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        this.setSelectedResonatorNbr();
    }

    private function setSelectedResonatorNbr():void{
       var maxNbrR:Number = this.nbrResonatorsSlider.getVal();  //number of resonators shown
       var inputText:String  = this.resonatorNbrLabel.label_txt.text;
       var inputNumber:Number = Number(inputText);
       if(inputNumber < 1){
               inputNumber = 1;
       }else if (inputNumber > 10){
               inputNumber = 9;
      }else if (inputNumber >  maxNbrR){  //user cannot choose resonator unless it is shown
               inputNumber = maxNbrR;
      }
      this.setResonatorIndex( inputNumber )

    } //end setSelectedResonatorNbr()

    private function setFreqLabel(): void {
        var rNbr: int = this.selectedResonatorNbr;
        var resFreq: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getF0();
        var resFreq_str: String = resFreq.toFixed( 3 );
        //this.freqLabel.setText(this.frequencyEquals_str + resFreq_str + " " + hz_str);
        this.freqLabel.setText(FlexSimStrings.get("frequencyEqualsXHz", "frequency = {0} Hz", [resFreq_str]));
    }


    public function setPresetComboBoxExternally( idx: int):void{
         this.presets_cbx.selectedIndex = idx;   //1st choice is idx = 0, 2nd choice is idx = 1, etc.
        this.setResonatorIndex(this.selectedResonatorNbr);
    }


    private function selectPreset(evt: Event ){
         var itemNbr: int = evt.target.selectedIndex;
        this.shakerModel.setResonatorArray(itemNbr);
        //update selected resonator view on control panel
        this.setResonatorIndex(this.selectedResonatorNbr);
        //this.setResonatorIndex(this.selectedResonatorNbr);
    }


    private function clickGravity( evt: Event ): void {
        var val: Object = this.gravityOnOff_rbg.selectedValue;
        if ( val == 1 ) {
            this.shakerModel.setG( 5 );
        }
        else {
            this.shakerModel.setG( 0 );
        }
    }

    private function clickRuler( evt:Event ):void{
        var shown: Boolean = this.showRulerCheckBox.selected;
        this.shakerModel.view.ruler.makeVisible( shown );
        //trace( "ControlPanel.clickRuler = " + val);
    }

    public function setGravityExternally(onOrOff:Boolean):void{
        if(onOrOff){
            this.shakerModel.setG( 5 );
            this.rbOn.selected = true;     //0 is gravity On value
        } else{
            this.shakerModel.setG( 0 );
            this.rbOff.selected = true;     //1 is gravity Off value
        }
        trace("ControlPanel.rbOff.x = "+this.rbOff.x );
    }

    public function setRulerCheckBoxExternally( tOrF:Boolean):void{
        this.showRulerCheckBox.selected = tOrF;
        this.shakerModel.view.ruler.makeVisible( tOrF );
        this.shakerModel.view.ruler.initializePositions();
    }

    //called from Slider
    public function setNbrResonators( ): void {
        var nbrR:Number = this.nbrResonatorsSlider.getVal();
        this.myMainView.setNbrResonators( nbrR );
        //hide presets comboBox if only one resonator is shown
        if(nbrR == 1){
            if( this.presets_cbx.parent == this.background ) {
               this.background.removeChild(this.presets_cbx);
            }
        } else{
            this.background.addChildAt( this.presets_cbx, this.displayIndxOfPresetsBox );
        }
    }


    public function setNbrResonatorsExternally( nbrR: int ): void {
        this.nbrResonatorsSlider.setVal( nbrR );
        this.myMainView.setNbrResonators( nbrR );
        //hide presets comboBox if only one resonator is shown
        if(nbrR == 1){
            if( this.presets_cbx.parent == this.background ) {
               this.background.removeChild(this.presets_cbx);
            }
        } else{
            this.background.addChildAt( this.presets_cbx, this.displayIndxOfPresetsBox );
        }
    }

    private function setMassWithSlider():void{
        //trace("ControlPanel.setMassWithSlider() called.");
        this.setMass();
        this.setPresetComboBoxExternally(0);  //Set to ComboBox to "Custom" whenever mass changed
    }

    public function setMass(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var m: Number = this.mSlider.getVal(); //massProperty.value;
        this.shakerModel.resonatorModel_arr[indx].setM( m );
        this.setFreqLabel();
        //trace("ControlPanel.setMass() mass = "+ m);
    }


    private function setKWithSlider():void{
        this.setK();
        this.setPresetComboBoxExternally(0);  //Set to ComboBox to "Custom" whenever spring constant changed
    }

    public function setK(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var k: Number = this.kSlider.getVal(); //value;
        this.shakerModel.resonatorModel_arr[indx].setK( k );
        this.setFreqLabel();
        //trace("ControlPanel.setK() k = "+ k);
    }

    private function resetResonators(): void {
        this.shakerModel.resetInitialResonatorArray();
        //trace("ControlPanel.resetResonators() called.");
    }

    private function resetAll():void{
        this.resetResonators();
        this.myMainView.initializeAll();
    }

//    private function resetAll( evt: MouseEvent ): void {
//        this.resetResonators( evt );
//        this.myMainView.initializeAll();
//    }

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