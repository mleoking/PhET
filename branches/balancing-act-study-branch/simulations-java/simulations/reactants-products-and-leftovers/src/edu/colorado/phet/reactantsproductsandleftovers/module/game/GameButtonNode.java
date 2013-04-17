// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;

/**
 * Buttons used in the "Game" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GameButtonNode extends HTMLImageButtonNode {

    public GameButtonNode( IUserComponent userComponent, String text ) {
        super( text, new PhetFont( Font.BOLD, 30 ), new Color( 255, 255, 0, 150 ) /* translucent yellow */ );
        setUserComponent( userComponent );
    }
}
