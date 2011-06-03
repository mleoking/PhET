package edu.colorado.phet.normalmodes {
import edu.colorado.phet.normalmodes.*;

import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.NumericProperty;
import edu.colorado.phet.normalmodes.HorizontalSlider;

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
    private var myModel: Model;
    private var background: VBox;
//    private var radioButtonBox: HBox;
//    private var rulerCheckBoxBox: HBox;
    private var innerBckgrnd: VBox;

    private var resetAllButton: NiceButton2;
//    private var selectedResonatorNbr: int;	//index number of currently selected resonator

    //private var massProperty: NumericProperty

    //internationalized strings
    public var numberOfResonators_str: String;



    public function ControlPanel( myMainView: MainView, model: Model ) {
        super();
        this.myMainView = myMainView;
        this.myModel = model;
        //this.init();

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



        this.background.addChild( new SpriteUIComponent(this.resetAllButton, true) );
    } //end of init()

    private function initializeStrings(): void {
        numberOfResonators_str = FlexSimStrings.get("numberOfResonators", "Number of Resonators");

    }





    private function onHitEnter( keyEvt: KeyboardEvent ):void{
        //this.setSelectedResonatorNbr();
    }

    private function onFocusOut( focusEvt: FocusEvent ):void{
        //trace( "ControlPanel.onFocuOut called.");
        //this.setSelectedResonatorNbr();
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