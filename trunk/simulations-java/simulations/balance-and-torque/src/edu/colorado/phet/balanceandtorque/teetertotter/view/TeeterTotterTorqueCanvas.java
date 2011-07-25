// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.SupportColumn;
import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
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

        // Set up the model-canvas transform.
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

        // Whenever a mass is added to the model, create a graphic for it
        model.addMassAddedListener( new VoidFunction1<Mass>() {
            public void apply( final Mass mass ) {
                PNode massNode = null;
                if ( mass instanceof ShapeMass ) {
                    // TODO: Always bricks right now, may have to change in the future.
                    massNode = new BrickStackNode( mvt, (ShapeMass) mass );
                }
                else if ( mass instanceof ImageMass ) {
                    massNode = new ImageModelElementNode( mvt, (ImageMass) mass );
                }
                // Add the removal listener for if and when this mass is removed from the model.
                final PNode finalMassNode = massNode;
                model.addMassRemovedListener( new VoidFunction1<Mass>() {
                    public void apply( Mass w ) {
                        if ( w == mass ) {
                            rootNode.removeChild( finalMassNode );
                        }
                    }
                } );
                rootNode.addChild( massNode );
            }
        } );

        // Add graphics for the plank, fulcrum, the attachment bar, and the columns.
        rootNode.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        rootNode.addChild( new PlankNode( mvt, model.getPlank() ) );
        rootNode.addChild( new AttachmentBarNode( mvt, model.getAttachmentBar() ) );
        for ( SupportColumn supportColumn : model.getSupportColumns() ) {
            rootNode.addChild( new SupportColumnNode( mvt, supportColumn, model.supportColumnsActive ) );
        }

        // Add the button that will restore the columns if they have been
        // previously removed.
        // TODO: i18n
        final TextButtonNode restoreColumnsButton = new TextButtonNode( "Restore Supports", new PhetFont( 14 ) ) {{
            setBackground( Color.YELLOW );
            setOffset( mvt.modelToViewX( 2.5 ) - getFullBounds().width / 2, mvt.modelToViewY( -0.2 ) );
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    model.supportColumnsActive.set( true );
                }
            } );
        }};
        rootNode.addChild( restoreColumnsButton );

        // Add the Reset All button.
        rootNode.addChild( new ResetAllButtonNode( model, this, 14, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            centerFullBoundsOnPoint( restoreColumnsButton.getFullBoundsReference().getCenterX(),
                                     restoreColumnsButton.getFullBoundsReference().getMaxY() + 30 );
            setConfirmationEnabled( false );
        }} );

        // Only show the Restore Columns button when the columns are not active.
        model.supportColumnsActive.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean supportColumnsActive ) {
                restoreColumnsButton.setVisible( !supportColumnsActive );
            }
        } );

        // Add the mass box, which is the place where the user will get the
        // objects that can be placed on the balance.
        rootNode.addChild( new MassBoxNode( model, mvt, this ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - 10, mvt.modelToViewY( 0 ) - getFullBoundsReference().height - 10 );
        }} );
    }
}
