// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;

/**
 * Primary model class for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class IntroModel {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Size of the lab table top.
    private static final double LAB_TABLE_WIDTH = 0.5; // In meters.

    // Size of the thermometer shelf.
    private static final double THERMOMETER_SHELF_WIDTH = 0.1; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Main model clock.
    protected final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of all shelves in the model.
    private final List<Shelf> shelfList = new ArrayList<Shelf>();

    // Burners.
    private final Burner leftBurner;
    private final Burner rightBurner;

    // Movable model objects.
    private final Brick brick;
    private final LeadBlock leadBlock;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public IntroModel() {

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the main lab bench shelf.  The center of the shelf is the point
        // (0, 0) in model space.
        shelfList.add( new Shelf( new Point2D.Double( -LAB_TABLE_WIDTH / 2, 0 ),
                                  LAB_TABLE_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_LONG,
                                  LAB_TABLE_WIDTH * 0.015,
                                  LAB_TABLE_WIDTH * 0.05,
                                  Math.PI / 2 ) );

        // Add the thermometer shelf.
        shelfList.add( new Shelf( new Point2D.Double( -0.2, 0.15 ),
                                  THERMOMETER_SHELF_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_SHORT,
                                  THERMOMETER_SHELF_WIDTH * 0.05,
                                  THERMOMETER_SHELF_WIDTH * 0.20,
                                  Math.PI / 2 ) );

        // Add the burners.
        leftBurner = new Burner( new Point2D.Double( 0.07, 0 ) );
        rightBurner = new Burner( new Point2D.Double( 0.17, 0 ) );

        // Add and position the brick.
        brick = new Brick();
        brick.position.set( new ImmutableVector2D( -0.2, 0 ) );

        // Add and position the lead block.
        leadBlock = new LeadBlock();
        leadBlock.position.set( new ImmutableVector2D( -0.1, 0 ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Update the state of the model.
     *
     * @param dt
     */
    private void stepInTime( double dt ) {

        // Cause any user-movable model elements that are not supported by a
        // surface to fall (or, in some cases, jump up) towards the nearest
        // supporting surface.
        for ( Block movableModelElement : Arrays.asList( leadBlock, brick ) ) {
            if ( !movableModelElement.userControlled.get() && movableModelElement.getParentSurface() == null && movableModelElement.position.get().getY() != 0 ) {
                double acceleration = -9.8; // meters/s*s
                double velocity = movableModelElement.verticalVelocity.get() + acceleration * dt;
                double proposedYPos = movableModelElement.position.get().getY() + velocity * dt;
                double minYPos = 0;
                ObservableProperty<HorizontalSurface> parentSurface = findHighestOverlappingSurface( movableModelElement.getBottomSurface(), movableModelElement );
                if ( parentSurface != null ) {
                    minYPos = parentSurface.get().yPos;

                    //Center the movableModelElement on its new parent
                    double targetX = parentSurface.get().getCenterX();
                    movableModelElement.setX( targetX );
                }
                if ( proposedYPos < minYPos ) {
                    proposedYPos = minYPos;
                    movableModelElement.verticalVelocity.set( 0.0 );
                    movableModelElement.setParentSurface( parentSurface );
                }
                else {
                    movableModelElement.verticalVelocity.set( velocity );
                }
                movableModelElement.position.set( new ImmutableVector2D( movableModelElement.position.get().getX(), proposedYPos ) );
            }
        }
    }

    public List<Shelf> getShelfList() {
        return shelfList;
    }

    public void reset() {
        // TODO.
    }

    public IClock getClock() {
        return clock;
    }

    public Brick getBrick() {
        return brick;
    }

    public LeadBlock getLeadBlock() {
        return leadBlock;
    }

    public Burner getLeftBurner() {
        return leftBurner;
    }

    public Burner getRightBurner() {
        return rightBurner;
    }

    private ObservableProperty<HorizontalSurface> findHighestOverlappingSurface( HorizontalSurface surface, Block element ) {
        ObservableProperty<HorizontalSurface> highestOverlappingSurface = null;
        RestingSurfaceOwner selected = null;
        for ( RestingSurfaceOwner restingSurfaceOwner : Arrays.asList( leftBurner, rightBurner, brick, leadBlock ) ) {
            System.out.println( "restingSurfaceOwner = " + restingSurfaceOwner + ". equals = " + ( restingSurfaceOwner == element ) );
            if ( restingSurfaceOwner != element && surface.overlapsWith( restingSurfaceOwner.getRestingSurface().get() ) ) {
                if ( highestOverlappingSurface == null || highestOverlappingSurface.get().yPos < restingSurfaceOwner.getRestingSurface().get().yPos ) {
                    highestOverlappingSurface = restingSurfaceOwner.getRestingSurface();
                    selected = restingSurfaceOwner;

                }
            }
        }
        System.out.println( "###############\n" +
                            "Finished method, element = " + element );
        System.out.println( "Returning: selected = " + selected + ", surface = " + highestOverlappingSurface );

        System.out.println( "leftBurner.getRestingSurface() = " + leftBurner.getRestingSurface() );
        System.out.println( "rightBurner.getRestingSurface() = " + rightBurner.getRestingSurface() );
        System.out.println( "brick.getRestingSurface() = " + brick.getRestingSurface() );
        System.out.println( "lead.getRestingSurface() = " + leadBlock.getRestingSurface() );

        System.out.println( "element.getBottomSurface() = " + element.getBottomSurface() );
        System.out.println( "leadBlock.getBottomSurface() = " + leadBlock.getBottomSurface() );
        System.out.println( "brick.getBottomSurface() = " + brick.getBottomSurface() );
        System.out.println( "############" );
        return highestOverlappingSurface;
    }
}
