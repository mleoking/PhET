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

import edu.colorado.phet.collision.FloorFixupStrategy;
import edu.colorado.phet.collision.SphereWallExpert;
import edu.colorado.phet.collision.VerticalWallFixupStrategy;
import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.GraduatedWallGraphic;
import edu.colorado.phet.idealgas.view.WallGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * MovableWallsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MovableWallsModule extends AdvancedModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static double s_verticalWallLayer = 1000;


    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private Wall leftFloor;
    private Wall rightFloor;
    private int wallThickness = (int)GasMolecule.s_defaultRadius * 8;

    public MovableWallsModule( final AbstractClock clock ) {
        super( clock, "<html><center>Potential Energy<br>Surface</center></html>" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );
        final Box2D box = super.getBox();

        // Create the lower vertical wall
        verticalWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                         box.getCorner1Y() + box.getHeight() / 3,
                                                         wallThickness, box.getHeight() * 2 / 3 ),
                                 box.getBoundsInternal() );
        verticalWall.setMinimumWidth( wallThickness );
        verticalWall.setMovementBounds( new Rectangle2D.Double( box.getCorner1X() + wallThickness,
                                                                box.getCorner1Y(),
                                                                box.getWidth() - 2 * wallThickness,
                                                                box.getHeight() ) );
        verticalWall.setFixupStrategy( new VerticalWallFixupStrategy() );
        WallGraphic lowerWallGraphic = new GraduatedWallGraphic( verticalWall, getApparatusPanel(),
                                                                 Color.gray, Color.black,
                                                                 WallGraphic.EAST_WEST );
        lowerWallGraphic.setIsResizable( true );
        verticalWall.addChangeListener( new LowerWallChangeListener() );

        // Create the left movable floor
        leftFloor = new Wall( new Rectangle2D.Double( box.getCorner1X(), box.getCorner2Y() - 60,
                                                      verticalWall.getBounds().getMinX() - box.getCorner1X(), wallThickness ),
                              box.getBoundsInternal() );
        leftFloor.setFixupStrategy( new FloorFixupStrategy() );
        // Add a listener that will make the left floor stay attached to the left wall of the box
        getBox().addChangeListener( new BoxChangeListener() );
        WallGraphic leftFloorGraphic = new WallGraphic( leftFloor, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.NORTH_SOUTH );
        getModel().addModelElement( leftFloor );
        addGraphic( leftFloorGraphic, s_verticalWallLayer - 1 );

        // Create the right movable floor
        rightFloor = new Wall( new Rectangle2D.Double( verticalWall.getBounds().getMaxX(), box.getCorner2Y() - 40,
                                                       box.getCorner2X() - verticalWall.getBounds().getMaxX(), wallThickness ),
                               box.getBoundsInternal() );
        rightFloor.setFixupStrategy( new FloorFixupStrategy() );
        WallGraphic rightFloorGraphic = new WallGraphic( rightFloor, getApparatusPanel(),
                                                         Color.gray, Color.black,
                                                         WallGraphic.NORTH_SOUTH );
        getModel().addModelElement( rightFloor );
        addGraphic( rightFloorGraphic, s_verticalWallLayer - 1 );

        // Note that we have to add the vertical wall last. This overcomes a subtle problem in the sequencing of
        // collision detection that is virtually intractable otherewise. Trust me.
        getModel().addModelElement( verticalWall );
        addGraphic( lowerWallGraphic, s_verticalWallLayer );

        // Set the region for the walls
        setWallBounds();

        // Add counters for the number of particles on either side of the vertical wall
        addParticleCounters();


        JButton testButton = new JButton( "Test" );
//        getControlPanel().add( testButton);
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                HeavySpecies newMolecule = null;
                newMolecule = new HeavySpecies( new Point2D.Double( rightFloor.getBounds().getMinX() + 95,
                                                                    rightFloor.getBounds().getMinY() - 105 ),
                                                new Vector2D.Double( -200, 200 ),
                                                new Vector2D.Double() );
                new PumpMoleculeCmd( getIdealGasModel(), newMolecule, MovableWallsModule.this ).doIt();

            }
        } );


        JButton backupButton = new JButton( "Backup" );
//        getControlPanel().add( backupButton );
        backupButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModel().stepInTime( -clock.getDt() );
            }
        } );

    }

    /**
     * Sets the region of the various walls and the region of their movement based on
     * the region of the lower vertical wall
     */
    private void setWallBounds() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D lowerWallBounds = verticalWall.getBounds();
        leftFloor.setBounds( new Rectangle2D.Double( boxBounds.getMinX(),
                                                     leftFloor.getBounds().getMinY(),
                                                     lowerWallBounds.getMinX() - boxBounds.getMinX(),
                                                     leftFloor.getBounds().getHeight() ) );
        rightFloor.setBounds( new Rectangle2D.Double( lowerWallBounds.getMaxX(),
                                                      rightFloor.getBounds().getMinY(),
                                                      boxBounds.getMaxX() - lowerWallBounds.getMaxX(),
                                                      rightFloor.getBounds().getHeight() ) );
        leftFloor.setMovementBounds( new Rectangle2D.Double( boxBounds.getMinX(),
                                                             lowerWallBounds.getMinY(),
                                                             lowerWallBounds.getMinX() - boxBounds.getMinX(),
                                                             boxBounds.getMaxY() - lowerWallBounds.getMinY() ) );
        // Right floor can't go higher than the intake port on the box
        rightFloor.setMovementBounds( new Rectangle2D.Double( lowerWallBounds.getMaxX(),
                                                              Math.max( lowerWallBounds.getMinY(), Pump.s_intakePortY + 10 ),
                                                              boxBounds.getMaxX() - lowerWallBounds.getMaxX(),
                                                              boxBounds.getMaxY() - ( Pump.s_intakePortY + 10 ) ) );
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    private class LowerWallChangeListener implements Wall.ChangeListener {
        public void wallChanged( Wall.ChangeEvent event ) {
            setWallBounds();
            setParticleCounterRegions();
        }
    }


    //-----------------------------------------------------------------
    // Event handlers
    //-----------------------------------------------------------------

    private class BoxChangeListener implements Box2D.ChangeListener {
        public void boundsChanged( Box2D.ChangeEvent event ) {
            Rectangle2D oldBounds = leftFloor.getBounds();
            Box2D box = event.getBox2D();
            leftFloor.setMovementBounds( getBox().getBoundsInternal() );
            leftFloor.setBounds( new Rectangle2D.Double( box.getBoundsInternal().getMinX(), oldBounds.getMinY(),
                                                         oldBounds.getMaxX() - box.getBoundsInternal().getMinX(),
                                                         oldBounds.getHeight() ) );
        }
    }

}