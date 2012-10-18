// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;

/**
 * Text node that stays synchronized with a dynamic value. This is used in interactive equations,
 * to keep non-interactive parts of the equation synchronized with the model. The model uses double
 * precision, but equations display integers, so conversion is performed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DynamicValueNode extends PhetPText {

    public DynamicValueNode( Property<Double> value, PhetFont font, Color color ) {
        this( value, font, color, false );
    }

    public DynamicValueNode( Property<Double> value, PhetFont font, Color color, final boolean absoluteValue ) {
        super( toIntString( value.get(), absoluteValue ), font, color );
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                setText( toIntString( value, absoluteValue ) );
            }
        } );
    }

    // Converts a double to an integer string, using nearest-neighbor rounding.
    private static String toIntString( double d, boolean absoluteValue ) {
        int i = MathUtil.roundHalfUp( d );
        if ( absoluteValue ) {
            i = Math.abs( i );
        }
        return String.valueOf( i );
    }
}
