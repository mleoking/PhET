//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.components {
import flash.display.GradientType;

import mx.skins.halo.SliderTrackSkin;
import mx.styles.StyleManager;
import mx.utils.ColorUtil;

/**
 * Provides a skin for how a slider (used in SliderDecorator) should look when it is disabled.
 * Copied from SliderTrackSkin.as, since we weren't able to override or replace style values otherwise.
 */
public class MyTrackSkin extends SliderTrackSkin {

    override protected function updateDisplayList( w: Number, h: Number ): void {
        super.updateDisplayList( w, h );

        // User-defined styles.
        var borderColor: Number = getStyle( "borderColor" );
        var fillAlphas: Array = getStyle( "fillAlphas" );
        var fillColors: Array = getStyle( "trackColors" ) as Array;
        StyleManager.getColorNames( fillColors );

        // Derivative styles.
        borderColor = ColorUtil.adjustBrightness2( borderColor, 55 );
        var borderColorDrk: Number = ColorUtil.adjustBrightness2( borderColor, 0 );

        graphics.clear();

        drawRoundRect( 0, 0, w, h, 0, 0, 0 ); // Draw a transparent rect to fill the entire space

        drawRoundRect(
                1, 0, w, h - 1, 1.5,
                borderColorDrk, 1, null,
                GradientType.LINEAR, null,
                { x: 2, y: 1, w: w - 2, h: 1, r: 0 } );

        drawRoundRect(
                2, 1, w - 2, h - 2, 1,
                borderColor, 1, null,
                GradientType.LINEAR, null,
                { x: 2, y: 1, w: w - 2, h: 1, r: 0 } );

        drawRoundRect(
                2, 1, w - 2, 1, 0,
                fillColors, Math.max( fillAlphas[1] - 0.3, 0 ),
                horizontalGradientMatrix( 2, 1, w - 2, 1 ) );
    }
}
}