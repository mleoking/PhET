// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;

/**
 * @author Sam Reid
 */
public class StoveControlSliderNode extends VSliderNode {
    public StoveControlSliderNode( final SettableProperty<Double> value ) {
        super( -1, 1, value );

        addLabel( +1, new PhetPText( "Add", new PhetFont( 16 ) ) );
        addLabel( 0.0, new PhetPText( "0", new PhetFont( 16 ) ) );
        addLabel( -1, new PhetPText( "Remove", new PhetFont( 16 ) ) );
    }
}
