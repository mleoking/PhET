// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
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
    public final ObservableList<Mass> massList = new ObservableList<Mass>();

    // Fulcrum on which the plank pivots
    private final FulcrumAbovePlank fulcrum = new FulcrumAbovePlank( 1, FULCRUM_HEIGHT );

    // Support columns
    private final List<SupportColumn> supportColumns = new ArrayList<SupportColumn>() {{
        // Note: These are positioned so that the closing window that is
        // placed on them (the red x) is between two snap-to points on the
        // plank that the they don't get blocked by force vectors.
        add( new SupportColumn( PLANK_HEIGHT, -1.5 ) );
        add( new SupportColumn( PLANK_HEIGHT, 1.5 ) );
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

    // Adds a mass to the model.
    public UserMovableModelElement addMass( final Mass mass ) {
        mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( !userControlled ) {
                    // The user has dropped this mass.
                    if ( !plank.addMassToSurface( mass ) ) {
                        // The attempt to add mass to surface of plank failed,
                        // probably because the area below the mass is full,
                        // or because the mass wasn't over the plank.
                        removeMass( mass );
                    }
                }
            }
        } );
        massList.add( mass );
        return mass;
    }

    // Removes a mass from the model.
    public void removeMass( Mass mass ) {
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
        supportColumnsActive.reset();
    }
}