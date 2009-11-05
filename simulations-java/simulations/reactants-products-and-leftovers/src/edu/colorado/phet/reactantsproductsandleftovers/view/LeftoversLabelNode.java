package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;

/**
 * A bracket that delineates the leftover reactants in a reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LeftoversLabelNode extends BracketedLabelNode {
    
    private static final Color BRACK_COLOR = new Color( 46, 107, 178 );
    private static final PhetFont TEXT_FONT = new PhetFont( 16 );

    public LeftoversLabelNode( double width ) {
        super( RPALStrings.LABEL_LEFTOVERS, width );
        setTextFont( TEXT_FONT );
        setBracketStrokePaint( BRACK_COLOR );
    }
}
