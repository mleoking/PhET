package edu.colorado.phet.resonance {
//import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.display.*;
import flash.events.*;
import flash.geom.Matrix;
import flash.geom.Rectangle;
import flash.text.*;

public class NiceButton2 extends Sprite {
    private var buttonBody: Sprite;
    private var bodyColor:Number;
    private var label_txt: TextField;
    private var myButtonWidth: Number;
    private var myButtonHeight: Number;
    private var nineSliceGrid:Rectangle;
    private var tFormat: TextFormat;
    private var buttonFunction: Function;

    public function NiceButton2( myButtonWidth: Number, myButtonHeight: Number, labelText: String, buttonFunction: Function, bodyColor:Number = 0x00ff00 ) {
        //this.buttonBody = buttonBody;
        this.buttonBody = new Sprite();
        this.label_txt = new TextField();
        this.label_txt.text = labelText;
        this.label_txt.selectable = false;
        //this.label_txt.border = true;  //for testing only
        this.addChild( this.buttonBody );
        this.myButtonWidth = myButtonWidth;
        this.myButtonHeight = myButtonHeight;
        this.buttonFunction = buttonFunction;
        this.bodyColor = bodyColor;  //default body color is green
        this.tFormat = new TextFormat();
        this.setTFormat();

        this.drawButtonBody();
        this.buttonBody.addChild( this.label_txt );
        this.activateButton();
        //this.buttonFunction = testFunction;
    }//end of constructor

    public function testFunction(): void {
        trace( "NiceButton2.testFunction called." );
    }

    //can we modify this function so that buttonWidth resizes to account for label text length?
    public function setLabel( label: String ): void {
        this.label_txt.text = label;
        this.setTFormat();  //must reformat when text is altered
        //resize width of button body so that text fits, then redraw buttonBody
        this.myButtonWidth =   this.label_txt.width + 20;
        trace("ControlPanel.setLabel buttonWidth = "+this.myButtonWidth );
        this.drawButtonBody();
        //TextFieldUtils.resizeText( this.buttonBody.label_txt, TextFieldAutoSize.CENTER);
    }

    public function setBodyColor( color:Number ):void{
        this.bodyColor = color;
        this.drawButtonBody();
    }

//        public function changeLabel(label:String):void{
//            buttonBody.label_txt.text = label;
//        }

    public function getLabel(): String {
        return this.label_txt.text;
    }

    public function setTFormat(): void {
        this.tFormat.align = TextFormatAlign.CENTER;
        this.tFormat.font = "Arial";
        this.tFormat.size = 15;
        this.label_txt.setTextFormat( this.tFormat );
    }

    private function drawButtonBody(): void {
        var gradMatrix = new Matrix();   //for creating shading on border
        var g: Graphics = this.buttonBody.graphics;
        var bW: Number = this.myButtonWidth;
        var bH: Number = this.myButtonHeight;
        gradMatrix.createGradientBox( 1.0 * bW, 1.0 * bH, Math.atan2(bH,bW)-Math.PI/2, -bW/2, -bH/2 );  //-Math.atan2(bH, bW)
        g.clear();
        g.lineStyle( 3, 0x0000ff, 1, true );
        g.lineGradientStyle( GradientType.LINEAR, [0x000077, 0xaaaaff], [1,1], [40,215], gradMatrix );
        g.beginFill( this.bodyColor );
        var inset:Number = bH/4;
        g.drawRoundRect( -bW / 2, -bH / 2, bW, bH, 2*inset );
        g.endFill();
        this.nineSliceGrid = new Rectangle(-0.5*bW + inset, - 0.5*bH + inset, bW - 2*inset, bH - 2*inset);
        this.label_txt.x = -bW / 2;
        this.label_txt.y = -0.4 * bH;
        this.label_txt.width = bW;
        this.label_txt.height = 0.8 * bH;
    }//end drawButtonBody()

    private function activateButton(): void {
        //trace("this.buttonBody = " , this.buttonBody);
        //this.buttonBody.background.width = this.myButtonWidth;
        //this.buttonBody.background.height = 30;
        //this.buttonBody.label_txt.mouseEnabled = false;
        this.buttonBody.buttonMode = true;
        this.buttonBody.mouseChildren = false;
        this.buttonBody.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.buttonBody.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.buttonBody.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.buttonBody.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseDown" ) {
                localRef.buttonBody.x += 2;
                localRef.buttonBody.y += 2;
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
                localRef.tFormat.bold = true;
                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);
                localRef.buttonBody.x -= 2;
                localRef.buttonBody.y -= 2;
                localRef.buttonFunction();
            } else if ( evt.type == "mouseOut" ) {
                localRef.tFormat.bold = false;
                localRef.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            }
        }//end of buttonBehave

    }//end of makeButton

}//end of class
}//end of package