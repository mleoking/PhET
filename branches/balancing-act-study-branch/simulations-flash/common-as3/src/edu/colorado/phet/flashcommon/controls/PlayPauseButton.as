/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 8/9/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.Graphics;
import flash.display.LineScaleMode;
import flash.display.Sprite;
import flash.events.MouseEvent;

/**
 * Single play/pause button.  No labels, just standard iconHolders for play and pause
 */
public class PlayPauseButton extends Sprite {
    private var iconHolder: Sprite;
    private var playIcon: Sprite;
    private var pauseIcon: Sprite;
    private var buttonFunction: Function;
    private var pushedIn: Boolean;  //true if mouse is over button and clicked down
    private var _paused: Boolean;   //false if in playing state (pause iconHolder showing);  true if in paused state (play button showing)

    public function PlayPauseButton( buttonFunction: Function = null ) {
        if( buttonFunction != null ){
            this.buttonFunction = buttonFunction;
        }
        this.iconHolder = new Sprite();
        this.playIcon = new Sprite();
        this.pauseIcon = new Sprite();
        this.paused = false;   //wake up in playing state
        this.drawIcons();
        iconHolder.addChild( playIcon );
        iconHolder.addChild( pauseIcon );
        this.addChild( iconHolder );
        this._paused = false;
        this.pushedIn = false;
        this.activateButton();
    }

    private function drawIcons():void{
        var gHitArea: Graphics = iconHolder.graphics;
        var gPlay: Graphics = playIcon.graphics;
        var gPause: Graphics = pauseIcon.graphics;
        var bH: Number = 30;    //width of button
        var bW: Number = 30;    //height of button
        //large invisible hit area
        with( gHitArea ){
            var W: Number = 60;
            var H: Number = 40;
            clear();
            lineStyle( 2.5, 0xffffff, 0 );
            beginFill( 0xffffff, 0 );
            drawRect( -W / 2, -H / 2, W, H )
            endFill();
        }
        //play iconHolder
        var f: Number = 0.3;    //scale factor
        with( gPlay ){
            //body
            clear();
            lineStyle( 2.5, 0xbbbbbb, 1, true, LineScaleMode.NONE );
            beginFill( 0xdddddd );
            drawRoundRect( -bW / 2, -bH / 2, bW, bH, bH / 2 )
            endFill();
            //triangle
            lineStyle( 1, 0x000000, 1, true, LineScaleMode.NONE )
            beginFill( 0x666666 );
            moveTo( f * bW, 0 );
            lineTo( -f * bW, f * bH );
            lineTo( -f * bW, -f * bH );
            lineTo( f * bW, 0 );
            endFill();
        }

        //pause iconHolder
        var barW: Number = 0.15 * bW;
        var barH: Number = 0.6 * bH;
        with( gPause ){
            //body
            clear();
            lineStyle( 2.5, 0xbbbbbb, 1, true, LineScaleMode.NONE );
            beginFill( 0xdddddd );
            drawRoundRect( -bW / 2, -bH / 2, bW, bH, bH / 2 )
            endFill();
            //double vertical bars
            lineStyle( 1, 0x000000, 1, true, LineScaleMode.NONE )
            beginFill( 0x666666 );
            drawRect( -1.4 * barW, -0.5 * barH, barW, barH );
            drawRect( 0.4 * barW, -0.5 * barH, barW, barH );
        }
    }//end drawIcons()
    

    private function activateButton(): void {
        //trace("this.iconHolder = " , this.iconHolder);
        //this.iconHolder.background.width = this.myButtonWidth;
        //this.iconHolder.background.height = 30;
        //this.iconHolder.label_txt.mouseEnabled = false;
        this.iconHolder.buttonMode = true;
        this.iconHolder.mouseChildren = false;
        this.iconHolder.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.iconHolder.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.iconHolder.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.iconHolder.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseDown" ) {
                if(!localRef.pushedIn ){
                    localRef.pushedIn = true;
                    localRef.iconHolder.x += 2;
                    localRef.iconHolder.y += 2;
                    localRef.buttonFunction();
                }
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
//                localRef.tFormat.bold = true;
//                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);
                if(localRef.pushedIn ){
                    localRef.pushedIn = false;
                    localRef.iconHolder.x -= 2;
                    localRef.iconHolder.y -= 2;
                    //localRef.buttonFunction();
                }

            } else if ( evt.type == "mouseOut" ) {
                if(localRef.pushedIn ){
                    localRef.pushedIn = false;
                    localRef.iconHolder.x -= 2;
                    localRef.iconHolder.y -= 2;
                    //localRef.buttonFunction();
                }
//                localRef.tFormat.bold = false;
//                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            }
        }//end of buttonBehave()

    }//end of activateButton()

    //Setters and getters
    public function get paused(): Boolean {
        return _paused;
    }

    public function set paused( value: Boolean ): void {
        _paused = value;
        if( _paused ){
            playIcon.visible = true;
            pauseIcon.visible = false;
        } else {
            playIcon.visible = false;
            pauseIcon.visible = true;
        }

    }
}
}
