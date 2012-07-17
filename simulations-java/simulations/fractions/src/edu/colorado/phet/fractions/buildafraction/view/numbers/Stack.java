// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.List;
import lombok.Data;

import static edu.colorado.phet.fractions.buildafraction.view.numbers.NumberCardNode._initialPositionEquals;
import static edu.colorado.phet.fractions.buildafraction.view.numbers.NumberCardNode._isInStackLocation;

/**
 * Keep track of a stack or pile of cards for purposes of making sure only the top one is grabbable, and if a middle card is taken out, then the top card will fall down one slot.
 *
 * @author Sam Reid
 */
public @Data class Stack {
    public final List<NumberCardNode> cards;

    public Stack( List<NumberCardNode> cards ) {
        this.cards = cards;

        for ( NumberCardNode card : cards ) {
            card.setStack( this );//Registers for callbacks for stack change events
        }
        updatePickable();
    }

    //find the frontmost card in the stack, and make it grabbable
    private void updatePickable() {
        for ( NumberCardNode card : cards ) {
            card.setAllPickable( false );
        }
        if ( getCardsCurrentlyInStack().length() > 0 ) {
            getCardsCurrentlyInStack().last().setAllPickable( true );
        }
    }

    private List<NumberCardNode> getCardsCurrentlyInStack() {return cards.filter( _isInStackLocation );}

    public void cardMoved( final NumberCardNode numberCardNode ) {
        updatePickable();
    }

    public boolean contains( final double xOffset, final double yOffset ) {
        return cards.exists( _initialPositionEquals( xOffset, yOffset ) );
    }
}