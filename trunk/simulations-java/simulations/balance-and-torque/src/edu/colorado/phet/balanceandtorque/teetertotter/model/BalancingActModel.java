// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.common.BalanceAndTorqueSharedConstants;
import edu.colorado.phet.balanceandtorque.common.model.AttachmentBar;
import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.FulcrumAbovePlank;
import edu.colorado.phet.balanceandtorque.common.model.Plank;
import edu.colorado.phet.balanceandtorque.common.model.SupportColumn;
import edu.colorado.phet.balanceandtorque.common.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Primary model class for the tab that depicts torque on a plank, a.k.a. a
 * teeter totter.
 *
 * @author John Blanco
 */
public class BalancingActModel implements Resettable {
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double FULCRUM_HEIGHT = 0.85; // In meters.
    private static final double PLANK_HEIGHT = 0.75; // In meters.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( BalanceAndTorqueSharedConstants.FRAME_RATE );

    // A list of all the masses in the model
    public final ObservableList<Mass> massList = new ObservableList<Mass>();

    // Fulcrum on which the plank pivots
    private final FulcrumAbovePlank fulcrum = new FulcrumAbovePlank( 1, FULCRUM_HEIGHT );

    // Support columns
    private final List<SupportColumn> supportColumns = new ArrayList<SupportColumn>() {{
        // Note: These are positioned so that the closing window that is
        // placed on them (the red x) is between two snap-to points on the
        // plank that the they don't get blocked by force vectors.
        add( new SupportColumn( PLANK_HEIGHT, -1.625 ) );
        add( new SupportColumn( PLANK_HEIGHT, 1.625 ) );
    }};

    // Property that controls whether two, one or zero columns are supporting the plank.
    public final Property<ColumnState> columnState = new Property<ColumnState>( ColumnState.DOUBLE_COLUMNS );

    // Plank upon which the various masses can be placed.
    private final Plank plank = new Plank( clock,
                                           new Point2D.Double( 0, PLANK_HEIGHT ),
                                           new Point2D.Double( 0, FULCRUM_HEIGHT ),
                                           columnState );

    // Bar that attaches the fulcrum to the pivot point.
    private final AttachmentBar attachmentBar = new AttachmentBar( plank );

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public BalancingActModel() {
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clock.getDt() );
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    private void stepInTime( double dt ) {
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            mass.stepInTime( dt );
        }
    }

    // Adds a mass to the model.
    public UserMovableModelElement addMass( final Mass mass ) {
//        mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
//            public void apply( Boolean userControlled ) {
//                if ( !userControlled ) {
//                    // The user has dropped this mass.
//                    if ( !plank.addMassToSurface( mass ) ) {
//                        // The attempt to add mass to surface of plank failed,
//                        // probably because the area below the mass is full,
//                        // or because the mass wasn't over the plank.
//                        removeMassAnimated( mass );
//                    }
//                }
//            }
//        } );
        mass.userControlled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean newValue, Boolean oldValue ) {
                if ( newValue == false && oldValue == true ) {
                    // The user has dropped this mass.
                    if ( !plank.addMassToSurface( mass ) ) {
                        // The attempt to add mass to surface of plank failed,
                        // probably because the area below the mass is full,
                        // or because the mass wasn't over the plank.
                        removeMassAnimated( mass );
                    }
                }
            }
        } );
        massList.add( mass );
        return mass;
    }

    /**
     * Remove the mass from the model and animate its removal.
     *
     * @param mass
     */
    private void removeMassAnimated( final Mass mass ) {
        // Register a listener for the completion of the removal animation sequence.
        mass.addAnimationStateObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isAnimating, Boolean wasAnimating ) {
                if ( wasAnimating && !isAnimating ) {
                    // Animation sequence has completed.
                    mass.removeAnimationStateObserver( this );
                    massList.remove( mass );
                }
            }
        } );
        // Kick off the animation back to the tool box.
        mass.initiateAnimation();
    }

    /**
     * Removes a mass right away, with no animation.
     *
     * @param mass
     */
    private void removeMass( final Mass mass ) {
        massList.remove( mass );
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

    public void reset() {
        getClock().resetSimulationTime();

        // Remove masses from the plank.
        plank.removeAllMasses();

        // Remove this model's references to the masses.
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            removeMass( mass );
        }

        // Set the support columns to their initial state.
        columnState.reset();
    }
}