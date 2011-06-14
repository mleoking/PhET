// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays the reaction number in the Game, for example "2 of 10".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactionNumberLabelNode extends PText {
    
    private static final String PATTERN = RPALStrings.LABEL_REACTION_NUMBER;
    private static final PhetFont FONT = new PhetFont( 20 );
    private static final Color COLOR = Color.BLACK;
    
    public ReactionNumberLabelNode( final GameModel model ) {
        super();
        setFont( FONT );
        setTextPaint( COLOR );
        setNumber( model.getChallengeNumber() );
        model.addGameListener( new GameAdapter() {
            @Override
            public void challengeChanged() {
                setNumber( model.getChallengeNumber() + 1 ); // model numbers challenges starting at 0, so +1
            }
        });
    }
    
    private void setNumber( int number ) {
        setText( MessageFormat.format(  PATTERN, new Integer( number ), new Integer( GameModel.getChallengesPerGame() ) ) );
    }
}
