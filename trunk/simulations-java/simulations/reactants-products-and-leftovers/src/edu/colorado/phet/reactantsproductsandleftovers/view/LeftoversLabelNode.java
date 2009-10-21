package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;


public class LeftoversLabelNode extends BracketedLabelNode {
    
    private static final Color BRACK_COLOR = new Color( 46, 107, 178 );
    private static final PhetFont TEXT_FONT = new PhetFont( 16 );

    public LeftoversLabelNode( double width ) {
        super( RPALStrings.LABEL_LEFTOVERS, width );
        setTextFont( TEXT_FONT );
        setBracketStrokePaint( BRACK_COLOR );
    }
}
