/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 6/11/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.control {
import edu.colorado.phet.flashcommon.controls.HorizontalSlider;
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flashcommon.controls.NiceRadioButton;
import edu.colorado.phet.flashcommon.controls.NiceRadioButtonGroup;
import edu.colorado.phet.flashcommon.controls.PlayPauseButton;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.triglab.view.MainView;

import flash.display.Sprite;
import flash.events.Event;

import mx.containers.Canvas;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.CheckBox;
import mx.controls.RadioButton;
import mx.controls.RadioButtonGroup;
import mx.events.ItemClickEvent;

/**
 * Control Panel for Trig Lab sim
 * Control panel must be flex canvas to use flex auto-layout
 */

public class ControlPanel extends Canvas {
    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
    private var background: VBox;
//    private var cos_cb: CheckBox;
//    private var sin_cb: CheckBox;
//    private var tan_cb: CheckBox;

    private var cosSinTan_nrbg: NiceRadioButtonGroup;
    private var cos_nrb: NiceRadioButton;
    private var sin_nrb: NiceRadioButton;
    private var tan_nrb: NiceRadioButton;






    //internationalized strings
    private var cos_str:String;
    private var sin_str:String;
    private var tan_str:String;




    public function ControlPanel( mainView:MainView, model:TrigModel ) {
        super();
        this.myMainView = mainView;
        this.myTrigModel = model;
        this.init();
    }

    private function init():void {
        this.initializeStrings();
        this.background = new VBox();
        //this.firstPanel = new VBox();
        with ( this.background ) {
            setStyle( "backgroundColor", 0x55ff55 );    //0x88ff88
            setStyle( "borderStyle", "solid" )
            setStyle( "borderColor", 0x009900 );  //0x009900
            setStyle( "cornerRadius", 10 );
            setStyle( "borderThickness", 2 );
            setStyle( "paddingTop", 15 );
            setStyle( "paddingBottom", 5 );
            setStyle( "paddingRight", 15 );
            setStyle( "paddingLeft", 15 );
            setStyle( "verticalGap", 10 );
            setStyle( "horizontalAlign", "center" );
        }
//        this.cos_cb = new CheckBox();
//        this.sin_cb = new CheckBox();
//        this.tan_cb = new CheckBox();
//        cos_cb.addEventListener( Event.CHANGE, cosCheckBoxListener );
//        var cosCheckBoxLabel: NiceLabel = new NiceLabel( 15, cos_str, false, cos_cb );
//        //cos_cb.setStyle( "label", cos_str );
//        cos_cb.label = cos_str;
//        cos_cb.setStyle( "fontSize", 25 );
//
//        sin_cb.addEventListener( Event.CHANGE, sinCheckBoxListener );
//        var sinCheckBoxLabel: NiceLabel = new NiceLabel( 15, sin_str, false, sin_cb );
//        sin_cb.label = sin_str;
//        sin_cb.setStyle( "fontSize", 25 );
//
//        tan_cb.addEventListener( Event.CHANGE, tanCheckBoxListener );
//        var tanCheckBoxLabel: NiceLabel = new NiceLabel( 15, tan_str, false, tan_cb );
//        tan_cb.label = tan_str;
//        tan_cb.setStyle( "fontSize", 25 );

        //set up radio buttons
        this.cosSinTan_nrbg = new NiceRadioButtonGroup();
        this.cos_nrb = new NiceRadioButton( cos_str,  true );
        this.sin_nrb = new NiceRadioButton( sin_str,  false );
        this.tan_nrb = new NiceRadioButton( tan_str,  false );
        this.initializeNiceRadioButton( cos_nrb );
        this.initializeNiceRadioButton( sin_nrb );
        this.initializeNiceRadioButton( tan_nrb );
        this.cosSinTan_nrbg.setListener( this );
        this.cosSinTan_nrbg.selectButton( cos_nrb );


        //layout controls
        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( cos_nrb ) );
        this.background.addChild( new SpriteUIComponent( sin_nrb ) );
        this.background.addChild( new SpriteUIComponent( tan_nrb ) );


    }//end init()



    private function initializeStrings():void{
        cos_str = FlexSimStrings.get( "cos", "cos" );
        sin_str = FlexSimStrings.get( "sin", "sin" );
        tan_str = FlexSimStrings.get( "tan", "tan" );
    }

    private function initializeNiceRadioButton( nrb: NiceRadioButton ):void{
        nrb.group = cosSinTan_nrbg;
        nrb.label.setFontColor( 0x000000 );
        nrb.label.setFontSize( 25 );
        nrb.setColorsOfDeselectedIcon( 0x888888, 0xeeeeee );
        nrb.setColorsOfSelectedIcon( 0x888888, 0x000000 );
        nrb.setLabelColors( 0x000000, 0x006600 );
    }

    public function niceRadioGroupListener( selectedButtonIndex: int ):void{
        var choice:int = selectedButtonIndex;
        //0 = cos, 1 = sin, 2 = tan
        this.myMainView.myGraphView.selectWhichGraphToShow( choice );
        this.myMainView.myUnitCircleView.trigMode = choice;
    }//end NiceRadioGroupListener

//    private function cosCheckBoxListener( evt: Event ):void{
//        var selected:Boolean = evt.target.selected;
//        myMainView.myGraphView.showCos = selected;
//
//    }
//    private function sinCheckBoxListener( evt: Event ):void{
//        var selected:Boolean = evt.target.selected;
//        myMainView.myGraphView.showSin = selected;
//
//    }
//    private function tanCheckBoxListener( evt: Event ):void{
//        var selected:Boolean = evt.target.selected;
//        myMainView.myGraphView.showTan = selected;
//
//    }





    public function resetAll():void{

    }







} //end class
} //end package
