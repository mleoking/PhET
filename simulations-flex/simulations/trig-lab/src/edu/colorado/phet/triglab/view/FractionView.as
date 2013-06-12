/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/12/13
 * Time: 8:06 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.geom.Rectangle;

/*
* View of a simple fraction, displayed vertically with numerator directly
 * above denominator, separated by horizontal line.
* */
public class FractionView extends Sprite {
    private var numerator: NiceLabel = new NiceLabel();     //label showing numerator
    private var denominator: NiceLabel = new NiceLabel();   //label showing denominator
    private var numerator_str: String;                      //string for numerator label
    private var denominator_str: String;
    private var horizLine: Sprite = new Sprite();   //horizontal line between num and denom
    private var horizLineLength: Number = 20;       //length of horiz line in pixels
    private var color: uint = 0x000000;             //color of displayed fraction, default is black


    public function FractionView( num: String, denom: String ) {
        this.numerator_str = num;
        this.denominator_str = denom;
        this.initialize();

    }//end constructor

    private function initialize():void{
        numerator.setText( numerator_str );
        denominator.setText( denominator_str );
        var maxWidth: Number = Math.max ( numerator.width,  denominator.width );
        horizLineLength = maxWidth + 14;
        this.drawHorizLine();
        this.addChild( numerator );
        this.addChild( horizLine );
        this.addChild( denominator );
        this.arrangeFractionParts();
    }

    private function drawHorizLine():void{
        var gHL: Graphics = this.horizLine.graphics;
        with ( gHL ){
            clear();
            lineStyle( 2, color, 1, false, "normal", "none" );   //flat end caps, not rounded
            moveTo( -horizLineLength/2, 0 );
            lineTo( horizLineLength/2, 0 );
        }
    }

    private function arrangeFractionParts():void{

        //registration point is upper left corner of numerator
        numerator.x = 0;
        numerator.y = 0;
        horizLine.x = 0.5*numerator.width;
        horizLine.y = numerator.height + horizLine.height;
        denominator.x = horizLine.x - 0.5*denominator.width;
        denominator.y = numerator.height + 2*horizLine.height;

//        numerator.x = - numerator.width/2;
//        numerator.y = - numerator.height - 3;
//        denominator.x = - denominator.width/2;
//        denominator.y = 3;

    }

    public function setFontSize( size:int ):void{
        numerator.setFontSize( size );
        denominator.setFontSize( size );
        arrangeFractionParts();
    }

    //for testing purposes only
    public function drawBounds(): void {
        var rect: Rectangle = this.getBounds( this );
        trace( "FractionView.drawBounds = " + rect );
        var g: Graphics = this.graphics;
        g.clear();
        g.lineStyle( 1, 0x000000, 0 );
        g.beginFill( 0xff7777 );
        g.drawRect( rect.x,  rect.y,  rect.width, rect.height );
        g.endFill();
//        var w: Number = this.width;
//        var h: Number = this.height;
//        var g: Graphics = this.graphics;
//        g.clear();
//        g.lineStyle( 1, 0x000000, 0 );
//        g.beginFill( 0xff0000 );
//        g.drawRect( 0, 0, w, h );
//        g.endFill();
//        trace( "NiceLabel.drawBounds this.width = " + this.width );
    }
}//end class
}//end package
