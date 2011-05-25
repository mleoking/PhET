/**
 * General purpose label to be used in control panels, etc.  in flex framework
 * Created by Michael Dubson
 * Date: 5/22/11
 * Time: 2:12 PM
 */
package edu.colorado.phet.resonance {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

public class NiceLabel extends Sprite {
    private var label_txt: TextField;
    private var tFormat: TextFormat;
    private var fontSize: int;
    private var fontColor:Number;

    public function NiceLabel( fontSize:int = 15, labelText_str:String = "Label") {
        this.fontSize = fontSize;
        this.fontColor = 0x000000;       //default color is black
        this.label_txt = new TextField();
        this.label_txt.text = labelText_str;
        this.tFormat = new TextFormat();
        this.setTextFormat();
        this.setLabel();
        this.addChild(this.label_txt)
    } //end of constructor


    public function setTextFormat(): void {
        this.tFormat.align = TextFormatAlign.LEFT;
        this.tFormat.font = "Arial";
        this.tFormat.color = this.fontColor;
        this.tFormat.size = this.fontSize;
    }

    public function setBold( tOrF:Boolean ):void{
        this.tFormat.bold = tOrF;
        this.label_txt.setTextFormat( this.tFormat );
    }

    private function setLabel(): void {
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.LEFT;
        this.label_txt.setTextFormat( this.tFormat );
        this.label_txt.y = 0;
        //this.label_txt.border = true;      //for testing only
    }//end setLabel()

    public function setText(labelText_str:String):void{
        this.label_txt.text = labelText_str;
        this.label_txt.setTextFormat( this.tFormat );
        //following code for testing only
        // var pixWidth:Number = this.label_txt.textWidth;
        //var pixHeight:Number = this.label_txt.textHeight;
        //trace("NiceLabel.label_txt.x = "+this.label_txt.x);
        //this.drawBounds( pixWidth,  pixHeight );
    }//end setText()

    public function setFontSize( fontSize:int ):void{
        this.fontSize = fontSize;
        this.tFormat.size = this.fontSize;
        this.label_txt.setTextFormat( this.tFormat );
    }

    public function setFontColor( fontColor:Number):void{
        this.fontColor = fontColor;
        this.tFormat.color = this.fontColor;
        this.label_txt.setTextFormat( this.tFormat );
    }

    //used to tweek position of label is flex layout
    //This does not work.  I tried it.
    public function setYOffset( yPix:int ):void{
        this.label_txt.y = yPix;
    }

    //for testing purposes only
    private function drawBounds(w:Number,  h:Number):void{
      var g:Graphics = this.graphics;
      g.clear();
      g.lineStyle(1, 0x000000, 0);
      g.beginFill(0xff0000);
      g.drawRect(0, 0, w, h);
      g.endFill();
      //trace("NiceLabel.drawBounds this.width = "+this.width);
    }

}//end of class
} //end of package
