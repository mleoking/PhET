// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;



import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for views in the game that include a periodic table on the right side.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class ToElementView extends ProblemView {
    private final ProblemDescriptionNode description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_FIND_THE_ELEMENT );

    private final GamePeriodicTable gamePeriodicTable = new GamePeriodicTable() {{
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.getWidth()*0.715 - getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.getHeight()/2-getFullBounds().getHeight()/2 );
        scale( 1.2 );
        super.addJRadioButtonListener(new SimpleObserver(){
            public void update() {
                enableCheckButton();
            }
        });
    }};

    /**
     * Constructor.
     */
    ToElementView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        description.centerAbove( gamePeriodicTable );
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( gamePeriodicTable );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( gamePeriodicTable );
    }

    @Override
    protected ImmutableAtom getGuess() {
        boolean userGuessMatchesAnswerNeutrality = gamePeriodicTable.doesAtomChargeMatchGuess( getProblem().getAnswer() );
        return new ImmutableAtom( gamePeriodicTable.getGuessedNumberProtons(),
                              getProblem().getAnswer().getNumNeutrons(),
                              userGuessMatchesAnswerNeutrality ? getProblem().getAnswer().getNumElectrons() ://if they guessed the right neutrality, assume they got the number of electrons right
                              getProblem().getAnswer().getNumElectrons() + 1 );//If they guessed the incorrect neutrality, then just return a number that differs from the correct # electrons
    }

    @Override
    protected void displayAnswer( ImmutableAtom answer ) {
        gamePeriodicTable.setNumProtonsInAtom( answer.getNumProtons() );
        gamePeriodicTable.setGuessNeutral( answer.isNeutral() );
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        //Disable the user from changing the answer
        gamePeriodicTable.setPickable( guessEditable );
        gamePeriodicTable.setChildrenPickable( guessEditable );
    }
}
