package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolo.nodes.PText;


public class ReactionNumberLabelNode extends PText {
    
    private static final String PATTERN = RPALStrings.LABEL_REACTION_NUMBER;
    private static final PhetFont FONT = new PhetFont( 36 );
    private static final Color COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    
    public ReactionNumberLabelNode( final GameModel model ) {
        super();
        setFont( FONT );
        setTextPaint( COLOR );
        setNumber( model.getChallengeNumber() );
        model.addGameListener( new GameAdapter() {
            public void reactionChanged() {
                setNumber( model.getChallengeNumber() );
            }
        });
    }
    
    private void setNumber( int number ) {
        setText( MessageFormat.format(  PATTERN, new Integer( number ), new Integer( GameModel.getChallengesPerGame() ) ) );
    }
}
