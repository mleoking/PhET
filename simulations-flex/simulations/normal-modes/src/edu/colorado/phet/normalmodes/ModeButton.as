/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/14/11
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import flash.display.Graphics;
import flash.display.PixelSnapping;
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFormat;

import mx.utils.object_proxy;

public class ModeButton extends Sprite{
    private var myModel2; Model2;
    private var iIndex:int;
    private var jIndex:int;
    private var sizeInPix:Number;
    private var label_txt; TextField;
    private var tFormat: TextFormat;

    public function ModeButton( myModel2:Model2, iIndx:int, jIndx:int, sizeInPix:Number) {
        this.myModel2 = myModel2;
        this.iIndex = iIndx;
        this.jIndex = jIndx;
        this.label_txt = new TextField();
        this.addChild(this.label_txt);
        this.tFormat = new TextFormat();
        this.drawButton();
        this.makeLabel();

    }//end constructor

    private function drawButton():void{
        var g:Graphics = this.graphics;
        var w:int = this.sizeInPix;       //width and height of button in pixels
        var h:int = this.sizeInPix;
        g.clear();
        g.lineStyle( 3, 0x0000ff, 1 );
        g.beginFill( 0x00ff00, 1);
        g.drawRoundRect( 0, 0, w,  h,  w/2 );
        g.endFill();
    }

    private function makeLabel():void{
        var label_str:String = iIndex.toString() + "," + jIndex.toString();
        this.label_txt.text = label_str;
        this.tFormat.font = "Arial";
        this.tFormat.size = 12;
        this.label_txt.setTextFormat( this.tFormat);
    }

    public function setSize(sizeInPix:Number):void{
        this.sizeInPix = sizeInPix;
        this.drawButton());
    }


}//end class
}//end package
