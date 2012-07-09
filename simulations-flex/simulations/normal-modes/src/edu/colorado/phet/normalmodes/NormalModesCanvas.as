/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import edu.colorado.phet.flexcommon.FlexCommon;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.events.Event;

import mx.containers.Canvas;
import mx.core.UIComponent;

public class NormalModesCanvas extends Canvas {
    public function NormalModesCanvas() {
    }//end constructor

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function init(): void {
        var buttonHolder: UIComponent = new UIComponent();
        addChild( buttonHolder );
        var common: FlexCommon = FlexCommon.getInstance();
        common.initialize( buttonHolder );
        setStyle( "backgroundColor", 0xf1f191 );  //build an atom color:  0xffff99
        percentWidth = 100;
        percentHeight = 100;

        const myMainView : MainView = new MainView( RENDER_WIDTH, RENDER_HEIGHT )
        this.addChild( myMainView );

        const listener: Function = function( event: Event ): void {
            const sx: Number  = stage.stageWidth / RENDER_WIDTH;
            const sy: Number = stage.stageHeight / RENDER_HEIGHT;
            myMainView.scaleX = Math.min( sx, sy );
            myMainView.scaleY = Math.min( sx, sy );
        };

        stage.addEventListener( Event.RESIZE, listener );
        listener( null );
    }//end init()
}//end class
}//end package
