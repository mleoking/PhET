// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.energyformsandchanges.test;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
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
                2200 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Root node for adding other nodes.
        PNode rootNode = new PNode();
        canvas.getLayer().addChild( rootNode );

        // Add the container where the energy chunks will be held.  Change the
        // shape as needed for testing.
        Shape containerShape = new Rectangle2D.Double( -0.05, -0.05, 0.1, 0.1 );
        EnergyChunkContainerSlice energyChunkContainerSlice = new EnergyChunkContainerSlice( containerShape, 0, new Property<Vector2D>( new Vector2D( 0, 0 ) ) );
//        rootNode.addChild( new PhetPPath( mvt.modelToView( containerShape ), Color.PINK ) );
        rootNode.addChild( new EnergyChunkContainerSliceNode( energyChunkContainerSlice, mvt, true, Color.BLUE ) );

        // Add the energy chunks.
        for ( int i = 0; i < 10; i++ ){
            EnergyChunk energyChunk = new EnergyChunk( EnergyType.THERMAL, 0, 0, new BooleanProperty( true ) );
            energyChunkContainerSlice.addEnergyChunk( energyChunk );
            rootNode.addChild( new EnergyChunkNode( energyChunk, mvt ) );
        }

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );

        // Iterate the distribution algorithm.
        double dt = 1 / EFACConstants.FRAMES_PER_SECOND;
        double testDuration = 20; // In seconds.
        List<EnergyChunkContainerSlice> sliceList = new ArrayList<EnergyChunkContainerSlice>(  );
        sliceList.add( energyChunkContainerSlice );
        for ( int i = 0; i < testDuration / dt; i++){
            EnergyChunkDistributor.updatePositions( sliceList, dt );
            try {
                Thread.sleep( (int)Math.round( dt * 1000 ) );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

}
