// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.common.util.FJUtils;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * Keep track of a stack or pile of cards for purposes of making sure only the top one is grabbable, and if a middle card is taken out, then the top card will fall down one slot.
 * <p/>
 * Notes: Weird restacking order when pieces or cards go back to the control panel.
 * Need to remember and restore the z-ordering somehow.
 * But pieces should also "fall" to the locations closer to the container icon--this should be accounted for at the same time.
 * Maybe make it so you can only grab the topmost one.
 * Then when reset or sending back cards, send to recorded location.
 * Well, I'm not sure that will work, since in a stack ABC with C in the front, you could send B to the collection box then send C back to the toolbox.
 * It should go to B's location in that case.
 *
 * @author Sam Reid
 */
public @Data class Stack<T extends Stackable> {
    public final List<T> cards;
    private final Integer stackIndex;
    private final StackContext context;

    public Stack( List<T> cards, final Integer stackIndex, final StackContext<T> context ) {
        this.cards = cards;
        this.stackIndex = stackIndex;
        this.context = context;

        updatePickable();
    }

    //find the frontmost card in the stack, and make it grabbable
    private void updatePickable() {
        for ( T card : cards ) {
            card.setAllPickable( false );
        }
        final Option<T> front = getFrontCardInStack();
        if ( front.isSome() ) {
            front.some().setAllPickable( true );
            front.some().moveToFront();
        }
        moveNonStackCardsToFront();
    }

    //Make sure dragged card remains in front
    private void moveNonStackCardsToFront() {
        for ( T card : cards ) {
            if ( card.getPositionInStack().isNone() ) {
                card.moveToFront();
            }
        }
    }

    private Option<T> getFrontCardInStack() {
        List<T> sorted = cards.sort( FJUtils.ord( new F<T, Double>() {
            @Override public Double f( final T n ) {
                final Option<Integer> positionInStack = n.getPositionInStack();
                return positionInStack.orSome( -1 ).doubleValue();
            }
        } ) );
        return sorted.isEmpty() ? Option.<T>none() : Option.some( sorted.last() );
    }

    //Fix Z ordering so that stacks will look like they did on startup
    public void update() {
        for ( T card : cards ) {
            card.moveToFront();
        }
        moveNonStackCardsToFront();
        updatePickable();
    }

    private final F<Option<Integer>, List<Integer>> optionToList = FJUtils.optionToList();

    public Vector2D getLocation( final int index, T card ) {
        return context.getLocation( stackIndex, index, card );
    }

    //Find the location for the topmost (in z-ordering) card and move the card there.  Also mark the site as used so no other cards will go there.
    public void moveToTopOfStack( final T cardNode ) {
        List<Integer> sites = List.range( 0, cards.length() );
        for ( Integer site : sites ) {
            if ( !hasCardAtSite( site ) ) {
                cardNode.setPositionInStack( Option.some( site ) );
                cardNode.animateTo( getLocation( site, cardNode ) );
                cardNode.moveToFront();
                break;
            }
        }
        moveNonStackCardsToFront();
    }

    private boolean hasCardAtSite( final Integer site ) {
        for ( T card : cards ) {
            if ( card.isAtStackIndex( site ) ) {
                return true;
            }
        }
        return false;
    }

    public void cardMoved() {
        updatePickable();
    }
}