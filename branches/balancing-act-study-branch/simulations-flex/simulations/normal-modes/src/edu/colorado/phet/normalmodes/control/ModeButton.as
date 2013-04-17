/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 6/14/11
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.model.Model2D;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

/**
 * Button which displays amplitude of (x,y) 2D mode. 
 * Amount of color fill indicates amplitude of mode.
 * Different colors and different directions of fill for the two polarization modes (H or V).
 */
public class ModeButton extends Sprite {
    private var myMddel2D: Model2D;
    private var rIndex: int;                //r-Index = x mode number  (standard notation in textbooks)
    private var sIndex: int;                //s-Index = y mode number
    private var colorLayer: Sprite;         //bottom layer of sprite is a solid color
    private var colorLayerMask: Sprite;     //mask for colorLayer.  mask is shape of rounded rect button
    private var nMax: int;                  //number of different color fill heights
    private var trimAndLabelLayer: Sprite;  //next layer has trim and label
    private var sizeInPix: Number;          //width of button body in pix
    private var lineWidth: Number;          //width of button body border in pix
    private var fillColor: Number;          //current fill color
    private var fillColorV: Number;         //color fill for vertical mode
    private var fillColorH: Number;         //color fill for horizontal mode
    private var emptyColor: uint;            //color of no-fill
    private var _borderColor: uint;
    private var myColorTransform: ColorTransform;   //needed to change color of colorLayer
    private var label_txt: TextField;
    private var tFormat: TextFormat;
    private var _activatedH: Boolean;         //state of button in horizontal polarization mode: true = on; false = off, button toggles
    private var _activatedV: Boolean;         //state of button in vertical polarization mode: true = on; false = off
    private var _pushedIn: Boolean;           //true if button pushed in by mouseDown

    public function ModeButton( myMddel2D: Model2D, iIndx: int, jIndx: int, sizeInPix: Number ) {
        this.myMddel2D = myMddel2D;
        this.rIndex = iIndx;
        this.sIndex = jIndx;
        this.sizeInPix = sizeInPix;
        this.fillColorV = 0x009900;          //green for vertical
        this.fillColorH = 0xff5500;          //orange for horizontal
        this.emptyColor = 0xffffff;          //white for backgound
        this._borderColor = 0x0000ff;        //blue border
        myColorTransform = new ColorTransform();
        this.nMax = 16;                      //16 different amplitude heights
        this._activatedH = false;      //true if button controls horizontal polarization mode
        this._activatedV = false;      //true if button controls vertical polarization mode
        this._pushedIn = false;
        this.colorLayer = new Sprite();
        this.colorLayerMask = new Sprite();
        this.trimAndLabelLayer = new Sprite();
        this.label_txt = new TextField();
        this.tFormat = new TextFormat();
        this.drawEmptyButton();

        this.makeLabel();
        this.activateButton();
        this.addChild( this.colorLayer );
        this.addChild( this.colorLayerMask );
        this.addChild( this.trimAndLabelLayer );
        this.trimAndLabelLayer.addChild( this.label_txt );
    }//end constructor


    public function drawEmptyButton(): void {
        var w: int = this.sizeInPix;       //width and height of button in pixels
        var h: int = this.sizeInPix;
        //although lineWidth is a Number, lineStyle only accepts integer values of lineWidth
        this.lineWidth = Math.max( 2, this.sizeInPix / 25 );
        var gT: Graphics = this.trimAndLabelLayer.graphics;
        gT.clear();
        gT.lineStyle( this.lineWidth, this._borderColor, 1 );
        gT.drawRoundRect( 0, 0, w, h, w / 2 );

        var gM: Graphics = this.colorLayerMask.graphics;
        gM.clear();
        gM.beginFill( 0xff0000 );
        gM.drawRoundRect( 0, 0, w, h, w / 2 );

        this.colorLayer.mask = this.colorLayerMask;

        var nbrOfButtonsInRow: int = this.myMddel2D.N;
        //Set label text size according to size of button
        var textSize: Number;
        if ( nbrOfButtonsInRow == 10 ) {
            textSize = 10;
        }
        else if ( nbrOfButtonsInRow < 10 && nbrOfButtonsInRow >= 7 ) {
            textSize = 14;
        }
        else if ( nbrOfButtonsInRow < 7 && nbrOfButtonsInRow >= 3 ) {
            textSize = 18;
        }
        else {
            textSize = 20;
        }
        this.sizeText( textSize );
        this.positionLabel();
    }


    public function set pushedIn( tOrF: Boolean ): void {
        this._pushedIn = tOrF;
    }

    public function set activatedH( tOrF: Boolean ): void {
        this._activatedH = tOrF;
    }

    public function set activatedV( tOrF: Boolean ): void {
        this._activatedV = tOrF;
    }

    public function set borderColor( bColor: uint ): void {
        this._borderColor = bColor;
        //redraw button trim
        var w: int = this.sizeInPix;       //width and height of button in pixels
        var h: int = this.sizeInPix;
        var gT: Graphics = this.trimAndLabelLayer.graphics;
        gT.clear();
        gT.lineStyle( this.lineWidth, this._borderColor, 1 );
        gT.drawRoundRect( 0, 0, w, h, w / 2 );
    }

    /*Used to temporarily increase border thickness when mouse hovers over button.*/
    private function setBorderThickness( borderThickness: Number ): void {
        var gT: Graphics = this.trimAndLabelLayer.graphics;
        var w: int = this.sizeInPix;       //width and height of button in pixels
        var h: int = this.sizeInPix;
        gT.clear();
        gT.lineStyle( borderThickness, this._borderColor, 1 );
        gT.drawRoundRect( 0, 0, w, h, w / 2 );
        this.positionLabel();
    }//setBorderThickness()

    //unused
    public function changeColor( inputColor: uint ): void {
        this.myColorTransform.color = inputColor;
    }

    /*Draw colorLayer to height(for xModes) or width(yModes) indicating amplitude of mode*/
    public function changeBackgroundHeight( inputHeight: int ): void {
        if ( this.myMddel2D.xModes ) {
            this.fillColor = this.fillColorH;
        }
        else {
            this.fillColor = this.fillColorV;
        }
        var w: int = this.sizeInPix;
        var hMax: int = this.sizeInPix;
        var h: Number = inputHeight * hMax / this.nMax;
        var gC: Graphics = this.colorLayer.graphics;
        gC.clear();
        if ( this.myMddel2D.xModes ) {  //if horizontal polarization, fill from left to right
            gC.beginFill( this.fillColor );
            gC.drawRect( 0, 0, h, w );   //x, y, width, height
            gC.endFill();
            gC.beginFill( this.emptyColor );
            gC.drawRect( h, 0, hMax - h, w )
            gC.endFill();
        }
        else {  //if vertical polarization, fill from bottom to top
            gC.beginFill( this.fillColor );
            gC.drawRect( 0, hMax - h, w, h );
            gC.endFill();
            gC.beginFill( this.emptyColor );
            gC.drawRect( 0, 0, w, hMax - h )
            gC.endFill();
        }
    }

    private function makeLabel(): void {
        var label_str: String = rIndex.toString() + "," + sIndex.toString();
        this.label_txt.text = label_str;
        this.tFormat.font = "Arial";
        this.tFormat.size = 12;
        this.tFormat.color = 0x000000;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        //this.label_txt.border = true;    //for testing only
        this.label_txt.setTextFormat( this.tFormat );
    }

    private function sizeText( size: Number ): void {
        this.tFormat.size = size;
        this.label_txt.setTextFormat( this.tFormat );
    }

    //for testing only
    public function setLabel( input_str: String ): void {
        this.label_txt.text = input_str;
        this.label_txt.setTextFormat( this.tFormat );
    }

    private function positionLabel(): void {
        this.label_txt.x = this.sizeInPix / 2 - this.label_txt.width / 2;
        this.label_txt.y = this.sizeInPix / 2 - this.label_txt.height / 2;
    }

    public function setSize( sizeInPix: Number ): void {
        this.sizeInPix = sizeInPix;
        this.drawEmptyButton();
    }

    private function activateButton(): void {
        this.buttonMode = true;
        this.mouseChildren = false;
        this.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {
            //When button pushed, amplitude of mode set to "largeAmplitude" = 0.3*equilibrium spring length
            var springLength: Number = 1 / (localRef.myMddel2D.N + 1);   // ~ distance between masses = equilibrium spring length
            var largeAmplitude: Number = 0.3 * springLength;
            if ( evt.type == "mouseDown" ) {
                if ( !localRef._pushedIn ) {
                    localRef.x += 2;
                    localRef.y += 2;
                    localRef._pushedIn = true;
                }

                if ( localRef.myMddel2D.xModes ) {
                    if ( !localRef._activatedH ) {
                        localRef.myMddel2D.setModeAmpli( "x", localRef.rIndex, localRef.sIndex, largeAmplitude );
                        localRef._activatedH = true;
                    }
                    else {
                        localRef.myMddel2D.setModeAmpli( "x", localRef.rIndex, localRef.sIndex, 0 );
                        localRef._activatedH = false;
                    }
                }
                else {
                    if ( !localRef._activatedV ) {
                        localRef.myMddel2D.setModeAmpli( "y", localRef.rIndex, localRef.sIndex, largeAmplitude );
                        localRef._activatedV = true;
                    }
                    else {
                        localRef.myMddel2D.setModeAmpli( "y", localRef.rIndex, localRef.sIndex, 0 );
                        localRef._activatedV = false;
                    }
                }
            }
            else if ( evt.type == "mouseOver" ) {
                localRef.setBorderThickness( localRef.lineWidth + 1.5 );
                localRef.tFormat.bold = true;
                localRef.label_txt.setTextFormat( localRef.tFormat );
            }
            else if ( evt.type == "mouseUp" ) {
                if ( localRef._pushedIn ) {
                    localRef.x -= 2;
                    localRef.y -= 2;
                    localRef._pushedIn = false;
                }
                if ( localRef.myMddel2D.xModes ) {
                    if ( !localRef._activatedH ) {
                        localRef.changeColor( 0xffffff );
                    }
                }
                else {
                    if ( !localRef._activatedV ) {
                        localRef.changeColor( 0xffffff );
                    }
                }
            }
            else if ( evt.type == "mouseOut" ) {
                localRef.tFormat.bold = false;
                localRef.label_txt.setTextFormat( localRef.tFormat );
                if ( localRef._pushedIn ) {
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
