// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
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

    // Movable model objects.
    private final Brick brick;
    private final LeadBlock leadBlock;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public IntroModel() {

        // Add the main lab bench shelf.  The position is tweaked such that it
        // sits at the bottom of the play area.  The various parameters are
        // set so that the image works well with the model representation.
        shelfList.add( new Shelf( new Point2D.Double( -LAB_TABLE_WIDTH / 2, -0.115 ),
                                  LAB_TABLE_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_LONG,
                                  LAB_TABLE_WIDTH * 0.015,
                                  LAB_TABLE_WIDTH * 0.05,
                                  Math.PI / 2 ) );

        // Add the thermometer shelf.
        shelfList.add( new Shelf( new Point2D.Double( -0.2, 0.05 ),
                                  THERMOMETER_SHELF_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_SHORT,
                                  THERMOMETER_SHELF_WIDTH * 0.05,
                                  THERMOMETER_SHELF_WIDTH * 0.20,
                                  Math.PI / 2 ) );

        // Add and position the brick.
        brick = new Brick();
        brick.position.set( new Point2D.Double( -0.2, -0.1 ) );

        // Add and position the lead block.
        leadBlock = new LeadBlock();
        leadBlock.position.set( new Point2D.Double( -0.1, -0.1 ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

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
}
