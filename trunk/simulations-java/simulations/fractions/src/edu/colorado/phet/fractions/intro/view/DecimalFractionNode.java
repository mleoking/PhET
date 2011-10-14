// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.view;

import java.awt.Font;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;

/**
 * Shows a decimal representation like "1.35"
 *
 * @author Sam Reid
 */
public class DecimalFractionNode extends VisibilityNode {

    public static final Font FONT = new PhetFont( 64 );

    public DecimalFractionNode( final CompositeProperty<Double> fraction, ObservableProperty<Boolean> visible ) {
        super( visible );
        addChild( new PhetPText( fraction.get().toString(), FONT ) {{
            fraction.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    setText( new DecimalFormat( "0.00" ).format( value ) );
                }
            } );
        }} );
    }
}
