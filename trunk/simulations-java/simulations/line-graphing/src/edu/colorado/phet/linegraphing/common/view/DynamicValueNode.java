// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Text node that stays synchronized with a dynamic value. This is used in interactive equations,
 * to keep non-interactive parts of the equation synchronized with the model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DynamicValueNode extends PText {

    public DynamicValueNode( Property<Double> value, final NumberFormat format, PhetFont font, Color color, final boolean absoluteValue ) {
        setFont( font );
        setTextPaint( color );
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                setText( format.format( ( absoluteValue ) ? Math.abs( value ) : value ) );
            }
        } );
    }
}
