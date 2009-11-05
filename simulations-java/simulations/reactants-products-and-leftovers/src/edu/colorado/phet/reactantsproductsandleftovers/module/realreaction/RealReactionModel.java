
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;

/**
 * Model for the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionModel {
    
    private final ArrayList<ChangeListener> listeners;
    
    private ChemicalReaction reaction;
    
    public RealReactionModel( ChemicalReaction reaction ) {
        this.reaction = reaction;
        listeners = new ArrayList<ChangeListener>();
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
