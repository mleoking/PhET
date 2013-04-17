/**
 * Created by ${PRODUCT_NAME}.
 * User: Sam
 * Date: 12/7/10
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.resonance {
import edu.colorado.phet.flashcommon.CommonButtons;
import edu.colorado.phet.flexcommon.FlexCommon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Sprite;
import flash.events.Event;
import flash.ui.ContextMenu;

import mx.containers.Canvas;
import mx.core.Application;

public class ResonanceCanvas extends Canvas {
    public function ResonanceCanvas() {
    }

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    private var buttonHolder: Sprite = new Sprite();

    public function init( app: * ): void {
        setStyle( "backgroundColor", 0xffff99 );//same color as build an atom
        percentWidth = 100;
        percentHeight = 100;
        const res: Resonance = new Resonance( RENDER_WIDTH, RENDER_HEIGHT );//todo: compare to Util
        addChild( res );
        const listener: Function = function( event: Event ): void {
            const sx: Number = stage.stageWidth / RENDER_WIDTH;
            const sy: Number = stage.stageHeight / RENDER_HEIGHT;

            var scale: Number = Math.min( sx, sy );
            res.scaleX = scale;
            res.scaleY = scale;

            if ( sx < sy ) {
                // horizontally limited, so center vertically
                res.y = stage.stageHeight / 2 - RENDER_HEIGHT * scale / 2;
            }
            else {
                // vertically limited, so center horizontally
                res.x = stage.stageWidth / 2 - RENDER_WIDTH * scale / 2;
            }
        };
        stage.addEventListener( Event.RESIZE, listener );
        listener( null );

//        contextMenu = new ContextMenu();
//        contextMenu.builtInItems.zoom = true;
//
//        var application: Application = app as Application;
//        application.setStyle( "backgroundColor", 0xffff99 );

        //Create About button
        var common: FlexCommon = FlexCommon.getInstance();

        res.addChild( new SpriteUIComponent( buttonHolder ) );

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );

        common.initialize( buttonHolder );
    }

    private function positionButtons(): void {
        var buttons: CommonButtons = FlexCommon.getInstance().commonButtons;
        if ( buttons != null ) {
            if( buttons.getParent() == null ) {
                buttonHolder.addChild( buttons );
            }
            var logoWidth: Number = new PhetIcon().width;
            buttons.setLocationXY( RENDER_WIDTH - buttons.getPreferredWidth() - 10, 0 );
        }
    }
}
}
