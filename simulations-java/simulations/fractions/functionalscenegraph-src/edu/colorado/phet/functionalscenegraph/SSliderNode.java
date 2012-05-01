// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionalscenegraph;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * @author Sam Reid
 */
public class SSliderNode extends SList {
    private SSliderNode() {}

    public static SNode init( double knobFraction ) {
        final SNode track = new DrawShape( new ImmutableRectangle2D( 0, 0, 100, 5 ) ).withPaint( Color.black );
        double trackLength = track.getBounds().width;
        double knobX = trackLength * knobFraction;
        final SNode knob = DrawText.textNode( "Knob", new PhetFont( 14, true ), Color.red );
        return new SList( track, knob.centeredOn( track.leftCenter() ).translate( knobX, 0 ) );
    }
}