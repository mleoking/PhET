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
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
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
    private PhetShapeGraphic energyCurveGraphic;

    /**
     * @param clock
     */
    public MovableWallsModule( final AbstractClock clock ) {
        super( clock, "<html><center>Potential Energy<br>Surface</center></html>" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );
        final Box2D box = super.getBox();

        createWalls( box );

        createCurve();

        addWallListeners();

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
     * Listeners that will cause the curve to be recomputed when a wall moves
     */
    private void addWallListeners() {
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
        energyCurve.lineTo( p8);
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
     * @param box
     */
    private void createWalls( final Box2D box ) {
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
                                                        invisiblePaint, invisiblePaint,
//                                                        Color.gray, Color.black,
                                                        WallGraphic.NORTH_SOUTH );
        leftFloorGraphic.setWallHighlightedByMouse( false );
        getModel().addModelElement( leftFloor );
        addGraphic( leftFloorGraphic, s_verticalWallLayer - 1 );

        // Create the right movable floor
        rightFloor = new Wall( new Rectangle2D.Double( verticalWall.getBounds().getMaxX(), box.getCorner2Y() - 40,
//                                                       box.getCorner2X() - verticalWall.getBounds().getMaxX(), 40 ),
                                                       box.getCorner2X() - verticalWall.getBounds().getMaxX(), wallThickness ),
                               box.getBoundsInternal() );
        rightFloor.setFixupStrategy( new FloorFixupStrategy() );
        WallGraphic rightFloorGraphic = new WallGraphic( rightFloor, getApparatusPanel(),
                                                         invisiblePaint, invisiblePaint,
//                                                         Color.gray, Color.black,
                                                         WallGraphic.NORTH_SOUTH );
        rightFloorGraphic.setWallHighlightedByMouse( false );
        getModel().addModelElement( rightFloor );
        addGraphic( rightFloorGraphic, s_verticalWallLayer - 1 );

        // Note that we have to add the vertical wall last. This overcomes a subtle problem in the sequencing of
        // collision detection that is virtually intractable otherewise. Trust me.
        getModel().addModelElement( verticalWall );
        addGraphic( lowerWallGraphic, s_verticalWallLayer );

        // Set the region for the walls
        setWallBounds();
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