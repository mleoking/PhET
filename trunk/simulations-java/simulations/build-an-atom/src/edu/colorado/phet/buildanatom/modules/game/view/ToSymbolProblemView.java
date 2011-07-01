// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.ToSymbolProblem;

/**
 * Base class for Piccolo ProblemViews that require the user to predict the Symbol for a corresponding input.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ToSymbolProblemView extends ProblemView {

    private final ProblemDescriptionNode description;
    private final InteractiveSymbolNode interactiveSymbolNode;

    /**
     * Constructor.
     */
    public ToSymbolProblemView( BuildAnAtomGameModel model, BuildAnAtomGameCanvas canvas, ToSymbolProblem problem ) {
        super( model, canvas, problem );

        interactiveSymbolNode = new InteractiveSymbolNode( problem.getAnswer(), problem.isConfigurableProtonCount(),
                                                           problem.isConfigurableMass(), problem.isConfigurableCharge() );
        interactiveSymbolNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                enableCheckButton();
            }
        } );
        interactiveSymbolNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width * 0.75 - interactiveSymbolNode.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - interactiveSymbolNode.getFullBounds().getHeight() / 2 );

        // Set up the problem description based upon which of the fields need
        // to be filled in.
        if ( problem.isConfigurableProtonCount() && problem.isConfigurableMass() && problem.isConfigurableCharge() ) {
            description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_COMPLETE_THE_SYMBOL_ALL );
        }
        else if ( problem.isConfigurableProtonCount() && !problem.isConfigurableMass() && !problem.isConfigurableCharge() ) {
            description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_COMPLETE_THE_SYMBOL_PROTON_COUNT );
        }
        else if ( !problem.isConfigurableProtonCount() && problem.isConfigurableMass() && !problem.isConfigurableCharge() ) {
            description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_COMPLETE_THE_SYMBOL_MASS );
        }
        else if ( !problem.isConfigurableProtonCount() && !problem.isConfigurableMass() && problem.isConfigurableCharge() ) {
            description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_COMPLETE_THE_SYMBOL_CHARGE );
        }
        else {
            // Should not reach this code, debug if it does.
            System.err.println( getClass().getName() + " - Error: No problem description available for specified configuration." );
            assert false;
            description = new ProblemDescriptionNode( "Fill in." );
        }

        description.centerAbove( interactiveSymbolNode );

        // For this type of problem view, the check button is enabled right away.
        enableCheckButton();
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( interactiveSymbolNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( interactiveSymbolNode );
    }

    @Override
    protected ImmutableAtom getGuess() {
        return interactiveSymbolNode.getGuess();
    }

    @Override
    protected void displayAnswer( ImmutableAtom answer ) {
        interactiveSymbolNode.displayAnswer( answer );
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        interactiveSymbolNode.setPickable( guessEditable );
        interactiveSymbolNode.setChildrenPickable( guessEditable );
    }
}
