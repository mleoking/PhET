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
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.GraduatedWallGraphic;
import edu.colorado.phet.idealgas.view.HeavySpeciesGraphic;
import edu.colorado.phet.idealgas.view.LightSpeciesGraphic;
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
public class MovableWallsModule extends AdvancedModule implements PChemModel.Listener {

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
    private PhetShapeGraphic energyCurveGraphic;
    private Pump reactantsPump;
    private Pump productsPump;

    /**
     * @param clock
     */
    public MovableWallsModule( final AbstractClock clock ) {
        super( clock, "<html><center>Potential Energy<br>Surface</center></html>", new PChemModel( clock.getDt() ) );

        ControlPanel controlPanel = new ControlPanel( this );
        setControlPanel( controlPanel );
        controlPanel.add( new AdvancedIdealGasControlPanel( this,
                                                            SimStrings.get( "AdvancedModule.Particle_Type_A" ),
                                                            SimStrings.get( "AdvancedModule.Particle_Type_B" )));

        // Add a collision expert for the walls and particles
        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );

        // Create a pump for each side of the box
        reactantsPump = new Pump( this, getBox(), getPumpingEnergyStrategy() );
        reactantsPump.setDispersionAngle( 0, Math.PI * 2 );
        productsPump = new Pump( this, getBox(), getPumpingEnergyStrategy() );
        productsPump.setDispersionAngle( 0, Math.PI * 2 );

        createWalls();
        createCurve();
        createCurveAdjuster();

        // Make box non-resizable
//        getBoxGraphic().setIgnoreMouse( true );

        // Remove the Wiggle-me
        getApparatusPanel().removeGraphic( wiggleMeGraphic );

        // Remove the pump graphic
        removePumpGraphic();

        // Remove the mannequin graphic and the box door
        getApparatusPanel().removeGraphic( getPusher() );
        getApparatusPanel().removeGraphic( getBoxDoorGraphic() );

        // Hook the model up to the vertical wall so it will know when it has moved
        PChemModel pchemModel = (PChemModel)getModel();
        pchemModel.setVerticalWall( verticalWall );
        pchemModel.addListener( this );

        // Add counters for the number of particles on either side of the vertical wall
        addParticleCounters( SimStrings.get( "AdvancedModule.Particle_Type_A" ),
                             SimStrings.get( "AdvancedModule.Particle_Type_B" ) );

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
     * Listeners that will cause the curve to be recomputed when a wall moves
     */
    private void createCurveAdjuster() {
        Wall.ChangeListener listener = new Wall.ChangeListener() {
            public void wallChanged( Wall.ChangeEvent event ) {
                getApparatusPanel().removeGraphic( energyCurveGraphic );
                createCurve();
            }
        };
        verticalWall.addChangeListener( listener );
        leftFloor.addChangeListener( listener );
        rightFloor.addChangeListener( listener );
    }

    /**
     * Make the curve that coverse the walls
     * <pre>
     *                      p5 p6
     *                     /----\
     *                  p4 |    | p7
     *                     |    |
     *    p1               |p3  | p8
     *     |--------------/     \----------| p10
     *     |             p2      p9        |
     *     |                               |
     * p12 |-------------------------------| p11
     * </pre>
     */
    private void createCurve1() {
        DoubleGeneralPath energyCurve = new DoubleGeneralPath();
        Rectangle2D leftFloorBounds = leftFloor.getBounds();
        Rectangle2D rightFloorBounds = rightFloor.getBounds();
        Rectangle2D verticalWallBounds = verticalWall.getBounds();
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        double filletRadius = verticalWall.getBounds().getWidth() / 2;

        // Create path points (p<n>) and control points (c<n>)
        Point2D p1 = new Point2D.Double( leftFloorBounds.getMinX(), leftFloorBounds.getMinY() );
        Point2D p2 = new Point2D.Double( leftFloorBounds.getMaxX() - filletRadius, leftFloorBounds.getMinY() );
        Point2D c3 = new Point2D.Double( leftFloorBounds.getMaxX(), leftFloorBounds.getMinY() );
//        double p3y = Math.max( leftFloorBounds.getMinY() - filletRadius, (leftFloorBounds.getMinY() + verticalWallBounds.getMinY() ) / 2);
//        Point2D p3 = new Point2D.Double( leftFloorBounds.getMaxX(), p3y );
        Point2D p3 = new Point2D.Double( leftFloorBounds.getMaxX(), leftFloorBounds.getMinY() - filletRadius );
//        double p4y = Math.min( p3y, leftFloorBounds.getMinY() - filletRadius );
//        Point2D p4 = new Point2D.Double( verticalWallBounds.getMinX(), p4y );
        Point2D p4 = new Point2D.Double( verticalWallBounds.getMinX(), verticalWallBounds.getMinY() + filletRadius );
        Point2D c4 = new Point2D.Double( verticalWallBounds.getMinX(), verticalWallBounds.getMinY() );
        Point2D p5 = new Point2D.Double( verticalWallBounds.getMinX() + filletRadius, verticalWallBounds.getMinY() );
        Point2D c5 = new Point2D.Double( verticalWallBounds.getMinX(), verticalWallBounds.getMinY() );
        Point2D p6 = new Point2D.Double( verticalWallBounds.getMaxX() - filletRadius, verticalWallBounds.getMinY() );
        Point2D p7 = new Point2D.Double( verticalWallBounds.getMaxX(), verticalWallBounds.getMinY() + filletRadius );
        Point2D c7 = new Point2D.Double( verticalWallBounds.getMaxX(), verticalWallBounds.getMinY() );
        Point2D p8 = new Point2D.Double( rightFloorBounds.getMinX(), rightFloorBounds.getMinY() - filletRadius );
        Point2D c8 = new Point2D.Double( rightFloorBounds.getMinX(), rightFloorBounds.getMinY() );
        Point2D p9 = new Point2D.Double( rightFloorBounds.getMinX() + filletRadius, rightFloorBounds.getMinY() );
        Point2D c9 = new Point2D.Double( rightFloorBounds.getMinX(), rightFloorBounds.getMinY() );
        Point2D p10 = new Point2D.Double( rightFloorBounds.getMaxX(), rightFloorBounds.getMinY() );
        Point2D p11 = new Point2D.Double( boxBounds.getMaxX(), boxBounds.getMaxY() );
        Point2D p12 = new Point2D.Double( boxBounds.getMinX(), boxBounds.getMaxY() );

        energyCurve.moveTo( p1 );
        energyCurve.lineTo( p2 );
        energyCurve.quadTo( c3.getX(), c3.getY(), p3.getX(), p3.getY() );
        energyCurve.lineTo( p4 );
        energyCurve.quadTo( c5.getX(), c5.getY(), p5.getX(), p5.getY() );
        energyCurve.lineTo( p6 );
        energyCurve.quadTo( c7.getX(), c7.getY(), p7.getX(), p7.getY() );
        energyCurve.lineTo( p8 );
        energyCurve.quadTo( c9.getX(), c9.getY(), p9.getX(), p9.getY() );
        energyCurve.lineTo( p10 );
        energyCurve.lineTo( p11 );
        energyCurve.lineTo( p12 );
        energyCurve.closePath();

        Color borderColor = Color.cyan;
        Color fill = new Color( 255, 255, 255, 160 );
        energyCurveGraphic = new PhetShapeGraphic( getApparatusPanel(), energyCurve.getGeneralPath(),
                                                   fill, new BasicStroke( 2f ), borderColor );
        energyCurveGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        energyCurveGraphic.setIgnoreMouse( true );
        getApparatusPanel().addGraphic( energyCurveGraphic, s_verticalWallLayer + 10 );
    }


    /**
     * Make the curve that coverse the walls
     * <pre>
     *                       p5
     *                     /----\
     *                     |    |
     *                     |    |
     *    p1               |    |
     *     |--------------/     \----------| p10
     *     |             p2      p9        |
     *     |                               |
     * p12 |-------------------------------| p11
     * </pre>
     */
    private void createCurve() {
        DoubleGeneralPath energyCurve = new DoubleGeneralPath();
        Rectangle2D leftFloorBounds = leftFloor.getBounds();
        Rectangle2D rightFloorBounds = rightFloor.getBounds();
        Rectangle2D verticalWallBounds = verticalWall.getBounds();
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        double filletRadius = verticalWall.getBounds().getWidth() / 2;

        // Create path points (p<n>) and control points (c<n>)
        Point2D p1 = new Point2D.Double( leftFloorBounds.getMinX(), leftFloorBounds.getMinY() );
        Point2D p2 = new Point2D.Double( leftFloorBounds.getMaxX() - filletRadius, leftFloorBounds.getMinY() );
        Point2D p5 = new Point2D.Double( verticalWallBounds.getMinX() + verticalWallBounds.getWidth() / 2, verticalWallBounds.getMinY() );
        double dx = p5.getX() - p2.getX();
        Point2D c5A = new Point2D.Double( p2.getX() + dx * 3 /4, p2.getY() );
        Point2D c5B = new Point2D.Double( p5.getX() - dx / 2, p5.getY() );
        Point2D c5 = new Point2D.Double( verticalWallBounds.getMinX(), verticalWallBounds.getMinY() );
        Point2D p9 = new Point2D.Double( rightFloorBounds.getMinX() + filletRadius, rightFloorBounds.getMinY() );
        Point2D c9A = new Point2D.Double( p5.getX() + dx / 2, p5.getY() );
        Point2D c9B = new Point2D.Double( p9.getX() - dx * 3/4, p9.getY() );
        Point2D p10 = new Point2D.Double( rightFloorBounds.getMaxX(), rightFloorBounds.getMinY() );
        Point2D p11 = new Point2D.Double( boxBounds.getMaxX(), boxBounds.getMaxY() );
        Point2D p12 = new Point2D.Double( boxBounds.getMinX(), boxBounds.getMaxY() );

        energyCurve.moveTo( p1 );
        energyCurve.lineTo( p2 );
        energyCurve.curveTo( c5A.getX(), c5A.getY(), c5B.getX(), c5B.getY(), p5.getX(), p5.getY() );
        energyCurve.curveTo( c9A.getX(), c9A.getY(), c9B.getX(), c9B.getY(), p9.getX(), p9.getY() );
        energyCurve.lineTo( p10 );
//        energyCurve.lineTo( p11 );
//        energyCurve.lineTo( p12 );
//        energyCurve.closePath();

        Color borderColor = Color.cyan;
        Color fill = new Color( 255, 255, 255, 160 );
        energyCurveGraphic = new PhetShapeGraphic( getApparatusPanel(), energyCurve.getGeneralPath(),
                                                   new BasicStroke( 2f ), borderColor );
//        energyCurveGraphic = new PhetShapeGraphic( getApparatusPanel(), energyCurve.getGeneralPath(),
//                                                   fill, new BasicStroke( 2f ), borderColor );
        energyCurveGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        energyCurveGraphic.setIgnoreMouse( true );
        getApparatusPanel().addGraphic( energyCurveGraphic, s_verticalWallLayer + 10 );
    }


    /**
     *
     */
    private void createWalls() {

        Box2D box = super.getBox();

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
        WallGraphic verticalWallGraphic = new GraduatedWallGraphic( verticalWall, getApparatusPanel(),
                                                                 Color.gray, Color.black,
                                                                 WallGraphic.NONE );
        // Only the top edge of the vertical call should be movable
        verticalWallGraphic.setResizableEast( false );
        verticalWallGraphic.setResizableWest( false );
        verticalWallGraphic.setResizableSouth( false );
        verticalWallGraphic.setMovable( false );
        verticalWallGraphic.setIsResizable( true );
        verticalWall.addChangeListener( new LowerWallChangeListener() );

        // Use an invisible paint for the floors
        Color invisiblePaint = new Color( 0, 0, 0, 0 );

        // Create the left movable floor
        leftFloor = new Wall( new Rectangle2D.Double( box.getCorner1X(), box.getCorner2Y() - 60,
                                                      verticalWall.getBounds().getMinX() - box.getCorner1X(),
                                                      wallThickness ),
                              box.getBoundsInternal() );
        leftFloor.setFixupStrategy( new FloorFixupStrategy() );
        // Add a listener that will make the left floor stay attached to the left wall of the box
        getBox().addChangeListener( new BoxChangeListener() );
        WallGraphic leftFloorGraphic = new WallGraphic( leftFloor, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.NORTH_SOUTH );
        leftFloorGraphic.setMovable( true );
        leftFloorGraphic.setResizableEast( false );
        leftFloorGraphic.setResizableWest( false );
        leftFloorGraphic.setResizableNorth( false );
        leftFloorGraphic.setResizableSouth( false );
        leftFloorGraphic.setWallHighlightedByMouse( false );
        getModel().addModelElement( leftFloor );
        addGraphic( leftFloorGraphic, s_verticalWallLayer - 1 );

        // Create the right movable floor
        rightFloor = new Wall( new Rectangle2D.Double( verticalWall.getBounds().getMaxX(), box.getCorner2Y() - 40,
//                                                       box.getCorner2X() - verticalWall.getBounds().getMaxX(), 40 ),
                                                       box.getCorner2X() - verticalWall.getBounds().getMaxX(),
                                                       wallThickness ),
                               box.getBoundsInternal() );
        rightFloor.setFixupStrategy( new FloorFixupStrategy() );
        WallGraphic rightFloorGraphic = new WallGraphic( rightFloor, getApparatusPanel(),
                                                         Color.gray, Color.black,
                                                         WallGraphic.NORTH_SOUTH );
        rightFloorGraphic.setMovable( true );
        rightFloorGraphic.setResizableEast( false );
        rightFloorGraphic.setResizableWest( false );
        rightFloorGraphic.setResizableNorth( false );
        rightFloorGraphic.setResizableSouth( false );
        rightFloorGraphic.setWallHighlightedByMouse( false );
        getModel().addModelElement( rightFloor );
        addGraphic( rightFloorGraphic, s_verticalWallLayer - 1 );

        // Note that we have to add the vertical wall last. This overcomes a subtle problem in the sequencing of
        // collision detection that is virtually intractable otherewise. Trust me.
        getModel().addModelElement( verticalWall );
        addGraphic( verticalWallGraphic, s_verticalWallLayer );

        // Set the region for the walls
        setWallBounds();
    }

    /**
     * Sets the region of the various walls and the region of their movement based on
     * the region of the vertical wall
     */
    private void setWallBounds() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D verticalWallBounds = verticalWall.getBounds();
        leftFloor.setBounds( new Rectangle2D.Double( boxBounds.getMinX(),
                                                     leftFloor.getBounds().getMinY(),
                                                     verticalWallBounds.getMinX() - boxBounds.getMinX(),
                                                     leftFloor.getBounds().getHeight() ) );
        rightFloor.setBounds( new Rectangle2D.Double( verticalWallBounds.getMaxX(),
                                                      rightFloor.getBounds().getMinY(),
                                                      boxBounds.getMaxX() - verticalWallBounds.getMaxX(),
                                                      rightFloor.getBounds().getHeight() ) );
        leftFloor.setMovementBounds( new Rectangle2D.Double( boxBounds.getMinX(),
                                                             verticalWallBounds.getMinY(),
                                                             verticalWallBounds.getMinX() - boxBounds.getMinX(),
                                                             boxBounds.getMaxY() - verticalWallBounds.getMinY() ) );
        rightFloor.setMovementBounds( new Rectangle2D.Double( verticalWallBounds.getMaxX(),
                                                              verticalWallBounds.getMinY(),
                                                              boxBounds.getMaxX() - verticalWallBounds.getMaxX(),
                                                              boxBounds.getMaxY() - verticalWallBounds.getMinY() ) );
    }


    //----------------------------------------------------------------
    // Model manipulation
    //----------------------------------------------------------------

    public void pumpGasMolecules( int numMolecules, Class species ) {
        Point2D location = null;
        if( species == HeavySpecies.class ) {
            location = new Point2D.Double( ( leftFloor.getBounds().getMinX() + leftFloor.getBounds().getMaxX() ) / 2,
                                           leftFloor.getBounds().getMinY() - 15 );
            reactantsPump.pump( numMolecules, species, location );
        }
        if( species == LightSpecies.class ) {
            location = new Point2D.Double( ( rightFloor.getBounds().getMinX() + rightFloor.getBounds().getMaxX() ) / 2,
                                           rightFloor.getBounds().getMinY() - 15 );
            productsPump.pump( numMolecules, species, location );
        }
    }

    //----------------------------------------------------------------
    // Implementation of abstract methods
    //----------------------------------------------------------------
    public Pump[] getPumps() {
        return new Pump[] { getPump(), reactantsPump, productsPump };        
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

    private class BoxChangeListener implements Box2D.ChangeListener {
        public void boundsChanged( Box2D.ChangeEvent event ) {
            Rectangle2D oldBounds = leftFloor.getBounds();
            Box2D box = event.getBox2D();
            leftFloor.setMovementBounds( getBox().getBoundsInternal() );
            leftFloor.setBounds( new Rectangle2D.Double( box.getBoundsInternal().getMinX(), oldBounds.getMinY(),
                                                         oldBounds.getMaxX() - box.getBoundsInternal().getMinX(),
                                                         oldBounds.getHeight() ) );
        }

        public void isVolumeFixedChanged( Box2D.ChangeEvent event ) {
            // noop
        }
    }

    public void moleculeCreated( PChemModel.MoleculeCreationEvent event ) {
        PhetGraphic graphic = null;
        GasMolecule molecule = event.getMolecule();
        if( molecule instanceof HeavySpecies ) {
            graphic = new HeavySpeciesGraphic( this.getApparatusPanel(), molecule );
        }
        else if( molecule instanceof LightSpecies ) {
            graphic = new LightSpeciesGraphic( this.getApparatusPanel(), molecule );
        }
        if( graphic != null ) {
            addGraphic( graphic, IdealGasConfig.MOLECULE_LAYER );
        }
    }
}