// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.view.SpinnerButtonPanel;

/**
 * Spinner that shows and allows the user to change the max value (max number of filled containers) for the sim between 1-6.
 *
 * @author Sam Reid
 */
public class MaxSpinner extends RichPNode {
    public MaxSpinner( final IntegerProperty value ) {
        addChild( new HBox( new PhetPText( new PhetFont( 24 ) ) {{
            value.addObserver( new VoidFunction1<Integer>() {
                @Override public void apply( Integer m ) {
                    setText( "Max " + m );
                }
            } );
        }}, new SpinnerButtonPanel( (int) ( 50.0 / 2.0 ), value.increment_(), value.lessThan( 6 ), value.decrement_(), value.greaterThan( 1 ) ) ) );
    }
}
