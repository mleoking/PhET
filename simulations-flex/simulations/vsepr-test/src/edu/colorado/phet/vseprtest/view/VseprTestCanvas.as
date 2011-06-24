//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.vseprtest.view {
import away3d.primitives.Sphere;

import edu.colorado.phet.flashcommon.ApplicationLifecycle;

import flash.events.Event;

import mx.containers.Canvas;
import mx.core.UIComponent;

public class VseprTestCanvas extends Canvas {

    private var background: Canvas = new Canvas();
    private var context: Away3DContext = new Away3DContext();
    private var finishedLoading = false;

    public function VseprTestCanvas() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle( "fontSize", 12 );

        background.percentWidth = 100;
        background.percentHeight = 100;
        addChild( background );


        context.init();
        context.scene.addChild( new Sphere( {radius:100} ) );
        context.addLight();

        var holder: UIComponent = new UIComponent();
        holder.addChild( context.view );
        addChild( holder );


        // listeners
        addEventListener( Event.ENTER_FRAME, onEnterFrame );
        addEventListener( Event.RESIZE, onResize );

        // we listen to the background, since it is set to 100% width and height, and once this component is added the resize will be called
        background.addEventListener( Event.RESIZE, function( evt: Event ): void {
            onResize( evt );
        } );

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            finishedLoading = true;
        } );

//        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
//            var flexCommon: FlexCommon = new FlexCommon();
//            flexCommon.initialize( holder );
//        } );
    }

    public function onEnterFrame( event: Event ): void {
        if ( finishedLoading ) {
            context.view.render();
        }
    }

    public function onResize( event: Event ): void {
        background.graphics.beginFill( 0x000000 );
        background.graphics.drawRect( 0, 0, background.width, background.height );
        context.view.x = background.width / 2;
        context.view.y = background.height / 2;
    }

}
}