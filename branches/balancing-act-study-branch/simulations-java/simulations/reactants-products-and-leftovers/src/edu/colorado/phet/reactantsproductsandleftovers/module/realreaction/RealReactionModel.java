// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConfig;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.AmmoniaReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.WaterReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.TwoProductReactions.MethaneReaction;

/**
 * Model for the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionModel extends RPALModel {

    private final EventListenerList listeners;
    private final ChemicalReaction[] reactions;
    private ChemicalReaction reaction;
    private final ChemicalReaction defaultReaction;

    public RealReactionModel() {
        reactions = new ChemicalReaction[] { new WaterReaction(), new AmmoniaReaction(), new MethaneReaction() };
        defaultReaction = ( SimSharingManager.usingConfig( SimSharingConfig.RPAL_APRIL_2012 ) ) ? reactions[1] : reactions[0];
        reaction = defaultReaction;
        listeners = new EventListenerList();
    }

    public void reset() {
        for ( ChemicalReaction reaction : reactions ) {
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getQuantityRange().getDefault() );
            }
        }
        setReaction( defaultReaction );
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
