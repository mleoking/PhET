package edu.colorado.phet.resonance {

import flash.display.*;
import flash.events.Event;

import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.HSlider;

public class ControlPanel extends Canvas {

    private var myMainView: MainView;
    private var shakerModel: ShakerModel;
    private var background: VBox;
    private var innerBckgrnd: VBox;
    private var dampingSlider: HSlider;
    private var nbrResonatorsSlider: HSlider;
    private var mSlider: HSlider;
    private var kSlider: HSlider;
    private var resetButton:Button;

    private var selectedResonatorNbr: int;	//index number of currently selected resonator

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

        this.background = new VBox();
        this.background.setStyle( "backgroundColor", 0x00ff00 );//same color as build an atom
        this.background.percentWidth = 100;
        this.background.percentHeight = 100;
        this.background.setStyle( "borderStyle", "solid" )
        this.background.setStyle( "borderColor", 0xff0000 );
        this.background.setStyle( "cornerRadius", 15 );
        this.background.setStyle( "borderThickness", 8 );
        this.background.setStyle( "paddingTop", 30 );
        this.background.setStyle( "paddingBottom", 20 );
        this.background.setStyle( "paddingRight", 10 );
        this.background.setStyle( "paddingLeft", 10 );
        this.background.setStyle( "verticalGap", 30 );



        this.innerBckgrnd = new VBox();
        with ( this.innerBckgrnd ) {
            setStyle( "backgroundColor", 0x00ff00 );//same color as build an atom
            percentWidth = 100;
            percentHeight = 100;
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x0000ff );
            setStyle( "cornerRadius", 8 );
            setStyle( "borderThickness", 4 );
            setStyle( "paddingTop", 20 );
            setStyle( "paddingBottom", 20 );
            setStyle( "paddingRight", 12 );
            setStyle( "paddingLeft", 12 );
            setStyle( "verticalGap", 30 );
        }

        //HorizontalSlider(action:Function, lengthInPix:int, minVal:Number, maxVal:Number, detented:Boolean = false, nbrTics:int = 0)
        this.dampingSlider = new HSlider(); //new HorizontalSlider( setDamping, 100, 0.05, 1 );
        with ( this.dampingSlider ) {
            minimum = 0.05;
            maximum = 5;
            buttonMode = true;
            labels = ["", "damping", ""];
            liveDragging = true;
        }

        this.dampingSlider.addEventListener( Event.CHANGE, setDamping );

        this.nbrResonatorsSlider = new HSlider();
        with ( this.nbrResonatorsSlider ) {
            minimum = 1;
            maximum = 10;
            labels = ["", "Resonators", ""];
            liveDragging = true;
            buttonMode = true;
            snapInterval = 1;
            tickInterval = 1;

        }
        this.nbrResonatorsSlider.addEventListener( Event.CHANGE, onChangeNbrResonators );

        this.mSlider = new HSlider();
        with(this.mSlider){
            minimum = 1;
            maximum = 10;
            liveDragging = true;
            buttonMode = true;
            labels = ["", "mass", ""];
        }
        this.mSlider.addEventListener(Event.CHANGE, onChangeM);

        this.kSlider = new HSlider();
        with(this.kSlider){
            minimum = 10;
            maximum = 500;
            liveDragging = true;
            buttonMode = true;
            labels = ["", "spring constant", ""];
        }

        this.kSlider.addEventListener(Event.CHANGE, onChangeK);

        this.resetButton = new Button();
        with(this.resetButton){
            label = " Reset All "
            buttonMode = true;
        }
        this.resetButton.addEventListener( MouseEvent.MOUSE_UP, resetResonators );

        this.addChild( this.background );
        this.background.addChild( nbrResonatorsSlider );
        this.background.addChild( dampingSlider );

        this.innerBckgrnd.addChild(this.mSlider);
        this.innerBckgrnd.addChild(this.kSlider);
        this.background.addChild( innerBckgrnd );
        this.background.addChild(this.resetButton);

    } //end of init()


    //add time rate adjuster here


    public function setResonatorIndex( rNbr: int ): void {
        this.selectedResonatorNbr = rNbr;
        var rNbr_str: String = rNbr.toString();
        //this.rNbr_txt.text = rNbr_str;
        var m: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getM();
        //trace("ControlPanel.setResonatorIndex. m = "+m);
        this.mSlider.value = m;
        var k: Number = this.shakerModel.resonatorModel_arr[rNbr - 1].getK();
        //trace("ControlPanel.setResonatorIndex. k = "+k);
        this.kSlider.value = k;
    }

    public function setDamping( evt: Event ): void {
        var b: Number = this.dampingSlider.value;
        this.shakerModel.setB( b );
    }

    private function onChangeNbrResonators( evt: Event ): void {
        var nbrR: int = this.nbrResonatorsSlider.value;
        //trace("ControlPanel.setNbrResonators called. nbrR = " + nbrR);
        this.setNbrResonators( nbrR );
    }

    public function setNbrResonators( nbrR: int ): void {
        this.myMainView.setNbrResonators( nbrR );
    }

    private function onChangeM(evt:Event):void{
      this.setMass();
    }

    public function setMass(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var m: Number = this.mSlider.value;
        this.shakerModel.resonatorModel_arr[indx].setM( m );
        //this.setFreqTextField();
        //trace("ControlPanel.setMass() mass = "+ m);
    }

    private function onChangeK(evt:Event):void{
        this.setK();
    }

    public function setK(): void {
        var indx: int = this.selectedResonatorNbr - 1;
        var k: Number = this.kSlider.value;
        this.shakerModel.resonatorModel_arr[indx].setK( k );
        //this.setFreqTextField();
        //trace("ControlPanel.setK() k = "+ k);
    }

    private function resetResonators(evt:MouseEvent): void {
        this.shakerModel.resetInitialResonatorArray();
        //this.setResonatorIndex( this.selectedResonatorNbr );
        //trace("ControlPanel.resetResonators() called.");
    }

}//end of class

}//end of package