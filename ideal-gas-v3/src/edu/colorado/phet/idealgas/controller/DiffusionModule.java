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
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
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
public class DiffusionModule extends IdealGasModule {
    private Wall lowerWall;
    private Wall upperWall;
    private int wallThickness = (int)GasMolecule.s_defaultRadius * 4;
    private double minimumWallSeparation = GasMolecule.s_defaultRadius * 4;

    public DiffusionModule( final AbstractClock clock ) {
        super( clock, "Diffusion" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );

        final Box2D box = super.getBox();

        // Create the lower vertical wall
        lowerWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                      box.getCorner1Y() + box.getHeight() * 2 / 3,
                                                      wallThickness, box.getHeight() * 1 / 3 ),
//                                                      wallThickness, box.getHeight() * 2 / 3 ),
                              box.getBoundsInternal() );
        lowerWall.setMinimumWidth( wallThickness );
        lowerWall.setMovementBounds( new Rectangle2D.Double( box.getCorner1X() + GasMolecule.s_defaultRadius * 8,
                                                             box.getCorner1Y() + GasMolecule.s_defaultRadius * 8,
                                                             box.getWidth() - GasMolecule.s_defaultRadius * 16,
                                                             box.getHeight() ) );
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
                                                      wallThickness,
                                                      box.getHeight() - lowerWall.getBounds().getHeight() - minimumWallSeparation ),
                              box.getBoundsInternal() );
        upperWall.setMinimumWidth( wallThickness );
        upperWall.setMovementBounds( new Rectangle2D.Double( box.getCorner1X() + GasMolecule.s_defaultRadius * 8,
                                                             box.getCorner1Y(),
                                                             box.getWidth() - GasMolecule.s_defaultRadius * 16,
                                                             box.getHeight() - GasMolecule.s_defaultRadius * 8 ) );
        WallGraphic upperWallGraphic = new WallGraphic( upperWall, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.EAST_WEST );
        upperWallGraphic.setIsResizable( true );
        getModel().addModelElement( upperWall );
        addGraphic( upperWallGraphic, 1000 );
        upperWall.addChangeListener( new LowerWallChangeListener() );


        JButton testButton = new JButton( "Test" );
        getControlPanel().add( testButton );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                HeavySpecies newMolecule = new HeavySpecies( new Point2D.Double( 314,
                                                                                 327 ),
                                                             new Vector2D.Double( 00, -100 ),
                                                             new Vector2D.Double() );
//                HeavySpecies newMolecule = new HeavySpecies( new Point2D.Double( box.getCorner1X() + 50,
//                                                                                 box.getCorner1Y() + 70),
//                                                             new Vector2D.Double( 100, 100 ),
//                                                             new Vector2D.Double( ) );
                new PumpMoleculeCmd( getIdealGasModel(), newMolecule, DiffusionModule.this ).doIt();

            }
        } );

        JButton backupButton = new JButton( "Backup" );
        getControlPanel().add( backupButton );
        backupButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModel().stepInTime( -clock.getDt() );
            }
        } );
    }


    /**
     * Sets the bounds of the various walls and the bounds of their movement based on
     * the bounds of the lower vertical wall
     */
    private void setWallBounds( Wall wallChanged ) {

        // Don't let the lower wall get too close to the upper one
        if( wallChanged == lowerWall ) {
            double minY = upperWall.getBounds().getMaxY() + minimumWallSeparation;
            if( lowerWall.getBounds().getMinY() < minY ) {
                Rectangle2D oldBounds = lowerWall.getBounds();
                lowerWall.setBounds( new Rectangle2D.Double( oldBounds.getMinX(), minY,
                                                             oldBounds.getWidth(), oldBounds.getMaxY() - minY ) );
            }
        }

        // Don't let the upper wall get too close to the lower one
        if( wallChanged == upperWall ) {
            double maxY = lowerWall.getBounds().getMinY() - minimumWallSeparation;
            if( upperWall.getBounds().getMaxY() > maxY ) {
                Rectangle2D oldBounds = upperWall.getBounds();
                upperWall.setBounds( new Rectangle2D.Double( oldBounds.getMinX(), oldBounds.getMinY(),
                                                             oldBounds.getWidth(), maxY - oldBounds.getMinY() ) );
            }
        }
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    private class LowerWallChangeListener implements Wall.ChangeListener {
        public void wallChanged( Wall.ChangeEvent event ) {
            setWallBounds( event.getWall() );
        }
    }
}
