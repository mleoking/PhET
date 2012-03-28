/**
 * General purpose label to be used in control panels, etc.  in flex framework
 * Created by Michael Dubson
 * Date: 5/22/11
 * Time: 2:12 PM
 */
package edu.colorado.phet.normalmodes.NiceComponents {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.KeyboardEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFieldType;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

public class NiceLabel extends Sprite {
    public var label_txt: TextField;
    private var tFormat: TextFormat;
    private var fontSize: int;
    private var fontColor:Number;
    private var action:Function;    //action to be performed if label is editable

    public function NiceLabel( fontSize:int = 15, labelText_str:String = "Label") {
        this.fontSize = fontSize;
        this.fontColor = 0x000000;       //default color is black
        this.label_txt = new TextField();
        this.label_txt.text = labelText_str;
        this.tFormat = new TextFormat();
        this.setDefaultTextFormat();
        this.setLabel();
        this.addChild(this.label_txt)
    } //end of constructor


    private function setDefaultTextFormat(): void {
        this.tFormat.align = TextFormatAlign.LEFT;
        this.tFormat.font = "Arial";
        this.tFormat.color = this.fontColor;
        this.tFormat.size = this.fontSize;
    }

    private function setLabel(): void {
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.LEFT;
        this.label_txt.setTextFormat( this.tFormat );
        this.label_txt.y = 0;
        //this.label_txt.border = true;      //for testing only
    }//end setLabel()

    public function setBold( tOrF:Boolean ):void{
        this.tFormat.bold = tOrF;
        this.label_txt.setTextFormat( this.tFormat );
    }

    public function makeEditable( allowedInput:String = "0-9.\\-" ):void{
        this.action = action;
        this.label_txt.restrict = allowedInput;
        this.label_txt.type = TextFieldType.INPUT;
        this.label_txt.selectable = true;
        this.label_txt.border = true;
        this.label_txt.background = true;
        this.label_txt.backgroundColor = 0xffffff ;
    }

    public function makeTextMultiline( maxWidthOfText:Number ):void{
        this.label_txt.wordWrap = true;
        this.label_txt.width = maxWidthOfText;
    }


    public function setTextFormat (tFormat:TextFormat):void{
        this.tFormat = tFormat;
        this.label_txt.setTextFormat( this.tFormat );
    }

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
    //This does not seem to work.  I tried it.
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
