/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereWallExpert;
import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.view.WallGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * MovableWallsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MovableWallsModule extends IdealGasModule {
    private Wall lowerWall;
    private Wall leftFloor;
    private Wall rightFloor;
    private int wallThickness = 12;

    public MovableWallsModule( AbstractClock clock ) {
        super( clock, "Movable Walls" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );

        Box2D box = super.getBox();

        // Create the lower vertical wall
        lowerWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + 100, box.getCorner1Y() + box.getHeight() / 3,
                                                      wallThickness, box.getHeight() * 2 / 3 ) );
        WallGraphic lowerWallGraphic = new WallGraphic( lowerWall, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.EAST_WEST,
                                                        box.getCorner1X(), box.getCorner2X() );
        getModel().addModelElement( lowerWall );
        addGraphic( lowerWallGraphic, 1000 );
        lowerWall.addChangeListener( new LowerWallChangeListener() );

        // Create the left movable floor
        leftFloor = new Wall( new Rectangle2D.Double( box.getCorner1X(), box.getCorner2Y() - 50,
                                                      lowerWall.getBounds().getMinX() - box.getCorner1X(), wallThickness ) );
        WallGraphic leftFloorGraphic = new WallGraphic( leftFloor, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.NORTH_SOUTH,
                                                        box.getCorner1Y(), box.getCorner2Y() );
        getModel().addModelElement( leftFloor );
        addGraphic( leftFloorGraphic, 1000 );

        // Create the right movable floor
        rightFloor = new Wall( new Rectangle2D.Double( lowerWall.getBounds().getMaxX(), box.getCorner2Y() - 50,
                                                       box.getCorner2X() - lowerWall.getBounds().getMaxX(), wallThickness ) );
        WallGraphic rightFloorGraphic = new WallGraphic( rightFloor, getApparatusPanel(),
                                                         Color.gray, Color.black,
                                                         WallGraphic.NORTH_SOUTH,
                                                         box.getCorner1Y(), box.getCorner2Y() );
        getModel().addModelElement( rightFloor );
        addGraphic( rightFloorGraphic, 1000 );

    }

    private class LowerWallChangeListener implements Wall.ChangeListener {
        public void wallChanged( Wall.ChangeEvent event ) {
            leftFloor.setMaxX( event.getWall().getBounds().getMinX() );
            rightFloor.setMinX( event.getWall().getBounds().getMaxX() );
        }
    }
}
