
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.*;

/**
 * Model for the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionModel {
    
    private final ArrayList<ChangeListener> listeners;
    private final ChemicalReaction[] reactions;
    private ChemicalReaction reaction;
    
    public RealReactionModel() {
        reactions = new ChemicalReaction[] { new WaterReaction(), new AmmoniaReaction(), new MethaneReaction() };
        this.reaction = reactions[0];
        listeners = new ArrayList<ChangeListener>();
    }
    
    public void reset() {
        for ( ChemicalReaction reaction : reactions ) {
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getQuantityRange().getDefault() );
            }
        }
        setReaction( reactions[0] );
    }
    
    public ChemicalReaction[] getReactions() {
        return reactions;
    }
    
    public void setReaction( ChemicalReaction reaction ) {
        this.reaction = reaction;
        fireStateChanged();
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
    }
    
    public void addChangeListeners( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListeners( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        ArrayList<ChangeListener> listenersCopy = new ArrayList<ChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( ChangeListener listener : listenersCopy ) {
            listener.stateChanged( event );
        }
    }
    
    public IntegerRange getCoefficientRange() {
        return RealReactionDefaults.COEFFICIENT_RANGE;
    }
    
    public IntegerRange getQuantityRange() {
        return RealReactionDefaults.QUANTITY_RANGE;
    }
    
}
