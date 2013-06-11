/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab {
import edu.colorado.phet.flashcommon.CommonButtons;
import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.FlexCommon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.triglab.util.Util;
import edu.colorado.phet.triglab.view.MainView;

import flash.display.Sprite;

import flash.events.Event;
import flash.display.StageQuality;

import mx.containers.Canvas;

public class TrigLabCanvas extends Canvas {

    var buttonHolder: Sprite = new Sprite();
    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function TrigLabCanvas() {
    }//end constructor

    public function init(): void {
        setStyle( "backgroundColor", 0x000055 );    //Util.XYAXESCOLORbeige is 0xf1f191
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

        //Create About button
        var common: FlexCommon = FlexCommon.getInstance();

        myMainView.addChild( new SpriteUIComponent( buttonHolder ) );

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );

        common.initialize( buttonHolder );

    }//end init()

    //Not currently used.
    public function setResolution( rez:String):void{
        if( rez == "LOW"){
            stage.quality = StageQuality.LOW;
        }else if (rez == "HIGH"){
            stage.quality = StageQuality.HIGH;
        }
    }

    private function positionButtons(): void {
        var buttons: CommonButtons = FlexCommon.getInstance().commonButtons;
        if ( buttons != null ) {
            trace( "positionButtons() buttons != null" );
            if( buttons.getParent() == null ) {
                buttonHolder.addChild( buttons );
            }
            var logoWidth: Number = new PhetIcon().width;
            buttons.setLocationXY( 0, RENDER_HEIGHT - buttons.getPreferredHeight());//place button in lower-left corner

        }
    }
}//end class
}//end package
