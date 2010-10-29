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
    var byteArray: ByteArray = new ByteArray();

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
        draw();
    }

    public function draw(): void {
        this.graphics.clear();

        //var step : Number = 10;

        var halfstep: Number = step / 2;

        byteArray.position = 0;
        for ( var i: int = 0; i < step; i++ ) {
            for ( var k: int = 0; k < step; k++ ) {
                byteArray.writeInt( 0x0000ff );
            }
        }

        var time: Number = (new Date()).valueOf();
        for ( var ox: int = 0; ox < myWidth; ox += step ) {
            for ( var oy: int = 0; oy < myHeight; oy += step ) {

                //                trace( "my array.length = " + byteArray.bytesAvailable );
                // 30ms for getV total
                var color: uint = model.getV( ox + halfstep, oy + halfstep )[1];
                //                var color: int = 0;
                //                var color: int = 0;

                //                bitmapData.draw()
                //                for ( var i: int = 0; i < step; i++ ) {
                //                    for ( var k: int = 0; k < step; k++ ) {
                //                        bitmapData.setPixel( ox + i, oy + k, color );
                //                    }
                //                }
                byteArray.position = 0;
                bitmapData.setPixels( new Rectangle( ox, oy, step, step ), byteArray );

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
        draw();
    }
}
}