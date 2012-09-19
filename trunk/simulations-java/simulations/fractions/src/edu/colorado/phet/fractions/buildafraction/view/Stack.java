// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;

/**
 * Keep track of a stack or pile of cards for purposes of making sure only the top one is grabbable,
 * and if a middle card is taken out, then the top card will fall down one slot.
 *
 * @author Sam Reid
 */
public @Data class Stack<T extends Stackable> {
    public List<T> cards;
    private final Integer stackIndex;
    private final StackContext<T> context;
    private final List<T> originalCards;

    public Stack( List<T> cards, final Integer stackIndex, final StackContext<T> context ) {
        this.cards = cards;
        this.originalCards = cards;
        this.stackIndex = stackIndex;
        this.context = context;

        updatePickable();
    }

    //find the front-most card in the stack, and make it grabbable
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
        context.movedNonStackCardsToFront();
    }

    @SuppressWarnings("unchecked") private Option<T> getFrontCardInStack() {
        List<T> sorted = cards.sort( FJUtils.ord( new F<T, Double>() {
            @Override public Double f( final T n ) {
                final Option<Integer> positionInStack = n.getPositionInStack(); //unchecked warning
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

    public Vector2D getLocation( final int index, T card ) { return context.getLocation( stackIndex, index, card ); }

    //Find the location for the topmost (in z-ordering) card and move the card there.  Also mark the site as used so no other cards will go there.
    public void animateToTopOfStack( final T cardNode, boolean deleteOnArrival ) {
        List<Integer> sites = List.range( 0, cards.length() );
        for ( Integer site : sites ) {
            if ( !hasCardAtSite( site ) ) {
                cardNode.setPositionInStack( Option.some( site ) );
                cardNode.animateToStackLocation( getLocation( site, cardNode ), deleteOnArrival );
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