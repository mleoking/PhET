/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/14/11
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.model.Model2;

import flash.display.Graphics;
import flash.display.PixelSnapping;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

import mx.utils.object_proxy;

public class ModeButton extends Sprite{
    private var myModel2; Model2;
    private var iIndex:int;
    private var jIndex:int;
    private var sizeInPix:Number;
    private var buttonColor:Number;
    private var label_txt; TextField;
    private var tFormat: TextFormat;
    private var _activated:Boolean;      //true if button pressed once, false is pressed again
    private var _pushedIn:Boolean;         //true if button pushed in by mouseDown

    public function ModeButton( myModel2:Model2, iIndx:int, jIndx:int, sizeInPix:Number) {
        this.myModel2 = myModel2;
        this.iIndex = iIndx;
        this.jIndex = jIndx;
        this.sizeInPix = sizeInPix;
        this.buttonColor = 0xffffff ;
        this._activated = false;
        this._pushedIn = false;
        this.label_txt = new TextField();
        this.addChild(this.label_txt);
        this.tFormat = new TextFormat();
        this.drawButton( this.buttonColor );
        this.makeLabel();
        this.activateButton();

    }//end constructor

    public function drawButton( backgroundColor:Number ):void{
        this.buttonColor = backgroundColor;
        var g:Graphics = this.graphics;
        var w:int = this.sizeInPix;       //width and height of button in pixels
        var h:int = this.sizeInPix;
        g.clear();
        g.lineStyle( 2, 0x0000ff, 1 );
        g.beginFill( backgroundColor, 1);
        g.drawRoundRect( 0, 0, w,  h,  w/2 );
        g.endFill();
        this.positionLabel();
    }

    public function set pushedIn( tOrF:Boolean ):void{
        this._pushedIn = tOrF;
    }

    public function set activated( tOrF:Boolean ):void{
        this._activated = tOrF;
    }

    private function setBorderThickness( borderThickness:Number ):void{
        var g:Graphics = this.graphics;
        var w:int = this.sizeInPix;       //width and height of button in pixels
        var h:int = this.sizeInPix;
        g.clear();
        g.lineStyle( borderThickness, 0x0000ff, 1 );
        g.beginFill( this.buttonColor, 1);
        g.drawRoundRect( 0, 0, w,  h,  w/2 );
        g.endFill();
        this.positionLabel();
    }

    private function makeLabel():void{
        var label_str:String = iIndex.toString() + "," + jIndex.toString();
        this.label_txt.text = label_str;
        this.tFormat.font = "Arial";
        this.tFormat.size = 12;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        //this.label_txt.border = true;    //for testing only
        this.label_txt.setTextFormat( this.tFormat);
    }

    private function positionLabel():void{
        this.label_txt.x = this.sizeInPix/2 - this.label_txt.width/2;
        this.label_txt.y = this.sizeInPix/2 - this.label_txt.height/2;
    }

    public function setSize( sizeInPix: Number):void{
        this.sizeInPix = sizeInPix;
        this.drawButton( 0xffffff );
    }

    private function activateButton(): void {
        //trace("this.buttonBody = " , this.buttonBody);
        //this.buttonBody.background.width = this.myButtonWidth;
        //this.buttonBody.background.height = 30;
        //this.buttonBody.label_txt.mouseEnabled = false;
        this.buttonMode = true;
        this.mouseChildren = false;
        this.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseDown" ) {
                if( !localRef._pushedIn ){
                    localRef.x += 2;
                    localRef.y += 2;
                    localRef._pushedIn = true;
                }

                if(!localRef._activated){
                    localRef._activated = true;
                    //Note (i, j) = (row, column) = (y,x).  Mode r pairs with x=j, mode s pairs with y=i
                    localRef.myModel2.setModeAmpli( localRef.jIndex, localRef.iIndex, 0.03  );
                    localRef.drawButton( 0x00ff00 );
                }else if(localRef._activated){
                    localRef._activated = false;
                    localRef.myModel2.setModeAmpli( localRef.jIndex, localRef.iIndex, 0  );
                    localRef.drawButton( 0xffffff );
                }

                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
//                if(!localRef._activated){
//                    localRef.drawButton( 0xffff00);
//                }
                localRef.setBorderThickness( 3 );
                localRef.tFormat.bold = true;
                localRef.label_txt.setTextFormat( localRef.tFormat );

                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);
                if( localRef._pushedIn ){
                    localRef.x -= 2;
                    localRef.y -= 2;
                    localRef._pushedIn = false;
                }
                if(!localRef._activated) {
                   localRef.drawButton( 0xffffff );
                }
                //localRef.myModel2.;
            } else if ( evt.type == "mouseOut" ) {
                localRef.tFormat.bold = false;
                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
                if( localRef._pushedIn ){
                    localRef.x -= 2;
                    localRef.y -= 2;
                    localRef._pushedIn = false;
                }
                if(!localRef._activated){
                    localRef.drawButton( 0xffffff );
                }
                localRef.setBorderThickness( 2 );
            }
        }//end of buttonBehave
    }//end of activateButton

}//end class
}//end package
