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
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class PlayPauseButtons extends Sprite {
    //private var canvas:Sprite;
    private var myMainView: MainView;
    private var myShakerModel: ShakerModel;
    private var playPauseButton: Sprite;
    private var playIcon: Sprite;                //overlayed on playPauseButton
    private var pauseIcon: Sprite;              //overlayed on playPauseButton
    private var playPause_txt: TextField;
    private var paused_txt: TextField;
    private var tFormat1: TextFormat;
    private var tFormat2: TextFormat;
    private var paused: Boolean;

    public var playPause_str: String;
    //private var play_str: String;
    //private var pause_str: String;
    public var paused_str: String;


    public function PlayPauseButtons( myMainView: MainView, myShakerModel: ShakerModel ) {

        this.initializeStrings();
        this.myMainView = myMainView;
        this.myShakerModel = myShakerModel;
        this.playPauseButton = new Sprite();
        this.playIcon = new Sprite();
        this.pauseIcon = new Sprite();
        this.playPause_txt = new TextField();
        this.paused_txt = new TextField();
        this.tFormat1 = new TextFormat();
        this.tFormat2 = new TextFormat();

        this.playPause_str = "play/pause";
        //this.play_str = "play";
        //this.pause_str = "pause";
        this.paused_str = "PAUSED";

        this.drawGraphics();
        this.initializeTextFields();
        this.positionTextFields();
        this.initializeButton();

        //this.addChild(this.canvas);
        this.playPauseButton.addChild( this.playIcon );
        this.playPauseButton.addChild( this.pauseIcon );
        this.addChild( this.playPauseButton );
        this.addChild( this.playPause_txt );
        this.addChild( this.paused_txt );

    }  //end of constructor

    public function initializeStrings():void{

    }

    private function drawGraphics(): void {
        //draw button body
        var g1: Graphics = this.playPauseButton.graphics;
        var g2: Graphics = this.pauseIcon.graphics;
        var g3: Graphics = this.playIcon.graphics;
        var bH: Number = 25;    //width of button
        var bW: Number = 25;    //height of button

        //button body
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

    }


    private function initializeButton(): void {
        this.playPauseButton.buttonMode = true;
        this.playPauseButton.mouseChildren = false;
        this.paused = false;
        //this.myShakerModel.unPauseSim();
        this.playIcon.visible = false;
        this.pauseIcon.visible = true;
        var thisObject: PlayPauseButtons = this;
        this.playPauseButton.addEventListener( MouseEvent.MOUSE_UP, onMouseClick );

        function onMouseClick( evt: MouseEvent ): void {
            if ( thisObject.paused ) {   //unpause sim
                thisObject.paused = false;
                thisObject.playIcon.visible = false;
                thisObject.pauseIcon.visible = true;
                //thisObject.playPause_txt.text = thisObject.play_str;
                thisObject.paused_txt.visible = false;
                thisObject.myShakerModel.unPauseSim();
            }
            else {                      //pause sim
                thisObject.paused = true;
                thisObject.playIcon.visible = true;
                thisObject.pauseIcon.visible = false;
                //thisObject.playPause_txt.text = thisObject.pause_str;
                thisObject.paused_txt.visible = true;
                thisObject.myShakerModel.pauseSim();
            }
        }
    }

    public function unPauseExternally(): void {
        if ( this.paused ) {
            this.paused = false;
            this.playIcon.visible = false;
            this.pauseIcon.visible = true;
            //thisObject.playPause_txt.text = thisObject.play_str;
            this.paused_txt.visible = false;
            this.myShakerModel.unPauseSim();
        }
    }

    private function initializeTextFields(): void {
        this.playPause_txt.text = this.playPause_str;
        this.paused_txt.text = this.paused_str;
        this.paused_txt.visible = false;
        this.playPause_txt.selectable = false;
        this.paused_txt.selectable = false;
        this.playPause_txt.autoSize = TextFieldAutoSize.CENTER;
        this.paused_txt.autoSize = TextFieldAutoSize.CENTER;
        this.tFormat1.font = "Arial";
        this.tFormat1.color = 0x000000;
        this.tFormat1.size = 15;
        this.tFormat2.font = "Arial";
        this.tFormat2.bold = true;
        this.tFormat2.color = 0xffcc33;   //background is 0xffff99
        this.tFormat2.size = 95;
        this.playPause_txt.setTextFormat( this.tFormat1 );
        this.paused_txt.setTextFormat( this.tFormat2 );
        this.playPause_txt.defaultTextFormat = this.tFormat1;
        this.paused_txt.defaultTextFormat = this.tFormat2;

    }

    private function positionTextFields(): void {
        this.playPause_txt.x = -0.5 * this.playPause_txt.width;
        this.playPause_txt.y = 0.5 * this.playPauseButton.height;
        this.paused_txt.x = -0.5 * this.paused_txt.width;
        this.paused_txt.y = -550;
    }

} //end of class
} //end of package
