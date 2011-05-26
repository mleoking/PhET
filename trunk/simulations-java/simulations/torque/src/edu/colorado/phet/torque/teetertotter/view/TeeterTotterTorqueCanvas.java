// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.torque.teetertotter.model.SupportColumn;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.torque.teetertotter.model.Weight;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class TeeterTotterTorqueCanvas extends PhetPCanvas {

    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;

    public TeeterTotterTorqueCanvas( final TeeterTotterTorqueModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  The test chamber is centered
        // at (0, 0) in model space, and this transform is set up to place
        // the chamber where we want it on the canvas.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.34 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.82 ) ),
                150 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        //Function for adding graphics for the weights to the canvas
        final VoidFunction1<Weight> addWeightNode = new VoidFunction1<Weight>() {
            public void apply( Weight weight ) {
                rootNode.addChild( new WeightNode( mvt, weight ) );
            }
        };

        // Add WeightNodes for any weights already in the model on startup
        for ( Weight weight : model.getWeights() ) {
            addWeightNode.apply( weight );
        }

        // Whenever a weight is added to the model, create a graphic for it
        model.addWeightAddedListener( addWeightNode );

        // Add graphics for the plank, fulcrum, and columns.
        rootNode.addChild( new FulcrumNode( mvt, model.getFulcrum() ) );
        rootNode.addChild( new PlankNode( mvt, model.getPlank() ) );
        for ( SupportColumn supportColumn : model.getSupportColumns() ) {
            rootNode.addChild( new SupportColumnNode( mvt, supportColumn, model.getSupportColumnsActiveProperty() ) );
        }

        // Add the button that will restore the columns if they have been
        // previously removed.
        // TODO: i18n
        final TextButtonNode button2 = new TextButtonNode( "Restore Columns", new PhetFont( 14 ) ) {{
            setBackground( Color.YELLOW );
            setOffset( mvt.modelToViewX( 2.5 ) - getFullBounds().width / 2, mvt.modelToViewY( -0.2 ) );
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    model.getSupportColumnsActiveProperty().set( true );
                }
            } );
        }};
        rootNode.addChild( button2 );

        // Only show the Restore Columns button when the columns are not active.
        model.getSupportColumnsActiveProperty().addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean supportColumnsActive ) {
                button2.setVisible( !supportColumnsActive );
            }
        } );
    }
}
