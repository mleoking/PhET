/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge {
import edu.colorado.phet.radiatingcharge.view.MainView;

import flash.events.Event;
import flash.display.StageQuality;

import mx.containers.Canvas;

public class RadiatingChargeCanvas extends Canvas {
    public function RadiatingChargeCanvas() {
    }//end constructor

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function init(): void {
        setStyle( "backgroundColor", 0x000000 );  // 0xf0e68c  //build an atom color is 0xffff99
        percentWidth = 100;
        percentHeight = 100;
        const myMainView : MainView = new MainView( this, RENDER_WIDTH, RENDER_HEIGHT )
        this.addChild( myMainView );
        const listener: Function = function( event: Event ): void {
            const sx: Number = stage.stageWidth / RENDER_WIDTH;
            const sy: Number = stage.stageHeight / RENDER_HEIGHT;

            myMainView.scaleX = Math.min( sx, sy );
            myMainView.scaleY = Math.min( sx, sy );
        }
        stage.addEventListener( Event.RESIZE, listener );
        listener( null );
    }//end init()

    //Not currently used.
    public function setResolution( rez:String):void{
        if( rez == "LOW"){
            stage.quality = StageQuality.LOW;
        }else if (rez == "HIGH"){
            stage.quality = StageQuality.HIGH;
        }
    }
}//end class
}//end package
