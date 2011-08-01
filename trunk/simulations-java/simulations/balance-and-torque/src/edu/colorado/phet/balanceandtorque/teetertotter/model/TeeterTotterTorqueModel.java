// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Primary model class for the tab that depicts torque on a plank, a.k.a. a teeter totter.
 *
 * @author John Blanco
 */
public class TeeterTotterTorqueModel implements Resettable {
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double FULCRUM_HEIGHT = 1.2; // In meters.
    private static final double PLANK_HEIGHT = 0.75; // In meters.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // A list of all the masses in the model
    // TODO: I'm not sure that this is even needed, since we listen to all masses as soon as they are added.  Revisit later and decide.
    private final List<Mass> masses = new ArrayList<Mass>();

    // Listeners that are notified when a shape-based mass is added to the model
    private final ArrayList<VoidFunction1<Mass>> massAddedListeners = new ArrayList<VoidFunction1<Mass>>();

    // Listeners that are notified when a shape-based mass is removed from the model
    private final ArrayList<VoidFunction1<Mass>> massRemovedListeners = new ArrayList<VoidFunction1<Mass>>();

    // Fulcrum on which the plank pivots
    private final FulcrumAbovePlank fulcrum = new FulcrumAbovePlank( 1, FULCRUM_HEIGHT );

    // Support columns
    private final List<SupportColumn> supportColumns = new ArrayList<SupportColumn>() {{
        add( new SupportColumn( PLANK_HEIGHT, -Plank.getLength() * 0.4 ) );
        add( new SupportColumn( PLANK_HEIGHT, Plank.getLength() * 0.4 ) );
    }};

    // Property that controls whether the columns are supporting the plank.
    public final BooleanProperty supportColumnsActive = new BooleanProperty( true );

    // Plank upon which the various masses can be placed.
    private final Plank plank = new Plank( clock,
                                           new Point2D.Double( 0, PLANK_HEIGHT ),
                                           new Point2D.Double( 0, FULCRUM_HEIGHT ),
                                           supportColumnsActive );

    // Bar that attaches the fulcrum to the pivot point.
    private final AttachmentBar attachmentBar = new AttachmentBar( plank );

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    // TODO: The next block of code is for listening for masses being added and removed.  It seems bulky and repetitions, and feels like it could be simplified.
    // TODO: I know: I should replace this with an observable list of masses.
    public void addMassAddedListener( VoidFunction1<Mass> listener ) {
        massAddedListeners.add( listener );
    }

    public void removeMassAddedListener( VoidFunction1<Mass> listener ) {
        massAddedListeners.remove( listener );
    }

    public void addMassRemovedListener( VoidFunction1<Mass> listener ) {
        massRemovedListeners.add( listener );
    }

    public void removeMassRemovedListener( VoidFunction1<Mass> listener ) {
        massRemovedListeners.remove( listener );
    }

    // Adds a mass to the model and notifies registered listeners
    public UserMovableModelElement addMass( final Mass mass ) {
        mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( !userControlled ) {
                    // The user has dropped this mass.
                    if ( plank.isPointAbovePlank( mass.getPosition() ) ) {
                        // The mass was dropped above the plank, move it to a
                        // valid location on the plank.
                        plank.addMassToSurface( mass );
                    }
                    else {
                        // Not above the plank, so remove it from the model.
                        removeMass( mass );
                    }
                }
            }
        } );
        masses.add( mass );
        notifyMassAdded( mass );
        return mass;
    }

    private void notifyMassAdded( Mass mass ) {
        for ( VoidFunction1<Mass> massAddedListener : massAddedListeners ) {
            massAddedListener.apply( mass );
        }
    }

    // Removes a mass from the model and notifies listeners.
    public void removeMass( Mass mass ) {
        if ( masses.contains( mass ) ) {
            masses.remove( mass );
            for ( VoidFunction1<Mass> massRemovedListener : massRemovedListeners ) {
                massRemovedListener.apply( mass );
            }
        }
    }

    public FulcrumAbovePlank getFulcrum() {
        return fulcrum;
    }

    public Plank getPlank() {
        return plank;
    }

    public AttachmentBar getAttachmentBar() {
        return attachmentBar;
    }

    public List<SupportColumn> getSupportColumns() {
        return supportColumns;
    }

    public List<Mass> getMasses() {
        return masses;
    }

    public void reset() {
        getClock().resetSimulationTime();

        // Remove masses from the plank.
        plank.removeAllMasses();

        // Remove this model's references to the masses.
        for ( Mass mass : new ArrayList<Mass>( masses ) ) {
            removeMass( mass );
        }

        // Set the support columns to their initial state.
        supportColumnsActive.reset();
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}