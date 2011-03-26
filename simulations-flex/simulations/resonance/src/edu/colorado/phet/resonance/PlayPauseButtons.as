/**
 * Created by ${PRODUCT_NAME}.
 * User: General User
 * Date: 12/12/10
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance {

import flash.display.Graphics;
import flash.display.LineScaleMode;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

import mx.controls.HSlider;
import mx.core.UIComponent;

public class PlayPauseButtons extends UIComponent {          //cannot extend Sprite, since contains an HSlider
    //private var canvas:Sprite;
    private var myMainView: MainView;
    private var myShakerModel: ShakerModel;
    private var timeRateSlider:HSlider;
    private var playPauseButton: Sprite;
    private var stepButton: Sprite;
    private var playIcon: Sprite;                //overlayed on playPauseButton
    private var pauseIcon: Sprite;              //overlayed on playPauseButton
    //private var stepIcon:Sprite;                //overlayed on stepButton
    private var timeRate_txt:TextField;
    private var slow_txt:TextField;
    private var normal_txt:TextField;
    private var playPause_txt: TextField;
    private var singleStep_txt: TextField;
    private var paused_txt: TextField
    private var tFormat0: TextFormat;
    private var tFormat1: TextFormat;
    private var tFormat2: TextFormat;
    private var paused: Boolean;

    //public var playSlashPause_str: String;
    public var timeRate_str:String;
    public var slow_str:String;
    public var normal_str:String;
    public var play_str: String;
    public var pause_str: String;    //Note well: pause_str and paused_str are two differerent strings
    public var paused_str: String;
    public var singleStep_str: String;


    public function PlayPauseButtons( myMainView: MainView, myShakerModel: ShakerModel ) {

        this.initializeStrings();

        this.myMainView = myMainView;
        this.myShakerModel = myShakerModel;
        this.timeRateSlider = new HSlider();
        this.playPauseButton = new Sprite();
        this.stepButton = new Sprite();
        this.playIcon = new Sprite();
        this.pauseIcon = new Sprite();
        this.timeRate_txt = new TextField();
        this.slow_txt = new TextField();
        this.normal_txt = new TextField();
        this.playPause_txt = new TextField();
        this.paused_txt = new TextField();
        this.singleStep_txt = new TextField();
        this.tFormat0 = new TextFormat();
        this.tFormat1 = new TextFormat();
        this.tFormat2 = new TextFormat();

        this.drawGraphics();
        this.initializeTextFields();
        this.formatSlider(this.timeRateSlider);
        this.positionFields();
        this.initializeControls();


        //this.addChild(this.canvas);
        this.playPauseButton.addChild( this.playIcon );
        this.playPauseButton.addChild( this.pauseIcon );
        this.addChild(this.timeRate_txt);
        this.addChild(this.slow_txt);
        this.addChild(this.normal_txt);
        this.addChild(this.timeRateSlider);
        this.addChild( this.playPauseButton );
        this.addChild( this.playPause_txt );
        this.addChild( this.stepButton );
        this.addChild( this.singleStep_txt );
        this.addChild( this.paused_txt );

    }  //end of constructor

    public function initializeStrings(): void {
        //this.playSlashPause_str = "play/pause";
        this.timeRate_str = "Time Rate";
        this.slow_str = "slow";
        this.normal_str = "normal";
        this.play_str = "play";
        this.pause_str = "pause";
        this.paused_str = "PAUSED";
        this.singleStep_str = "step";
    }

    private function drawGraphics(): void {
        //draw button body
        var g1: Graphics = this.playPauseButton.graphics;
        var g2: Graphics = this.pauseIcon.graphics;
        var g3: Graphics = this.playIcon.graphics;
        var g4: Graphics = this.stepButton.graphics;
        var bH: Number = 25;    //width of button
        var bW: Number = 25;    //height of button

        //play/pause button body
        g1.clear();
        g1.lineStyle( 2.5, 0x777777, 1, true, LineScaleMode.NONE );
        g1.beginFill( 0xdddddd );
        g1.drawRoundRect( -bW / 2, -bH / 2, bW, bH, bH / 2 )
        g1.endFill();
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
        this.playPauseButton.buttonMode = true;
        this.stepButton.buttonMode = true;
        this.playPauseButton.mouseChildren = false;
        this.paused = false;
        //this.myShakerModel.unPauseSim();
        this.playIcon.visible = false;
        this.pauseIcon.visible = true;
        var thisObject: PlayPauseButtons = this;
        //this.playPauseButton
        this.timeRateSlider.addEventListener(Event.CHANGE, onChangeTimeRate );
        this.playPauseButton.addEventListener( MouseEvent.MOUSE_UP, onMouseClick );
        this.playPauseButton.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.playPauseButton.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.stepButton.addEventListener( MouseEvent.MOUSE_DOWN, singleStep );
        this.stepButton.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.stepButton.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );

        function onChangeTimeRate(evt:Event):void{
             var rate:Number = evt.target.value;
             thisObject.myShakerModel.setTRate(rate);
            //trace("PlayPauseButtons.onChangeTimeRate = " + rate);
        }

        function onMouseClick( evt: MouseEvent ): void {
            if ( thisObject.paused ) {   //unpause sim
                thisObject.paused = false;
                thisObject.playIcon.visible = false;
                thisObject.pauseIcon.visible = true;
                thisObject.playPause_txt.text = thisObject.pause_str;
                thisObject.paused_txt.visible = false;
                thisObject.myShakerModel.unPauseSim();
            }
            else {                      //pause sim
                thisObject.paused = true;
                thisObject.playIcon.visible = true;
                thisObject.pauseIcon.visible = false;
                thisObject.playPause_txt.text = thisObject.play_str;
                thisObject.paused_txt.visible = true;
                thisObject.myShakerModel.pauseSim();
            }
        }

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseOver" ) {
                thisObject.tFormat1.bold = true;
            } else if ( evt.type == "mouseOut" ) {
                thisObject.tFormat1.bold = false;
            }
            if ( evt.target == thisObject.playPauseButton ) {
                thisObject.playPause_txt.setTextFormat( thisObject.tFormat1 );
            }
            else {
                thisObject.singleStep_txt.setTextFormat( thisObject.tFormat1 );
            }
        }//end of buttonBehave

        function singleStep( evt: MouseEvent ): void {
            if ( !thisObject.paused ) {
                thisObject.paused = true;
                thisObject.playIcon.visible = true;
                thisObject.pauseIcon.visible = false;
                thisObject.playPause_txt.text = thisObject.pause_str;
                thisObject.paused_txt.visible = true;
                thisObject.myShakerModel.pauseSim();
            }
            thisObject.myShakerModel.singleStepWhenPaused();
        }
    }

    public function unPauseExternally(): void {
        if ( this.paused ) {
            this.paused = false;
            this.playIcon.visible = false;
            this.pauseIcon.visible = true;
            this.playPause_txt.text = this.pause_str;
            this.paused_txt.visible = false;
            this.myShakerModel.unPauseSim();
        }
    }

    public function setSliderExternally(rate:Number):void{
        this.timeRateSlider.value = rate;
        this.myShakerModel.setTRate(rate);
    }

    private function initializeTextFields(): void {
        this.setTextField( this.timeRate_txt);
        this.setTextField(this.slow_txt);
        this.setTextField(this.normal_txt);
        this.setTextField( this.playPause_txt );
        this.setTextField( this.singleStep_txt );
        this.setTextField( this.paused_txt );
        this.timeRate_txt.text = this.timeRate_str;
        this.slow_txt.text = this.slow_str;
        this.normal_txt.text = this.normal_str;
        this.playPause_txt.text = this.pause_str;
        this.singleStep_txt.text = this.singleStep_str;
        this.paused_txt.text = this.paused_str;
        this.paused_txt.visible = false;
        //this.playPause_txt.selectable = false;
        //this.paused_txt.selectable = false;
        //this.playPause_txt.autoSize = TextFieldAutoSize.CENTER;
        //this.paused_txt.autoSize = TextFieldAutoSize.CENTER;
        this.tFormat0.font = "Arial";
        this.tFormat0.color = 0x000000;
        this.tFormat0.size = 12;
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 15;
        this.tFormat2.font = "Arial";
        this.tFormat2.bold = true;
        this.tFormat2.color = 0xffcc33;   //background is 0xffff99
        this.tFormat2.size = 95;
        this.timeRate_txt.setTextFormat( this.tFormat0 );
        this.slow_txt.setTextFormat( this.tFormat0 );
        this.normal_txt.setTextFormat( this.tFormat0 );
        this.playPause_txt.setTextFormat( this.tFormat1 );
        this.singleStep_txt.setTextFormat( this.tFormat1 );
        this.paused_txt.setTextFormat( this.tFormat2 );
        this.playPause_txt.defaultTextFormat = this.tFormat1;
        this.singleStep_txt.defaultTextFormat = this.tFormat1;
        this.paused_txt.defaultTextFormat = this.tFormat2;
    }

    private function setTextField( tField: TextField ): void {
        with ( tField ) {
            selectable = false;
            autoSize = TextFieldAutoSize.CENTER;
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

        this.timeRateSlider.x = -1.5*this.timeRateSlider.width;
        this.timeRateSlider.y = -0.5*this.timeRateSlider.height;
        this.timeRate_txt.x = -this.timeRateSlider.width - 0.5*this.timeRate_txt.width;
        this.slow_txt.x =  this.timeRateSlider.x;
        this.slow_txt.y = 3;
        this.normal_txt.x = 0.5*this.timeRateSlider.x;
        this.normal_txt.y = 3;
        this.timeRate_txt.y = this.timeRateSlider.y - 0.5*this.timeRate_txt.height;
        this.playPause_txt.x = -0.5 * this.playPause_txt.width;
        this.playPause_txt.y = 0.5 * this.playPauseButton.height;
        this.paused_txt.x = -0.5 * this.paused_txt.width;
        this.paused_txt.y = -550;

        this.stepButton.x = 3 * this.stepButton.width;
        this.stepButton.y = 0;
        this.singleStep_txt.x = this.stepButton.x - 0.5 * this.singleStep_txt.width;
        this.singleStep_txt.y = 0.5 * this.stepButton.height;
    }

} //end of class
} //end of package
