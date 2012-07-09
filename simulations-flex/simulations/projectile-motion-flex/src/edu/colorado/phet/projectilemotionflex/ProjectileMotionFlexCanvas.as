/*
 * Copyright 2002-2012, University of Colorado
 */

package edu.colorado.phet.projectilemotionflex {
import edu.colorado.phet.projectilemotionflex.view.MainView;

import flash.display.StageQuality;
import flash.events.Event;

import mx.containers.Canvas;

/**
 *
 */
public class ProjectileMotionFlexCanvas extends Canvas {
    public function ProjectileMotionFlexCanvas() {
    }//end constructor

    private var RENDER_WIDTH: int = 1024;
    private var RENDER_HEIGHT: int = 768;

    public function init(): void {
        setStyle( "backgroundColor", 0xf0e68c );  //build an atom color is 0xffff99
        percentWidth = 100;
        percentHeight = 100;
        const myMainView: MainView = new MainView( RENDER_WIDTH, RENDER_HEIGHT )
        this.addChild( myMainView );
        const listener: Function = function ( event: Event ): void {
            const sx = stage.stageWidth / RENDER_WIDTH;
            const sy = stage.stageHeight / RENDER_HEIGHT;

            myMainView.scaleX = Math.min( sx, sy );
            myMainView.scaleY = Math.min( sx, sy );
        }


        stage.addEventListener( Event.RESIZE, listener );
        listener( null );
    }//end init()

    public function setResolution( rez: String ): void {
        if ( rez == "LOW" ) {
            stage.quality = StageQuality.LOW;
        }
        else if ( rez == "HIGH" ) {
            stage.quality = StageQuality.HIGH;
        }
    }
}//end class
}//end package
