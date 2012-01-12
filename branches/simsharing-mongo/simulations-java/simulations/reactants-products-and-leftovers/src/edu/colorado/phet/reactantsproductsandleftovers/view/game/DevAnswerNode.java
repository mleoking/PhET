// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;

/**
 * Shows the number of reactants, products and leftovers (the Game's correct answer).
 * This node is a developer control and not internationalized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevAnswerNode extends HTMLNode {

    private static final PhetFont FONT = new PhetFont( 10 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = null;

    private final GameModel model;

    public DevAnswerNode( GameModel model ) {
        super();
        setFont( FONT );
        setHTMLColor( TEXT_COLOR );
        setPaint( BACKGROUND_COLOR );
        this.model = model;
        model.addGameListener( new GameAdapter() {
            @Override
            public void challengeChanged() {
                update();
            }
        } );
        update();
    }

    protected void update() {
        setHTML( HTMLUtils.toHTMLString( model.getChallenge().toString() ) );
    }
}
