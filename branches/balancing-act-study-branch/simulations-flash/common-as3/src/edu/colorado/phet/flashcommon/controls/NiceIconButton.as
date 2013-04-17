/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 7/1/12
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.flashcommon.controls {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

//Like NiceButton except that user-supplied icon acts as button body
public class NiceIconButton extends Sprite {

    private var icon: Sprite;               //icon that is body of button
    private var label_txt: TextField;       //optional label
    private var fontColor: Number;
    private var pushedIn:Boolean;           //true if button pushedIn with mouseDown
    private var tFormat: TextFormat;
    private var buttonFunction: Function;
    
    public function NiceIconButton( icon: Sprite, buttonFunction: Function, labelText: String = "", fontColor:Number = 0x000000) {
        this.icon = icon;
        //this.drawTestIcon();
        this.label_txt = new TextField();
        this.label_txt.text = labelText;
        this.label_txt.selectable = false;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        //this.label_txt.border = true;  //for testing only
        this.addChild( this.icon );
        this.buttonFunction = buttonFunction;
        this.pushedIn = false;
        this.fontColor = fontColor;  //default font color is black
        this.tFormat = new TextFormat();
        this.setTFormat();
        this.addChild( this.label_txt );
        this.activateButton();
    }

    //for testing only
    private function drawTestIcon():void{
        var g:Graphics = icon.graphics;
        g.lineStyle( 4, 0x0000ff,  1);
        g.beginFill( 0xffffff, 1 );
        g.drawCircle( 0, 0, 20 );
        g.endFill()
    }
    public function setFontColor( color:Number ):void{
        this.fontColor = color;
        this.setTFormat();
    }

    public function getLabel(): String {
        return this.label_txt.text;
    }

    public function setTFormat(): void {
        this.tFormat.align = TextFormatAlign.CENTER;
        this.tFormat.font = "Arial";
        this.tFormat.size = 15;
        this.tFormat.color = this.fontColor;
        this.label_txt.setTextFormat( this.tFormat );
        //trace("ControlPanel.setTFormat buttonWidth = "+this.myButtonWidth );
    }

    private function activateButton(): void {
        //trace("this.icon = " , this.icon);
        //this.icon.background.width = this.myButtonWidth;
        //this.icon.background.height = 30;
        //this.icon.label_txt.mouseEnabled = false;
        this.icon.buttonMode = true;
        this.icon.mouseChildren = false;
        this.icon.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.icon.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.icon.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.icon.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseDown" ) {
                if(!localRef.pushedIn ){
                    localRef.pushedIn = true;
                    localRef.icon.x += 2;
                    localRef.icon.y += 2;
                    localRef.buttonFunction();
                }
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
                localRef.tFormat.bold = true;
                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);
                if(localRef.pushedIn ){
                    localRef.pushedIn = false;
                    localRef.icon.x -= 2;
                    localRef.icon.y -= 2;
                    //localRef.buttonFunction();
                }

            } else if ( evt.type == "mouseOut" ) {
                if(localRef.pushedIn ){
                    localRef.pushedIn = false;
                    localRef.icon.x -= 2;
                    localRef.icon.y -= 2;
                    //localRef.buttonFunction();
                }
                localRef.tFormat.bold = false;
                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            }
        }//end of buttonBehave()

    }//end of activateButton()

} //end class
} //end package
