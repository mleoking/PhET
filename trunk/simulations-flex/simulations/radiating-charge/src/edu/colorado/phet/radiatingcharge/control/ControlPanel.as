/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/11/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.control {
import edu.colorado.phet.flashcommon.controls.NiceButton2;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.radiatingcharge.model.FieldModel;
import edu.colorado.phet.radiatingcharge.util.SpriteUIComponent;
import edu.colorado.phet.radiatingcharge.view.MainView;

import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.ComboBox;
import mx.events.DropdownEvent;

//Control Panel for Radiating Charge sim
public class ControlPanel extends Canvas {
    private var myMainView:MainView;
    private var myFieldModel:FieldModel;
    private var background: VBox;
    private var pauseButton:NiceButton2;
    private var myComboBox: ComboBox;
    private var choiceList_arr:Array;

    //internationalized strings
    public var start_str:String;
    public var stop_str:String;
    public var pause_str:String;
    public var unPause_str:String;

    public function ControlPanel( mainView:MainView, model:FieldModel ) {
        super();
        this.myMainView = mainView;
        this.myFieldModel = model;
        this.init();
    }

    private function init():void{
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

        this.pauseButton = new NiceButton2( 100, 25, pause_str, pauseUnPause, 0x00ff00, 0x000000 );
        this.myComboBox = new ComboBox();
        this.choiceList_arr = new Array( 3 );
        choiceList_arr = [ "sinusoid", "linear", "circular"];
        myComboBox.dataProvider = choiceList_arr;
        myComboBox.addEventListener( DropdownEvent.CLOSE, comboBoxListener );

        this.addChild( background );
        this.background.addChild( new SpriteUIComponent( pauseButton, true ) );
        this.background.addChild( myComboBox );
    }//end init()

    private function initializeStrings():void{
        pause_str = FlexSimStrings.get( "pause", "Pause" );
        unPause_str = FlexSimStrings.get( "unPause", "UnPause" );
        start_str = FlexSimStrings.get( "start", "Start" );
        stop_str = FlexSimStrings.get( "stop", "Stop" );
    }

    private function pauseUnPause():void{
        if( myFieldModel.paused ){
            this.myFieldModel.paused = false;
        }else{
            this.myFieldModel.paused = true;
        }
    }//end pauseUnPause

    private function comboBoxListener( evt: DropdownEvent ):void{
        trace("ControlPanel.comboBoxListener: selected item = "+evt.currentTarget.selectedItem )  ;
    }
} //end class
} //end package
