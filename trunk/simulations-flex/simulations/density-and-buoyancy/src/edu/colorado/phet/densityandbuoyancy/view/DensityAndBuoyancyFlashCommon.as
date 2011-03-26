//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flexcommon.FlexCommon;

import flash.events.Event;
import flash.geom.ColorTransform;

import mx.core.UIComponent;

/**
 * Adds project-specific customization for common button locations and high contrast
 */
public class DensityAndBuoyancyFlashCommon extends UIComponent {
    public function DensityAndBuoyancyFlashCommon() {
    }

    public function init(): void {
        var common: FlexCommon = FlexCommon.getInstance();
        common.initialize( this );

        common.highContrastFunction = function ( contrast: Boolean ): void {
            if ( contrast ) {
                var stretch: Number = 2.0;
                var newCenter: Number = 128;
                var offset: Number = newCenter - 128 * stretch;
                root.stage.transform.colorTransform = new ColorTransform( stretch, stretch, stretch, 1, offset, offset, offset, 1 );
            }
            else {
                root.stage.transform.colorTransform = new ColorTransform( 1, 1, 1, 1, 0, 0, 0, 0 );
            }
        };

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );
        positionButtons();
    }

    protected function positionButtons(): void {
        throw new Error( "Nice to meet you, I'm abstract!" );
    }

}
}