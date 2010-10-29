package edu.colorado.phet.chargesandfields {

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.Sprite;
import flash.geom.Rectangle;
import flash.utils.ByteArray;

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
    var positiveByteArray: ByteArray;
    var negativeByteArray: ByteArray;

    public function VoltageMosaic( model: Model, w: Number, h: Number ) {
        this.model = model;
        myWidth = w;
        myHeight = h;

        addChild( bitmap );

        initializeColorArrays();
        draw();
    }

    private function initializeColorArrays(): void {
        positiveByteArray = new ByteArray();
        negativeByteArray = new ByteArray();
        // (1 - ratio) * 255 == c
        for ( var c: int = 0; c < 256; c++ ) {
            var red: Number;
            var green: Number;
            var blue: Number;

            red = 255;
            green = blue = c;
            var color: uint = (red << 16) | (green << 8) | blue;

            positiveByteArray.position = c * 4 * step * step;
            for ( var i: int = 0; i < step; i++ ) {
                for ( var k: int = 0; k < step; k++ ) {
                    positiveByteArray.writeInt( color );
                }
            }
        }

        negativeByteArray.position = 0;
        // (1 + ratio) * 255 == c
        for ( c = 0; c < 256; c++ ) {
            blue = 255;
            green = red = c;
            color = (red << 16) | (green << 8) | blue;

            positiveByteArray.position = c * 4 * step * step;
            for ( i = 0; i < step; i++ ) {
                for ( k = 0; k < step; k++ ) {
                    negativeByteArray.writeInt( color );
                }
            }
        }
    }

    public function changeSize( w: Number, h: Number ): void {
        myWidth = w;
        myHeight = h;
        draw();
    }

    public function draw(): void {
        this.graphics.clear();

        //var step : Number = 10;

        var halfstep: Number = step / 2;


        var time: Number = (new Date()).valueOf();
        for ( var ox: int = 0; ox < myWidth; ox += step ) {
            for ( var oy: int = 0; oy < myHeight; oy += step ) {

                //                trace( "my array.length = " + byteArray.bytesAvailable );
                // 30ms for getV total
                var ratio: Number = model.getVRatio( ox + halfstep, oy + halfstep );

                var red: Number;
                var green: Number;
                var blue: Number;

                if ( ratio > 0 ) {
                    var idx: Number = Math.floor( ((1 - ratio) * 255) );
                    if ( idx < 0 ) {
                        idx = 0;
                    }
                    positiveByteArray.position = idx * 4 * step * step;
                    bitmapData.setPixels( new Rectangle( ox, oy, step, step ), positiveByteArray );
                }
                else {
                    idx = Math.floor( ((1 + ratio) * 255) );
                    if ( idx < 0 ) {
                        idx = 0;
                    }
                    negativeByteArray.position = idx * 4 * step * step;
                    bitmapData.setPixels( new Rectangle( ox, oy, step, step ), negativeByteArray );
                }
                //                var color: uint = (red << 16) | (green << 8) | blue;
                //                var color: int = 0;
                //                var color: int = 0;

                //                                bitmapData.draw()
                //                                for ( var i: int = 0; i < step; i++ ) {
                //                                    for ( var k: int = 0; k < step; k++ ) {
                //                                        bitmapData.setPixel( ox + i, oy + k, color );
                //                                    }
                //                                }
                //                byteArray.position = 0;
                //                bitmapData.setPixels( new Rectangle( ox, oy, step, step ), byteArray );

                // 10 ms for graphics total
                //                this.graphics.beginFill( color );
                //                this.graphics.drawRect( ox, oy, ox + step, oy + step );
                //                this.graphics.endFill();
            }
        }
        trace( bitmapData.getPixels( new Rectangle( 0, 0, 3, 3 ) ).length );

        fps = String( int( 1000 / ((new Date()).valueOf() - time) ) );
    }

    public function changeStepSize( event: SliderEvent ): void {
        step = event.value;
        initializeColorArrays();
        draw();
    }
}
}