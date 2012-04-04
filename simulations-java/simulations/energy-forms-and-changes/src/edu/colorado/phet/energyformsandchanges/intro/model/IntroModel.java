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

    // size of the lab table top.
    private static final double LAB_TABLE_WIDTH = 0.5; // In meters.


    // Main model clock.
    protected final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of all shelves in the model.
    private final List<Shelf> shelfList = new ArrayList<Shelf>();

    public IntroModel() {
        shelfList.add( new Shelf( new Point2D.Double( -LAB_TABLE_WIDTH / 2, 0 ),
                                  LAB_TABLE_WIDTH,
                                  EnergyFormsAndChangesResources.Images.SHELF_LONG,
                                  LAB_TABLE_WIDTH * 0.015,
                                  LAB_TABLE_WIDTH * 0.05,
                                  Math.PI / 2 ) );
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
}
