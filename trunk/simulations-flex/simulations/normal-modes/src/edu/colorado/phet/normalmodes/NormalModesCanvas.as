/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import edu.colorado.phet.normalmodes.view.MainView;

import flash.events.Event;

import mx.containers.Canvas;

public class NormalModesCanvas extends Canvas {
    public function NormalModesCanvas() {
    }//end constructor

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function init(): void {
        //trace("NormalModesCanvas.init() called");
        setStyle( "backgroundColor", 0x99ffff );//same color as build an atom   0xffff99
        percentWidth = 100;
        percentHeight = 100;
        //const res: NormalModes = new NormalModes( RENDER_WIDTH, RENDER_HEIGHT );//todo: compare to Util
        const myMainView : MainView = new MainView( RENDER_WIDTH, RENDER_HEIGHT )
        this.addChild( myMainView );
       // addChild( res );
        const listener: Function = function( event: Event ): void {
            const sx = stage.stageWidth / RENDER_WIDTH;
            const sy = stage.stageHeight / RENDER_HEIGHT;

            myMainView.scaleX = Math.min( sx, sy );
            myMainView.scaleY = Math.min( sx, sy );
            //res.scaleX = Math.min( sx, sy );
            //res.scaleY = Math.min( sx, sy );
        };
        stage.addEventListener( Event.RESIZE, listener );
        listener( null );
    }//end init()
}//end class
}//end package
