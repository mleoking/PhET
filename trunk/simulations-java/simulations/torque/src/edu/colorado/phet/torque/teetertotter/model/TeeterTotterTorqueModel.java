// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.torque.teetertotter.model.weights.Weight;

/**
 * Primary model class for the tab that depicts torque on a plank, a.k.a. a teeter totter.
 *
 * @author John Blanco
 */
public class TeeterTotterTorqueModel implements Resettable {
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // A list of all the weights in the model
    // TODO: I'm not sure that this is even needed, since we listen to all weights as soon as they are added.  Revist later and decide.
    private final List<Weight> weights = new ArrayList<Weight>();

    // Listeners that are notified when a weight is added to the model
    private final ArrayList<VoidFunction1<Weight>> weightAddedListeners = new ArrayList<VoidFunction1<Weight>>();

    // Listeners that are notified when a weight is removed from the model
    private final ArrayList<VoidFunction1<Weight>> weightRemovedListeners = new ArrayList<VoidFunction1<Weight>>();

    // Fulcrum on which the plank pivots
    private final Fulcrum fulcrum = new Fulcrum();

    // Support columns
    private final List<SupportColumn> supportColumns = new ArrayList<SupportColumn>() {{
        add( new SupportColumn( Fulcrum.getHeight(), -Plank.getLength() * 0.4 ) );
        add( new SupportColumn( Fulcrum.getHeight(), Plank.getLength() * 0.4 ) );
    }};

    // Property that controls whether the columns are supporting the plank.
    private final BooleanProperty supportColumnsActive = new BooleanProperty( true );

    // Plank that objects can be placed on that is (optionally) supported by pillars
    private final Plank plank = new Plank( clock, Fulcrum.getHeight(), supportColumnsActive );

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    public void addWeightAddedListener( VoidFunction1<Weight> listener ) {
        weightAddedListeners.add( listener );
    }

    public void removeWeightAddedListener( VoidFunction1<Weight> listener ) {
        weightAddedListeners.remove( listener );
    }

    public void addWeightRemovedListener( VoidFunction1<Weight> listener ) {
        weightRemovedListeners.add( listener );
    }

    public void removeWeightRemovedListener( VoidFunction1<Weight> listener ) {
        weightRemovedListeners.remove( listener );
    }

    // Adds a weight to the model and notifies registered listeners
    public UserMovableModelElement addWeight( final Weight weight ) {
        weight.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( !userControlled ) {
                    // The user has dropped this weight.
                    if ( plank.isPointAbovePlank( weight.getPosition() ) ) {
                        // The weight was dropped above the plank, move it to a
                        // valid location on the plank.
                        plank.addWeightToSurface( weight );
                    }
                    else {
                        // Put the weight on the ground.
                        // TODO: Once tool box nodes exist, this will cause the weight to return to the tool box.
                        weight.setPosition( weight.getPosition().getX(), 0 );
                    }
                }
            }
        } );
        weights.add( weight );
        // Notify listeners that a new weight has been added.
        for ( VoidFunction1<Weight> weightAddedListener : weightAddedListeners ) {
            weightAddedListener.apply( weight );
        }
        return weight;
    }

    // Removes a weight from the model and notifies listeners.
    public void removeWeight( Weight weight ) {
        if ( weights.contains( weight ) ) {
            weights.remove( weight );
            for ( VoidFunction1<Weight> weightRemovedListener : weightRemovedListeners ) {
                weightRemovedListener.apply( weight );
            }
        }
    }

    public Fulcrum getFulcrum() {
        return fulcrum;
    }

    public Plank getPlank() {
        return plank;
    }

    public List<SupportColumn> getSupportColumns() {
        return supportColumns;
    }

    public BooleanProperty getSupportColumnsActiveProperty() {
        return supportColumnsActive;
    }

    public void reset() {
        getClock().resetSimulationTime();

        // Remove any existing weights.
        for ( Weight weight : new ArrayList<Weight>( weights ) ) {
            removeWeight( weight );
        }
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}