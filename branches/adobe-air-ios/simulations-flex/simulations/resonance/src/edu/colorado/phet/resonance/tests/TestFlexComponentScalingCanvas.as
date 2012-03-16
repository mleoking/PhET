/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/7/10
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance.tests {
import flash.events.Event;

import mx.containers.Canvas;

public class TestFlexComponentScalingCanvas extends Canvas {
    public function TestFlexComponentScalingCanvas() {
    }

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function init(): void {
        setStyle( "backgroundColor", 0xffff99 );//same color as build an atom
        percentWidth = 100;
        percentHeight = 100;
        const res: TestCanvas = new TestCanvas( RENDER_WIDTH, RENDER_HEIGHT );//todo: compare to Util
        addChild( res );
        const listener: Function = function( event: Event ): void {
            const sx = stage.stageWidth / RENDER_WIDTH;
            const sy = stage.stageHeight / RENDER_HEIGHT;

            res.scaleX = Math.min( sx, sy );
            res.scaleY = Math.min( sx, sy );
        };
        stage.addEventListener( Event.RESIZE, listener );
        listener( null );
    }
}
}
