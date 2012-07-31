/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 5/31/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import edu.colorado.phet.flashcommon.CommonButtons;
import edu.colorado.phet.flashcommon.view.PhetIcon;
import edu.colorado.phet.flexcommon.FlexCommon;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Sprite;

import flash.events.Event;

import mx.containers.Canvas;
import mx.core.UIComponent;

public class NormalModesCanvas extends Canvas {
    public function NormalModesCanvas() {
    }//end constructor

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    var buttonHolder: Sprite = new Sprite();

    public function init(): void {
        setStyle( "backgroundColor", 0xf1f191 );  //build an atom color:  0xffff99
        percentWidth = 100;
        percentHeight = 100;

        const myMainView: MainView = new MainView( RENDER_WIDTH, RENDER_HEIGHT )
        this.addChild( myMainView );

        const resizeListener: Function = function ( event: Event ): void {
            const sx: Number = stage.stageWidth / RENDER_WIDTH;
            const sy: Number = stage.stageHeight / RENDER_HEIGHT;
            myMainView.scaleX = Math.min( sx, sy );
            myMainView.scaleY = Math.min( sx, sy );
        };

        stage.addEventListener( Event.RESIZE, resizeListener );
        resizeListener( null );

        //Create About button
        var common: FlexCommon = FlexCommon.getInstance();

        myMainView.addChild( new SpriteUIComponent( buttonHolder ) );

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );

        common.initialize( buttonHolder );
    }//end init()

    private function positionButtons(): void {
        var buttons: CommonButtons = FlexCommon.getInstance().commonButtons;
        if ( buttons != null ) {
            trace( "positionButtons() buttons != null" );
            if( buttons.getParent() == null ) {
                buttonHolder.addChild( buttons );
            }
            var logoWidth: Number = new PhetIcon().width;
            buttons.setLocationXY( RENDER_WIDTH - buttons.getPreferredWidth() - logoWidth * 2 - 10, 0 );
        }
    }
}//end class
}//end package
