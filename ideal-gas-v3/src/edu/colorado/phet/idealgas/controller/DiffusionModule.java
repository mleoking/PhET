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
public class DiffusionModule extends IdealGasModule {
    private Wall lowerWall;
    private Wall upperWall;
    private int wallThickness = 12;

    public DiffusionModule( AbstractClock clock ) {
        super( clock, "Diffusion" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );

        Box2D box = super.getBox();

        // Create the lower vertical wall
        lowerWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                      box.getCorner1Y() + box.getHeight() / 3,
                                                      wallThickness, box.getHeight() * 2 / 3 ),
                              box.getBoundsInternal() );
        WallGraphic lowerWallGraphic = new WallGraphic( lowerWall, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.EAST_WEST );
        lowerWallGraphic.setIsResizable( true );
        getModel().addModelElement( lowerWall );
        addGraphic( lowerWallGraphic, 1000 );
        lowerWall.addChangeListener( new LowerWallChangeListener() );

        // Create the upper vertical wall
        upperWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                      box.getCorner1Y(),
                                                      wallThickness, box.getHeight() / 4 ),
                              box.getBoundsInternal() );
        WallGraphic upperWallGraphic = new WallGraphic( upperWall, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.EAST_WEST );
        upperWallGraphic.setIsResizable( true );
        getModel().addModelElement( upperWall );
        addGraphic( upperWallGraphic, 1000 );
        upperWall.addChangeListener( new LowerWallChangeListener() );

        // Set the bounds for the walls
        setWallBounds();
    }

    /**
     * Sets the bounds of the various walls and the bounds of their movement based on
     * the bounds of the lower vertical wall
     */
    private void setWallBounds() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D lowerWallBounds = lowerWall.getBounds();

//        upper
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    private class LowerWallChangeListener implements Wall.ChangeListener {
        public void wallChanged( Wall.ChangeEvent event ) {
            setWallBounds();
        }
    }
}
