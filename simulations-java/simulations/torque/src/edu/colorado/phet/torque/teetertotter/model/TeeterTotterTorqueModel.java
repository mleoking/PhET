// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.torque.teetertotter.model.weights.Brick;
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
    private final List<Weight> weights = new ArrayList<Weight>();

    // Listeners that are notified when a weight is added to the model
    private final ArrayList<VoidFunction1<Weight>> weightAddedListeners = new ArrayList<VoidFunction1<Weight>>();

    // Listeners that are notified when a weight is removed from the model
    private final ArrayList<VoidFunction1<Weight>> weightRemovedListeners = new ArrayList<VoidFunction1<Weight>>();

    // Fulcrum on which the plank pivots
    private final Fulcrum fulcrum = new Fulcrum();

    // Plank that objects can be placed on that is (optionally) supported by pillars
    private final Plank plank = new Plank( clock, Fulcrum.getHeight() );

    // Support columns
    private final List<SupportColumn> supportColumns = new ArrayList<SupportColumn>() {{
        add( new SupportColumn( Fulcrum.getHeight(), -plank.getLength() * 0.4 ) );
        add( new SupportColumn( Fulcrum.getHeight(), plank.getLength() * 0.4 ) );
    }};

    // Property that controls whether the columns are supporting the plank.
    private final BooleanProperty supportColumnsActive = new BooleanProperty( true ) {{
        addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean supportColumnsActive ) {
                if ( supportColumnsActive ) {
                    plank.setTorqueFromWeights( 0 );
                    plank.forceToLevel();
                }
                else {
                    updateTorqueDueToWeights();
                }
            }
        } );
    }};

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
    public void addWeight( final Weight weight ) {
        weight.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( !userControlled ) {
                    // The user has dropped this weight.
                    if ( plank.isPointAbovePlank( weight.getPosition() ) ) {
                        // The weight was dropped above the plank, move it to a
                        // valid location on the plank.
                        weight.setPosition( plank.getClosestOpenLocation( weight.getPosition() ) );
                        plank.addWeightToSurface( weight );
                        // Update the torque on the plank due to the weights.
                        if ( !supportColumnsActive.get() ) {
                            updateTorqueDueToWeights();
                        }
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
    }

    private void updateTorqueDueToWeights() {
        double netTorqueFromWeights = 0;
        for ( Weight weight : weights ) {
            // TODO: Need a better way to determine whether a weight is on the plank.  Weights under the plank will affect torque using this clause.
            if ( weight.getPosition().getX() > plank.getShape().getBounds2D().getMinX() &&
                 weight.getPosition().getX() < plank.getShape().getBounds2D().getMaxX() ) {
                // Note: According to M. Dubson, the convention is that torque that causes a counter-
                // clockwise motion is considered positive.
                netTorqueFromWeights += -weight.getPosition().getX() * weight.getMass();
            }
        }
        plank.setTorqueFromWeights( netTorqueFromWeights );
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
        // Add initial weights.
        addWeight( new Brick( new Point2D.Double( 3, 0 ) ) );
        addWeight( new Brick( new Point2D.Double( 3.3, 0 ) ) );
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}