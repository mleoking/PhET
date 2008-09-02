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

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.collision.SphereWallExpert;
import edu.colorado.phet.idealgas.collision.VerticalBarrier;
import edu.colorado.phet.idealgas.collision.VerticalWallFixupStrategy;
import edu.colorado.phet.idealgas.collision.Wall;
import edu.colorado.phet.idealgas.controller.command.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.model.*;
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
public class DiffusionModule extends AdvancedModule {
    private Wall upperWall;
    private int wallThickness = (int)GasMolecule.s_radius * 8;
    private double minimumWallSeparation = GasMolecule.s_radius * 2;

    /**
     * @param clock
     */
    public DiffusionModule( final SimulationClock clock ) {
        super( clock, "Diffusion" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );

        final Box2D box = super.getBox();

        ControlPanel controlPanel = new ControlPanel( this );
        setControlPanel( controlPanel );
        controlPanel.add( new AdvancedIdealGasControlPanel( this,
                                                            IdealGasResources.getString( "AdvancedModule.Particle_Type_A" ),
                                                            IdealGasResources.getString( "AdvancedModule.Particle_Type_A" ) ) );

        createWalls( box );

        JButton testButton = new JButton( "Test" );
//        getControlPanel().add( testButton );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                HeavySpecies newMolecule = new HeavySpecies( new Point2D.Double( 314,
//                                                                                 327 ),
//                                                             new Vector2D.Double( 00, -100 ),
//                                                             new Vector2D.Double() );
                HeavySpecies newMolecule = new HeavySpecies( new Point2D.Double( 324 + 70,
                                                                                 315 + 3 ),
                                                             new Vector2D.Double( -100, 0 ),
                                                             new Vector2D.Double() );
//                HeavySpecies newMolecule = new HeavySpecies( new Point2D.Double( box.getCorner1X() + 50,
//                                                                                 box.getCorner1Y() + 70),
//                                                             new Vector2D.Double( 100, 100 ),
//                                                             new Vector2D.Double( ) );
                new PumpMoleculeCmd( getIdealGasModel(), newMolecule, DiffusionModule.this ).doIt();

            }
        } );

        // Add the particle counters
        addParticleCounters( IdealGasResources.getString( "AdvancedModule.Particle_Type_A" ),
                             IdealGasResources.getString( "AdvancedModule.Particle_Type_A" ) );

        // Change title of control under the pump
        setPumpSelectorPanelTitle( IdealGasResources.getString( "IdealGasControlPanel.Pump_Particles" ) );

        // Remove the mannequin graphic and the box door
        getApparatusPanel().removeGraphic( getPusher() );
        getApparatusPanel().removeGraphic( getBoxDoorGraphic() );


        JButton backupButton = new JButton( "Backup" );
//        getControlPanel().add( backupButton );
        backupButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ( (IdealGasModel)getModel() ).stepInTime( -clock.getDt() );
            }
        } );
    }

    private void createWalls( final Box2D box ) {
        // Create the lower vertical wall
        verticalWall = new VerticalBarrier( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                                    box.getCorner1Y() + box.getHeight() * 2 / 3,
                                                                    wallThickness, box.getHeight() * 1 / 3 ),
                                            box.getBoundsInternal() );
        verticalWall.setFixupStrategy( new VerticalWallFixupStrategy() );
        verticalWall.setMinimumWidth( wallThickness );
        verticalWall.setMovementBounds( new Rectangle2D.Double( box.getCorner1X() + GasMolecule.s_radius * 8,
                                                                box.getCorner1Y() + GasMolecule.s_radius * 8,
                                                                box.getWidth() - GasMolecule.s_radius * 16,
                                                                box.getHeight() ) );
        WallGraphic verticalWallGraphic = new WallGraphic( verticalWall, getApparatusPanel(),
                                                           Color.gray, Color.black,
                                                           WallGraphic.EAST_WEST );
        verticalWallGraphic.setIsResizable( true );
        getModel().addModelElement( verticalWall );
        addGraphic( verticalWallGraphic, 1000 );
        verticalWall.addChangeListener( new WallChangeListener() );

        // Create the upper vertical wall
        upperWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                      box.getCorner1Y(),
                                                      wallThickness,
                                                      box.getHeight() - verticalWall.getBounds().getHeight() - minimumWallSeparation ),
                              box.getBoundsInternal() );
        upperWall.setFixupStrategy( new VerticalWallFixupStrategy() );
        upperWall.setMinimumWidth( wallThickness );
        upperWall.setMovementBounds( new Rectangle2D.Double( box.getCorner1X() + GasMolecule.s_radius * 8,
                                                             box.getCorner1Y(),
                                                             box.getWidth() - GasMolecule.s_radius * 16,
                                                             box.getHeight() - GasMolecule.s_radius * 8 ) );
        WallGraphic upperWallGraphic = new WallGraphic( upperWall, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.EAST_WEST );
        upperWallGraphic.setIsResizable( true );
        getModel().addModelElement( upperWall );
        addGraphic( upperWallGraphic, 1000 );
        upperWall.addChangeListener( new WallChangeListener() );
    }


    /**
     * Sets the bounds of the various walls and the bounds of their movement based on
     * the bounds of the lower vertical wall
     */
    private void setWallBounds( Wall wallChanged ) {

        if( wallChanged == verticalWall ) {
            double minY = upperWall.getBounds().getMaxY() + minimumWallSeparation;
            Rectangle2D oldBounds = verticalWall.getBounds();
            // Don't let the lower wall get too close to the upper one
            if( verticalWall.getBounds().getMinY() < minY ) {
                verticalWall.setBounds( new Rectangle2D.Double( oldBounds.getMinX(), minY,
                                                                oldBounds.getWidth(), oldBounds.getMaxY() - minY ) );
            }
            // Don't let the lower wall get too thin
            if( verticalWall.getBounds().getMinY() > verticalWall.getBounds().getMaxY() - minimumWallSeparation ) {
                verticalWall.setBounds( new Rectangle2D.Double( oldBounds.getMinX(), verticalWall.getBounds().getMaxY() - minimumWallSeparation,
                                                                oldBounds.getWidth(), minimumWallSeparation ) );
            }
        }

        if( wallChanged == upperWall ) {
            double maxY = verticalWall.getBounds().getMinY() - minimumWallSeparation;
            Rectangle2D oldBounds = upperWall.getBounds();
            // Don't let the upper wall get too close to the lower one
            if( upperWall.getBounds().getMaxY() > maxY ) {
                upperWall.setBounds( new Rectangle2D.Double( oldBounds.getMinX(), oldBounds.getMinY(),
                                                             oldBounds.getWidth(), maxY - oldBounds.getMinY() ) );
            }
            // Don't let the upper wall get too thin
            if( upperWall.getBounds().getMaxY() < upperWall.getBounds().getMinY() + minimumWallSeparation ) {
                upperWall.setBounds( new Rectangle2D.Double( oldBounds.getMinX(), oldBounds.getMinY(),
                                                             oldBounds.getWidth(), minimumWallSeparation ) );
            }
        }
    }

    //----------------------------------------------------------------
    // Implementation of abstract methods
    //----------------------------------------------------------------
    public Pump[] getPumps() {
        return new Pump[]{getPump()};
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    private class WallChangeListener implements Wall.ChangeListener {
        public void wallChanged( Wall.ChangeEvent event ) {
            setWallBounds( event.getWall() );
        }
    }
}
