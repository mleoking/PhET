/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * A "guess" is the user's answer to a game challenge, and it may or may not be correct.
 * We call it a "guess" to distinguish the user's answer from the correct answer.
 * (And yes, "guess" is semantically incorrect, since a guess is uninformed. Live with it.)
 * <p>
 * A guess is correct if all of the reactants and products in the guess are equal to
 * the reactants and products in the reaction (the correct answer), as defined by method equals.
 * <p>
 * A guess is constructed based on a reaction and challenge type.
 * The guess will have the same number of reactants and products as the reaction,
 * and they are guaranteed to be in the same order.
 * Depending on the challenge type, values in the guess will be initialized to zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameGuess {
    
    private final EventListenerList listeners;
    
    public GameGuess( GameChallenge.ChallengeType challengeType ) {
        listeners = new EventListenerList();
    }

    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
