import flash.display.BitmapData
import flash.geom.Rectangle

class VoltageMosaic extends MovieClip implements Observer {

    private var bitmapData:BitmapData;
    private var model:ChargeGroup;

    public var step:Number = 10;

    public var sWidth:Number = 640;
    public var sHeight:Number = 480;

    public function VoltageMosaic() {
        trace( "created" );
    }

    public function init( model:ChargeGroup ) {
        this.model = model;
        bitmapData = new BitmapData( sWidth, sHeight, false, 0xffffff );
        attachBitmap( bitmapData, getNextHighestDepth() );
        model.addObserver( this );
    }

    public function update( model:ChargeGroup ):Void {
        var rect:Rectangle = new Rectangle( 0, 0, step, step );
        var halfStep:Number=step/2;
        for ( var i:Number = 0; i < sWidth; i += step ) {
            var iPos = i + halfStep;
            rect.x = i;
            for ( var j:Number = 0; j < sHeight; j += step ) {
                rect.y = j;
                bitmapData.fillRect( rect, model.getColor( iPos, j + halfStep ) );
            }
        }
    }
}