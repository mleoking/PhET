// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.energyformsandchanges.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkNode;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkContainerSlice;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkContainerSliceNode;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkDistributor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Test bed for energy chunk distribution algorithms.
 *
 * @author John Blanco
 */
public class TestEnergyChunkDistribution {

    private static final int NUM_ENERGY_CHUNKS = 50;
    private static final double CONTAINER_WIDTH = 0.05; // In meters.

    /*
     * Main routine that constructs a PhET Piccolo canvas in a window.
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 500, 400 );
        PhetPCanvas canvas = new PhetPCanvas();

        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        // Set up the model-view transform.
        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                4000 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Root node for adding other nodes.
        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );

        // Add the container where the energy chunks will be held.  Change the
        // shape as needed for testing.
//        Shape containerShape = new Rectangle2D.Double( -CONTAINER_WIDTH / 2, -CONTAINER_WIDTH / 2, CONTAINER_WIDTH, CONTAINER_WIDTH );
        Shape containerShape = new Ellipse2D.Double( -CONTAINER_WIDTH / 2, -CONTAINER_WIDTH / 2 * 2, CONTAINER_WIDTH, CONTAINER_WIDTH * 2 );
        final EnergyChunkContainerSlice energyChunkContainerSlice = new EnergyChunkContainerSlice( containerShape, 0, new Property<Vector2D>( new Vector2D( 0, 0 ) ) );
        PNode sliceNode = new EnergyChunkContainerSliceNode( energyChunkContainerSlice, mvt, true, Color.BLUE );
        rootNode.addChild( sliceNode );

        // Add the energy chunks.
        for ( int i = 0; i < NUM_ENERGY_CHUNKS; i++ ) {
            EnergyChunk energyChunk = new EnergyChunk( EnergyType.THERMAL,
                                                       EnergyChunkDistributor.generateRandomLocation( energyChunkContainerSlice.getShape().getBounds2D() ),
                                                       new BooleanProperty( true ) );
            energyChunkContainerSlice.addEnergyChunk( energyChunk );
            rootNode.addChild( new EnergyChunkNode( energyChunk, mvt ) );
        }

        // Set up a timer to update the state.
        final double dt = 1 / EFACConstants.FRAMES_PER_SECOND;
        final List<EnergyChunkContainerSlice> sliceList = new ArrayList<EnergyChunkContainerSlice>();
        sliceList.add( energyChunkContainerSlice );
        Timer timer = new Timer( (int) Math.round( dt * 1000 ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                EnergyChunkDistributor.updatePositions( sliceList, dt );
            }
        } );
        timer.start();

        // Add a button that will reset the state.
        ResetAllButtonNode resetButton = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                for ( EnergyChunk ec : energyChunkContainerSlice.energyChunkList ) {
                    ec.setVelocity( 0, 0 );
                }
                randomizeEnergyChunkPositions( energyChunkContainerSlice );
            }
        }, canvas, 16, Color.BLACK, Color.ORANGE );
        resetButton.setConfirmationEnabled( false );
        resetButton.setOffset( sliceNode.getFullBoundsReference().getCenterX(), sliceNode.getFullBoundsReference().getMaxY() + 20 );
        rootNode.addChild( resetButton );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

    private static void randomizeEnergyChunkPositions( EnergyChunkContainerSlice energyChunkContainerSlice ) {
        for ( EnergyChunk ec : energyChunkContainerSlice.energyChunkList ) {
            ec.position.set( EnergyChunkDistributor.generateRandomLocation( energyChunkContainerSlice.getShape().getBounds2D() ) );
        }
    }
}
