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
import edu.colorado.phet.normalmodes.util.Util;

import flash.display.Graphics;
import flash.display.PixelSnapping;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

import mx.utils.object_proxy;
//button which displays amplitude of x,y 2D mode by filling with color
//amount of color fill indicates amplitude of mode
public class ModeButton extends Sprite{
    private var myModel2; Model2;
    private var rIndex:int;                //r-Index = x mode number
    private var sIndex:int;                //s-Index = y mode number
    //private var polarization:String;       //polarization = "vertical" or "horizontal" indicates which polarization the button displays
    private var colorLayer:Sprite;         //bottom layer of sprite is a solid color
    private var colorLayerMask:Sprite;     //mask for colorLayer.  mask is shape of rounded rect button
    private var nMax;                      //number of different color fill heights
    private var trimAndLabelLayer:Sprite;  //next layer has trim and label
    private var sizeInPix:Number;          //width of button body in pix
    private var lineWidth:Number;          //width of button body border in pix
    private var buttonColor:Number;
    private var fillColor:Number;
    private var fillColorV:Number;        //color fill for vertical mode
    private var fillColorH:Number;        //color fill for horizontal mode
    private var emptyColor:uint;
    private var _borderColor:uint;
    private var myColorTransform:ColorTransform;   //to change color of colorLayer
    private var modeXColor:Number;                 //color corresponding to x-polarization mode
    private var modeYColor:Number;                 //color corresponding to y-polarization mode
    private var label_txt; TextField;
    private var tFormat: TextFormat;
    private var _activatedH:Boolean;         //state of button in horizontal polarization Mode: true if button pressed once, false is pressed again
    private var _activatedV:Boolean;         //state of button in vertical polarization Mode: true = on; false = off
    private var _pushedIn:Boolean;           //true if button pushed in by mouseDown

    public function ModeButton( myModel2:Model2, iIndx:int, jIndx:int, sizeInPix:Number) {
        this.myModel2 = myModel2;
        this.rIndex = iIndx;
        this.sIndex = jIndx;
        this.sizeInPix = sizeInPix;
        //this.buttonColor = 0xffffff ;      //default color
        this.fillColor;// = 0x00ff00;
        this.fillColorV = 0x009900;          //green for vertical
        this.fillColorH = 0xff5500;          //orange for horizontal
        this.emptyColor = 0xffffff;
        this._borderColor = 0x0000ff;
        myColorTransform = new ColorTransform();
        this.nMax = 16
        //this.colorLayer_arr = new Array( nMax );
        this._activatedH = false;      //true if button activated for horizontal polarization mode
        this._activatedV = false;      //true if button activated for vertical polarization mode
        this._pushedIn = false;
        this.colorLayer = new Sprite();
        this.colorLayerMask = new Sprite();
        this.trimAndLabelLayer = new Sprite();
        this.label_txt = new TextField();
        this.tFormat = new TextFormat();
        //this.createColorLayerArray();
        this.drawEmptyButton();

        this.makeLabel();
        this.activateButton();
        this.addChild( this.colorLayer );
        this.addChild( this.colorLayerMask );
        this.addChild( this.trimAndLabelLayer );
        this.trimAndLabelLayer.addChild(this.label_txt);
    }//end constructor


    public function drawEmptyButton( ):void{
        var w:int = this.sizeInPix;       //width and height of button in pixels
        var h:int = this.sizeInPix;
        //although lineWidth is a Number, lineStyle only accepts integer values of lineWidth
        this.lineWidth = Math.max( 2, this.sizeInPix/25 );
        var gT:Graphics = this.trimAndLabelLayer.graphics;
        gT.clear();
        gT.lineStyle( this.lineWidth, this._borderColor, 1 );
        gT.drawRoundRect( 0, 0, w,  h,  w/2 );

        var gM:Graphics = this.colorLayerMask.graphics;
        gM.clear();
        gM.beginFill( 0xff0000 );
        gM.drawRoundRect( 0, 0, w,  h,  w/2 );

        this.colorLayer.mask = this.colorLayerMask;
        //this.colorLayer = this.colorLayer_arr[0];
        /*
        var gC:Graphics = this.colorLayer.graphics;
        gC.clear();
        gC.beginFill( emptyColor );
        gC.drawRoundRect( 0, 0, w,  h,  w/2 );
        gC.endFill();
        */
        var nbrButtonsInRow = this.myModel2.N;
        var textSize:Number;
        if(nbrButtonsInRow == 10){
           textSize = 10;
        } else if( nbrButtonsInRow < 10 && nbrButtonsInRow >= 7 ){
           textSize = 14;
        } else if( nbrButtonsInRow < 7 && nbrButtonsInRow >= 3) {
           textSize = 18;
        } else {
            textSize = 20;
        }
        this.sizeText( textSize );
        this.positionLabel();
    }


    public function set pushedIn( tOrF:Boolean ):void{
        this._pushedIn = tOrF;
    }

    public function set activatedH( tOrF:Boolean ):void{
        this._activatedH = tOrF;
    }

    public function set activatedV( tOrF:Boolean ):void{
        this._activatedV = tOrF;
    }
    
    public function set borderColor( bColor:uint ):void{
        this._borderColor = bColor;
        //redraw button trim
        var w:int = this.sizeInPix;       //width and height of button in pixels
        var h:int = this.sizeInPix;
        var gT:Graphics = this.trimAndLabelLayer.graphics;
        gT.clear();
        gT.lineStyle( this.lineWidth, this._borderColor, 1 );
        gT.drawRoundRect( 0, 0, w,  h,  w/2 );
    }

    //used to temporarily increase border thickness when mouse hovers over button
    private function setBorderThickness( borderThickness:Number ):void{
        var gT:Graphics = this.trimAndLabelLayer.graphics;
        var w:int = this.sizeInPix;       //width and height of button in pixels
        var h:int = this.sizeInPix;
        gT.clear();
        gT.lineStyle( borderThickness, this._borderColor, 1 );
        //gT.beginFill( this.buttonColor, 1);
        gT.drawRoundRect( 0, 0, w,  h,  w/2 );
        //gT.endFill();
        this.positionLabel();
    }//setBorderThickness()

    //unused
    public function changeColor( inputColor:uint ):void{
        this.myColorTransform.color = inputColor;
        //this.colorLayer.transform.colorTransform = this.myColorTransform;
    }

    //draw colorLayer to height(for xModes) or width(yModes) indicating amplitude of mode
    public function changeBackgroundHeight( inputHeight:int):void{
        //this.colorLayer = this.colorLayer_arr[ inputHeight ];
        if(this.myModel2.xModes){
            this.fillColor = this.fillColorH;
        } else{
            this.fillColor = this.fillColorV;
        }
        var w:int = this.sizeInPix;
        var hMax:int = this.sizeInPix;
        var h:Number = inputHeight*hMax/this.nMax;
        var gC:Graphics = this.colorLayer.graphics;
        gC.clear();
        if(this.myModel2.xModes){  //if horizontal polarization, fill from left to right
            gC.beginFill( this.fillColor );
            gC.drawRect( 0, 0, h, w);   //x, y, width, height
            gC.endFill();
            gC.beginFill( this.emptyColor );
            gC.drawRect( h, 0, hMax - h, w )
            gC.endFill();
        }else{  //if vertical polarization, fill from bottom to top
            gC.beginFill( this.fillColor );
            gC.drawRect( 0, hMax - h, w, h);
            gC.endFill();
            gC.beginFill( this.emptyColor );
            gC.drawRect( 0, 0, w, hMax - h)
            gC.endFill();
        }

        //trace("ModeButton.changeBackgroundColor. color = " + this.fillColor.toString(16));
    }

    private function makeLabel():void{
        var label_str:String = rIndex.toString() + "," + sIndex.toString();
        this.label_txt.text = label_str;
        this.tFormat.font = "Arial";
        this.tFormat.size = 12;
        this.tFormat.color = 0x000000;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        //this.label_txt.border = true;    //for testing only
        this.label_txt.setTextFormat( this.tFormat);
    }
    
    private function sizeText( size:Number ):void{
        this.tFormat.size = size;
        this.label_txt.setTextFormat( this.tFormat);
    }

    //for testing only
    public function setLabel( input_str:String ):void{
        this.label_txt.text = input_str;
        this.label_txt.setTextFormat( this.tFormat);
    }

    private function positionLabel():void{
        this.label_txt.x = this.sizeInPix/2 - this.label_txt.width/2;
        this.label_txt.y = this.sizeInPix/2 - this.label_txt.height/2;
    }

    public function setSize( sizeInPix: Number):void{
        this.sizeInPix = sizeInPix;
        this.drawEmptyButton( );
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
            var springLength:Number = 1/(localRef.myModel2.N + 1);
            var largeAmplitude = 0.3*springLength;
            if ( evt.type == "mouseDown" ) {
                if( !localRef._pushedIn ){
                    localRef.x += 2;
                    localRef.y += 2;
                    localRef._pushedIn = true;
                }

                if( localRef.myModel2.xModes ){
                    if( !localRef._activatedH ){
                        localRef.myModel2.setModeAmpli( "x", localRef.rIndex, localRef.sIndex, largeAmplitude  );
                        localRef._activatedH = true;
                    }else{
                        localRef.myModel2.setModeAmpli( "x", localRef.rIndex, localRef.sIndex, 0  );
                        localRef._activatedH = false;
                    }
                }else{
                    if( !localRef._activatedV ){
                        localRef.myModel2.setModeAmpli( "y", localRef.rIndex, localRef.sIndex, largeAmplitude  );
                        localRef._activatedV = true;
                    }else{
                        localRef.myModel2.setModeAmpli( "y", localRef.rIndex, localRef.sIndex, 0  );
                        localRef._activatedV = false;
                    }
                }
                /*
                if(!localRef._activated){
                    localRef._activated = true;
                    if( localRef.myModel2.xModes ){
                       localRef.myModel2.setModeAmpli( "x", localRef.rIndex, localRef.sIndex, largeAmplitude  );
                    }else{
                       localRef.myModel2.setModeAmpli( "y", localRef.rIndex, localRef.sIndex, largeAmplitude  );
                    }

                    //localRef.changeColor( 0x00ff00 );
                }else if(localRef._activated){
                    localRef._activated = false;
                    if( localRef.myModel2.xModes ){
                       localRef.myModel2.setModeAmpli( "x", localRef.rIndex, localRef.sIndex, 0  );
                    }else{
                       localRef.myModel2.setModeAmpli( "y", localRef.rIndex, localRef.sIndex, 0  );
                    }
                    //localRef.myModel2.setModeAmpli( localRef.rIndex, localRef.sIndex, 0  );
                    //localRef.changeColor( 0xffffff );
                }  */

                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {

                localRef.setBorderThickness( localRef.lineWidth + 1.5 );
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
                if( localRef.myModel2.xModes ) {
                    if(!localRef._activatedH) {
                        localRef.changeColor( 0xffffff );//drawButton( 0xffffff );
                    }
                } else{
                    if(!localRef._activatedV) {
                        localRef.changeColor( 0xffffff );//drawButton( 0xffffff );
                    }
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

                localRef.setBorderThickness( localRef.lineWidth );
            }
        }//end of buttonBehave
    }//end of activateButton

}//end class
}//end package
