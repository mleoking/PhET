package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a message telling the user that images are hidden for a challenge.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculesHiddenNode extends PText {

    public MoleculesHiddenNode() {
        setText( RPALStrings.MESSAGE_MOLECULES_HIDDEN );
        setTextPaint( Color.BLACK );
        setFont( new PhetFont( 28 ) );
    }
}
