//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.vseprtest.view {
import mx.containers.Canvas;
import mx.core.UIComponent;

/**
 * Base class for the main UI of the density and buoyancy sims.
 */
public class VseprTestCanvas extends Canvas {

    public function VseprTestCanvas() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle( "fontSize", 12 );

        addBackground();
//
        var context: Away3DContext = new Away3DContext();

        var holder: UIComponent = new UIComponent();
        holder.addChild( context.view );
        addChild( holder );

//        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
//            var flexCommon: FlexCommon = new FlexCommon();
//            flexCommon.initialize( holder );
//        } );
    }

    protected function addBackground(): void {
        var background: Canvas = new Canvas();
        background.percentWidth = 100;
        background.percentHeight = 100;

        background.graphics.beginFill( 0x000000 );
        background.graphics.drawRect( 0, 0, 100, 100 );

        var component: UIComponent = new UIComponent();
        component.addChild( background );
        addChild( component );
    }
}
}