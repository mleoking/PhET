// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;

/**
 * Shows a fraction as a percent, such as "12.3%"
 *
 * @author Sam Reid
 */
public class PercentNode extends VisibilityNode {
    public PercentNode( final CompositeProperty<Double> fraction, Property<Boolean> enabled ) {
        super( enabled );
        addChild( new PhetPText( fraction.get().toString(), DecimalFractionNode.FONT ) {{
            fraction.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    setText( new DecimalFormat( "0.00" ).format( value * 100 ) + "%" );
                }
            } );
        }} );
    }
}
