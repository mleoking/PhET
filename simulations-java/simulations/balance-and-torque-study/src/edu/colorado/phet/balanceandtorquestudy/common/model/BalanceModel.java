// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorquestudy.common.BalanceAndTorqueSharedConstants;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Basic model for depicting masses on a balance, used as a base class for
 * several of the models.
 *
 * @author John Blanco
 */
public abstract class BalanceModel implements Resettable {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double FULCRUM_HEIGHT = 0.85; // In meters.
    private static final double PLANK_HEIGHT = 0.75; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    protected final ConstantDtClock clock = new ConstantDtClock( BalanceAndTorqueSharedConstants.FRAME_RATE );

    // A list of all the masses in the model
    public final ObservableList<Mass> massList = new ObservableList<Mass>();

    // Fulcrum on which the plank pivots
    private final FulcrumAbovePlank fulcrum = new FulcrumAbovePlank( 1, FULCRUM_HEIGHT );

    // Support columns
    private final List<LevelSupportColumn> supportColumns = new ArrayList<LevelSupportColumn>() {{
        // Note: These are positioned so that the closing window that is
        // placed on them (the red x) is between two snap-to points on the
        // plank that the they don't get blocked by force vectors.
        add( new LevelSupportColumn( PLANK_HEIGHT, -1.625 ) );
        add( new LevelSupportColumn( PLANK_HEIGHT, 1.625 ) );
    }};

    // Property that controls how many columns are supporting the plank.
    public final Property<ColumnState> columnState = new Property<ColumnState>( ColumnState.DOUBLE_COLUMNS );

    // Plank upon which the various masses can be placed.
    protected final Plank plank = new Plank( clock,
                                             new Point2D.Double( 0, PLANK_HEIGHT ),
                                             new Point2D.Double( 0, FULCRUM_HEIGHT ),
                                             columnState );

    // Bar that attaches the fulcrum to the pivot point.
    private final AttachmentBar attachmentBar = new AttachmentBar( plank );

    // Observer that turns on column support whenever a mass is user controlled.  Added for Stanford study.
    private VoidFunction1<Boolean> columnReEnabler = new VoidFunction1<Boolean>() {
        public void apply( Boolean userControlled ) {
            if ( userControlled ) {
                columnState.set( ColumnState.DOUBLE_COLUMNS );
            }
        }
    };


    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BalanceModel() {
        // Hook up the clock to step this model at each tick.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clock.getDt() );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    protected void stepInTime( double dt ) {
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            mass.stepInTime( dt );
        }
    }

    /**
     * Add a mass to the model.  Subclasses handle this somewhat differently.
     *
     * @param mass
     * @return
     */
    public void addMass( final Mass mass ) {
        massList.add( mass );
        mass.userControlled.addObserver( columnReEnabler );
    }

    /**
     * Removes a mass right from the mass list.
     *
     * @param mass
     */
    protected void removeMass( final Mass mass ) {
        massList.remove( mass );
        mass.userControlled.removeObserver( columnReEnabler );
    }

    /**
     * Reset method, most likely will need to be overriden in subclasses in
     * order to do the appropriate thing with the masses.
     */
    public void reset() {
        getClock().resetSimulationTime();

        // Remove masses from the plank.
        plank.removeAllMasses();

        // Set the support columns to their initial state.
        columnState.reset();
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

    public List<LevelSupportColumn> getSupportColumns() {
        return supportColumns;
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

}
