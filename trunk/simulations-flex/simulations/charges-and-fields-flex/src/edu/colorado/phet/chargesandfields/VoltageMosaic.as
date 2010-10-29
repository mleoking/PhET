package edu.colorado.phet.chargesandfields {

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.Sprite;
import flash.geom.Rectangle;

import mx.events.SliderEvent;

public class VoltageMosaic extends Sprite {
    private var myWidth: int;
    private var myHeight: int;

    private var model: Model;

    [Bindable]
    public var fps: String = "FPS";

    [Bindable]
    public var step: int = 10;
    var bitmapData: BitmapData = new BitmapData( 500, 500, false, 0xffffff );
    var bitmap: Bitmap = new Bitmap( bitmapData );

    public function VoltageMosaic( model: Model, w: Number, h: Number ) {
        this.model = model;
        myWidth = w;
        myHeight = h;

        addChild( bitmap );

        draw();
    }

    public function changeSize( w: Number, h: Number ): void {
        myWidth = w;
        myHeight = h;
        removeChild( bitmap );
        bitmapData = new BitmapData( w, h, false, 0xffffff );
        bitmap = new Bitmap( bitmapData );
        addChild( bitmap );
        draw();
    }

    public function draw(): void {
        this.graphics.clear();

        var halfstep: Number = step / 2;

        var red: Number;
        var green: Number;
        var blue: Number;

        var time: Number = (new Date()).valueOf();
        var drawBounds: Rectangle = new Rectangle( 0, 0, step, step );
        for ( var ox: int = 0; ox < myWidth; ox += step ) {
            for ( var oy: int = 0; oy < myHeight; oy += step ) {
                var ratio: Number = model.getVRatio( ox + halfstep, oy + halfstep );

                if ( ratio > 0 ) {
                    var c: Number = Math.floor( ((1 - ratio) * 255) );
                    if ( c < 0 ) {
                        c = 0;
                    }
                    red = 255;
                    green = blue = c;
                }
                else {
                    c = Math.floor( ((1 + ratio) * 255) );
                    if ( c < 0 ) {
                        c = 0;
                    }
                    blue = 255;
                    green = red = c;
                }
                var color: uint = (red << 16) | (green << 8) | blue;
                drawBounds.x = ox;
                drawBounds.y = oy;
                bitmapData.fillRect( drawBounds, color );
            }
        }
        fps = String( int( 1000 / ((new Date()).valueOf() - time) ) );
    }

    public function changeStepSize( event: SliderEvent ): void {
        step = event.value;
        draw();
    }
}
}