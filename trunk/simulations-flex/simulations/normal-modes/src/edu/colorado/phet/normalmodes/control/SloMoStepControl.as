/**
 * Created by ${PRODUCT_NAME}.
 * User: General User
 * Date: 12/12/10
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.*;

import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.model.Model1;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Graphics;
import flash.display.LineScaleMode;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

import mx.containers.Canvas;

import mx.controls.HSlider;
import mx.core.UIComponent;

public class SloMoStepControl extends UIComponent {          //cannot extend Sprite, since contains an HSlider
    //private var canvas:Sprite;
    //private var myMainView: MainView;
    private var myControlPanel: ControlPanel;
    private var myModel: Object; //Model1 or Model1, can change with setModel();
    //private var backGroundRectangle:Sprite;
    private var timeRateSlider:HSlider;
    //private var playPauseButton: Sprite;
    private var stepButton: Sprite;
    private var playIcon: Sprite;                //overlayed on playPauseButton
    private var pauseIcon: Sprite;              //overlayed on playPauseButton
    //private var stepIcon:Sprite;                //overlayed on stepButton
    private var currentTime_txt:TextField;
    private var simSpeed_txt:TextField;
    private var slow_txt:TextField;
    private var normal_txt:TextField;
    //private var playPause_txt: TextField;
    private var singleStep_txt: TextField;
    //private var sloMo_txt: TextField;
    private var paused_txt: TextField
    private var tFormat0: TextFormat;
    private var tFormat1: TextFormat;
    private var tFormat2: TextFormat;
    private var paused: Boolean;

    //public var playSlashPause_str: String;
    //public var timeEquals_str:String;
    //public var timeInSec_str:String;
    public var simSpeed_str:String;
    public var slow_str:String;
    public var normal_str:String;
    public var singleStep_str: String;


    public function SloMoStepControl( myControlPanel: ControlPanel, myModel: Object ) {  //( myMainView: MainView, myModel: Object ) {

        this.initializeStrings();
        this.myControlPanel = myControlPanel;
        this.myModel = myModel;
        //this.backGroundRectangle = new Sprite();    //for testing only
        this.timeRateSlider = new HSlider();
        this.stepButton = new Sprite();
        this.playIcon = new Sprite();
        this.pauseIcon = new Sprite();
        this.currentTime_txt = new TextField();
        this.simSpeed_txt = new TextField();
        this.slow_txt = new TextField();
        this.normal_txt = new TextField();
        //this.playPause_txt = new TextField();
        this.paused_txt = new TextField();
        //this.sloMo_txt = new TextField();
        this.singleStep_txt = new TextField();
        this.tFormat0 = new TextFormat();
        this.tFormat1 = new TextFormat();
        this.tFormat2 = new TextFormat();

        this.drawGraphics();
        this.initializeTextFields();
        this.formatSlider(this.timeRateSlider);
        this.positionFields();
        this.initializeControls();

        //this.addChild( this.backGroundRectangle );
        this.addChild(this.simSpeed_txt);
        this.addChild(this.slow_txt);
        this.addChild(this.normal_txt);
        this.addChild(this.timeRateSlider);
        this.addChild( this.stepButton );
        this.addChild( this.singleStep_txt );
        //this.addChild( this.paused_txt );
        //this.addChild( this.sloMo_txt );
        //this.explicitHeight = 30;
        //this.explicitWidth = 100;

    }  //end of constructor

    public function initializeStrings(): void {
        //this.timeEquals_str = "time = ";  //this string set by setTimeLabel() but need initial string to make height for positionLabels()
        this.simSpeed_str = FlexSimStrings.get("simSpeed", "sim speed");
        this.slow_str = FlexSimStrings.get("slow", "slow");
        this.normal_str = FlexSimStrings.get("normal", "normal");
        this.singleStep_str = FlexSimStrings.get("step", "step");
    }

    private function drawGraphics(): void {
        //draw button body
        //var g1: Graphics = this.backGroundRectangle.graphics;
        var g2: Graphics = this.pauseIcon.graphics;
        var g3: Graphics = this.playIcon.graphics;
        var g4: Graphics = this.stepButton.graphics;
        var bH: Number = 25;    //width of button
        var bW: Number = 25;    //height of button

        //background rectangle
        //g1.clear();
        //g1.lineStyle( 1, 0x000000, 1 );
        //g1.drawRect( 0, 0, 200, 50 );
        //pause icon
        g2.clear();
        g2.lineStyle( 1, 0x000000, 1, true, LineScaleMode.NONE )
        g2.beginFill( 0x666666 );
        var barW: Number = 0.15 * bW;
        var barH: Number = 0.6 * bH;
        g2.drawRect( -1.4 * barW, -0.5 * barH, barW, barH );
        g2.drawRect( 0.4 * barW, -0.5 * barH, barW, barH );
        g2.endFill();
        //play icon
        g3.clear();
        g3.lineStyle( 1, 0x000000, 1, true, LineScaleMode.NONE )
        g3.beginFill( 0x666666 );
        var f: Number = 0.3;
        g3.moveTo( f * bW, 0 );
        g3.lineTo( -f * bW, f * bH );
        g3.lineTo( -f * bW, -f * bH );
        g3.lineTo( f * bW, 0 );
        g3.endFill();
        //singleStep button body
        g4.clear();
        g4.lineStyle( 2.5, 0x777777, 1, true, LineScaleMode.NONE );
        g4.beginFill( 0xdddddd );
        g4.drawRoundRect( -bW / 2, -bH / 2, bW, bH, bH / 2 )
        g4.endFill();
        //draw singleStep icon on button
        g4.lineStyle( 1, 0x000000, 1, true, LineScaleMode.NONE )
        g4.beginFill( 0x666666 );
        barH = 0.5 * bH;
        barW = 0.15 * bW;
        f = 0.3;
        g4.drawRect( -1.75 * barW, -0.5 * barH, barW, barH );
        g4.moveTo( f * bW, 0 );
        g4.lineTo( 0, 0.5 * barH );
        g4.lineTo( 0, -0.5 * barH );
        g4.lineTo( f * bW, 0 );
        g4.endFill();

    } //end drawGraphics()


    private function initializeControls(): void {
        this.timeRateSlider.value = 1;
        this.stepButton.buttonMode = true;
        this.paused = false;
        this.playIcon.visible = false;
        this.pauseIcon.visible = true;
        var thisObject: SloMoStepControl = this;
        this.timeRateSlider.addEventListener(Event.CHANGE, onChangeTimeRate );
        this.stepButton.addEventListener( MouseEvent.MOUSE_DOWN, singleStep );
        this.stepButton.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.stepButton.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );

        function onChangeTimeRate(evt:Event):void{
             var rate:Number = evt.target.value;
             thisObject.myModel.setTRate(rate);
//            if(rate != 1){
//               thisObject.sloMo_txt.visible = true;
//            }else{
//               thisObject.sloMo_txt.visible = false;
//            }
            //trace("PlayPauseButtons.onChangeTimeRate = " + rate);
        }

        function onMouseClick( evt: MouseEvent ): void {
            if ( thisObject.paused ) {   //unpause sim
                thisObject.paused = false;
                thisObject.myModel.unPauseSim();
            }
            else {                      //pause sim
                thisObject.paused = true;
                thisObject.playIcon.visible = true;
                thisObject.pauseIcon.visible = false;
                thisObject.myModel.pauseSim();
            }
        }

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseOver" ) {
                thisObject.tFormat1.bold = true;
            } else if ( evt.type == "mouseOut" ) {
                thisObject.tFormat1.bold = false;
            }
            thisObject.singleStep_txt.setTextFormat( thisObject.tFormat1 )
        }//end of buttonBehave

        function singleStep( evt: MouseEvent ): void {
            if ( !thisObject.myModel.paused ) {
                thisObject.myModel.pauseSim();
                //thisObject.myMainView.myControlPanel.initializeStartStopButton();
                thisObject.myControlPanel.initializeStartStopButton();
                thisObject.paused = true;
            }
            thisObject.myModel.singleStepWhenPaused();
        }
    }//end initializeControls

    public function setModel( currentModel: Object ):void{
        this.myModel.unregisterView( this );
        this.myModel = currentModel;
        this.myModel.registerView( this );
    }

    public function unPauseExternally(): void {
        if ( this.paused ) {
            this.paused = false;
            this.playIcon.visible = false;
            this.pauseIcon.visible = true;
            this.paused_txt.visible = false;
            this.myModel.unPauseSim();
        }
    }

    public function setSliderExternally(rate:Number):void{
        this.timeRateSlider.value = rate;
        this.myModel.setTRate(rate);
    }

    private function initializeTextFields(): void {
        this.setTextField( this.simSpeed_txt);
        this.setTextField(this.slow_txt);
        this.setTextField(this.normal_txt);
        //this.setTextField( this.playPause_txt );
        this.setTextField( this.singleStep_txt );
        this.setTextField( this.paused_txt );
        //this.setTextField( this.sloMo_txt );
        //this.currentTime_txt.text = timeEquals_str;
        this.currentTime_txt.selectable = false;
        this.currentTime_txt.autoSize = TextFieldAutoSize.LEFT;
        this.simSpeed_txt.text = this.simSpeed_str;
        this.slow_txt.text = this.slow_str;
        this.normal_txt.text = this.normal_str;
        //this.playPause_txt.text = this.pause_str;
        this.singleStep_txt.text = this.singleStep_str;
        //this.paused_txt.text = this.paused_str;
        this.paused_txt.visible = false;
        this.tFormat0.font = "Arial";
        this.tFormat0.color = 0x000000;
        this.tFormat0.size = 12;
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 12;
        this.tFormat2.font = "Arial";
        this.tFormat2.bold = true;
        this.tFormat2.color = 0xf1c100;   //background is 0xffff99
        this.tFormat2.size = 25;
        this.currentTime_txt.defaultTextFormat = this.tFormat0;
        this.slow_txt.setTextFormat( this.tFormat0 );
        this.normal_txt.setTextFormat( this.tFormat0 );
        this.tFormat0.italic = true;
        this.simSpeed_txt.setTextFormat( this.tFormat0 );
        this.singleStep_txt.setTextFormat( this.tFormat1 );
        this.paused_txt.setTextFormat( this.tFormat2 );
        this.singleStep_txt.defaultTextFormat = this.tFormat1;
        this.paused_txt.defaultTextFormat = this.tFormat2;
    }

    private function setTextField( tField: TextField ): void {
        with ( tField ) {
            selectable = false;
            autoSize = TextFieldAutoSize.CENTER;
            //for testing
            //border = true;
            //borderColor = 0x000000;
        }
    }

    private function formatSlider( mySlider: HSlider ): void {
        mySlider.minimum = 0.02;
        mySlider.maximum = 1;
        mySlider.buttonMode = true;
        mySlider.liveDragging = true;
        //mySlider.percentWidth = 100;
        mySlider.width = 100;
        mySlider.height = 20;
        mySlider.showDataTip = false;
        //mySlider.setStyle( "labelOffset", 25 );
        mySlider.setStyle( "invertThumbDirection", true );
        //setStyle( "dataTipOffset", -50 );  //this does not work.  Why not?
        mySlider.setStyle( "fontFamily", "Arial" );
    }

    private function positionFields(): void {
        //registration point at upper left, required for flex layout
        //careful layout and setting of explicitWidth and explicitHeight required for UIComponent to function
        //properly in autolayout
        this.timeRateSlider.x = 0;  
        this.simSpeed_txt.x = timeRateSlider.x + 0.5*timeRateSlider.width - 0.5*simSpeed_txt.width;
        this.slow_txt.x =  this.timeRateSlider.x;
        this.normal_txt.x = timeRateSlider.x + timeRateSlider.width - normal_txt.width;

        this.simSpeed_txt.y = 0;
        this.timeRateSlider.y = simSpeed_txt.height - 0.5*this.timeRateSlider.height;
        this.slow_txt.y = this.timeRateSlider.y + 0.6*this.timeRateSlider.height;
        this.normal_txt.y = this.timeRateSlider.y + 0.6*this.timeRateSlider.height;
        //registration point of step button is at center of button
        this.stepButton.x = this.timeRateSlider.width + 0.8 * this.stepButton.width;
        this.stepButton.y = 0.5*this.stepButton.height;
        this.singleStep_txt.x = this.stepButton.x - 0.5 * this.singleStep_txt.width;
        this.singleStep_txt.y = this.stepButton.y + 0.5 * this.stepButton.height;
        
        this.explicitHeight = this.singleStep_txt.y + 0.8*this.singleStep_txt.height;
        this.explicitWidth = this.singleStep_txt.x + this.singleStep_txt.width;
        //this.currentTime_txt.x = this.timeRateSlider.x;
        //this.currentTime_txt.y = this.timeRateSlider.y - 1.5*this.currentTime_txt.height;
 
        //this.timeRateSlider.y - 0.5*this.simSpeed_txt.height;
        //this.paused_txt.x = -0.5 * this.paused_txt.width;
        //this.paused_txt.y = -1.3 * this.paused_txt.height;
    } //end position fields

    //unused in this sim
   /* public function setTimeLabel():void{
        var timeInSec:Number = this.myModel.t;
        var timeInSec_str:String = timeInSec.toFixed( 2 );
        //this.timeEquals_str =  FlexSimStrings.get("timesEquals", "time = {0} s",[timeInSec_str]);
        this.currentTime_txt.text =  FlexSimStrings.get("timesEquals", "time = {0} s",[timeInSec_str]);
    }
    */
    public function update():void{
        //do nothing in this sim
        //this.setTimeLabel();
    }

} //end of class
} //end of package
