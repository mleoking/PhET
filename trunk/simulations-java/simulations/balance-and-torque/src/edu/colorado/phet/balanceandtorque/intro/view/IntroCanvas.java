// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.intro.model.IntroModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.LabeledImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank.MassForceVector;
import edu.colorado.phet.balanceandtorque.teetertotter.model.SupportColumn;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.balanceandtorque.teetertotter.view.AttachmentBarNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.BrickStackNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.FulcrumAbovePlankNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.ImageMassNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.LabeledImageMassNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.LevelIndicatorNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.LevelSupportColumnNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.MysteryVectorNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.PlankNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.PositionedVectorNode;
import edu.colorado.phet.balanceandtorque.teetertotter.view.RotatingRulerNode;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * @author John Blanco
 */
public class IntroCanvas extends PhetPCanvas {

    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;

    public final BooleanProperty massLabelVisibilityProperty = new BooleanProperty( true );
    public final BooleanProperty distancesVisibleProperty = new BooleanProperty( false );
    public final BooleanProperty forceVectorsFromObjectsVisibleProperty = new BooleanProperty( false );
    public final BooleanProperty levelIndicatorVisibleProperty = new BooleanProperty( false );

    public IntroCanvas( final IntroModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.4 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.75 ) ),
                150 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        // Set up a layer for the non-mass model elements.
        final PNode nonMassLayer = new PNode();
        rootNode.addChild( nonMassLayer );

        // Set up a separate layer for the masses so that they will be out in
        // front of the other elements of the model.
        final PNode massesLayer = new PNode();
        rootNode.addChild( massesLayer );

        // Whenever a mass is added to the model, create a graphic for it.
        model.massList.addElementAddedObserver( new VoidFunction1<Mass>() {
            public void apply( Mass mass ) {
                // Create and add the view representation for this mass.
                PNode massNode = null;
                if ( mass instanceof ShapeMass ) {
                    // TODO: Always bricks right now, may have to change in the future.
                    massNode = new BrickStackNode( (ShapeMass) mass, mvt, IntroCanvas.this, massLabelVisibilityProperty );
                }
                else if ( mass instanceof LabeledImageMass ) {
                    // These are mystery objects.
                    massNode = new LabeledImageMassNode( mvt, (LabeledImageMass) mass, IntroCanvas.this, massLabelVisibilityProperty );
                }
                else if ( mass instanceof ImageMass ) {
                    massNode = new ImageMassNode( mvt, (ImageMass) mass, IntroCanvas.this, massLabelVisibilityProperty );
                }
                else {
                    System.out.println( getClass().getName() + " - Error: Unrecognized mass type." );
                    assert false;
                }
                massesLayer.addChild( massNode );
                // Add the removal listener for if and when this mass is removed from the model.
                final PNode finalMassNode = massNode;
                model.massList.addElementRemovedObserver( mass, new VoidFunction0() {
                    public void apply() {
                        rootNode.removeChild( finalMassNode );
                    }
                } );
            }
        } );

        // Add graphics for the plank, the fulcrum, the attachment bar, and the columns.
        nonMassLayer.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        nonMassLayer.addChild( new AttachmentBarNode( mvt, model.getAttachmentBar() ) );
        nonMassLayer.addChild( new PlankNode( mvt, model.getPlank(), this ) );
        for ( SupportColumn supportColumn : model.getSupportColumns() ) {
            nonMassLayer.addChild( new LevelSupportColumnNode( mvt, supportColumn, model.columnState ) );
        }

        // Add the ruler.
        nonMassLayer.addChild( new RotatingRulerNode( model.getPlank(), mvt, distancesVisibleProperty ) );

        // Add the level indicator node which will show whether the plank is balanced or not
        nonMassLayer.addChild( new LevelIndicatorNode( mvt, model.getPlank() ) {{
            levelIndicatorVisibleProperty.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );

        // Listen to the list of various vectors and manage their representations.
        model.getPlank().forceVectorList.addElementAddedObserver( new VoidFunction1<MassForceVector>() {
            public void apply( final MassForceVector addedMassForceVector ) {
                // Add a representation for the new vector.
                final PNode forceVectorNode;
                if ( addedMassForceVector.isObfuscated() ) {
                    forceVectorNode = new MysteryVectorNode( addedMassForceVector.forceVectorProperty, forceVectorsFromObjectsVisibleProperty, Color.WHITE, mvt );
                }
                else {
                    forceVectorNode = new PositionedVectorNode( addedMassForceVector.forceVectorProperty,
                                                                0.002,  // Scaling factor, chosen to make size reasonable.
                                                                forceVectorsFromObjectsVisibleProperty,
                                                                Color.WHITE,
                                                                mvt );
                }
                nonMassLayer.addChild( forceVectorNode );
                // Listen for removal of this vector and, if and when it is
                // removed, remove the corresponding representation.
                model.getPlank().forceVectorList.addElementRemovedObserver( new VoidFunction1<MassForceVector>() {
                    public void apply( MassForceVector removedMassForceVector ) {
                        if ( removedMassForceVector == addedMassForceVector ) {
                            nonMassLayer.removeChild( forceVectorNode );
                        }
                    }
                } );
            }
        } );

        // Add the button that will control whether or not the support columns
        // are in place.
        final TextButtonNode columnControlButton = new TextButtonNode( "", new PhetFont( 14 ) ) {{
            setBackground( Color.YELLOW );
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    model.columnState.set( model.columnState.get() == ColumnState.NONE ? ColumnState.DOUBLE_COLUMNS : ColumnState.NONE );
                }
            } );
            // Change the button back and forth from one that removes the
            // supports to one that restores the supports.
            final Point2D columnControlButtonLocation = new Point2D.Double( mvt.modelToViewX( 2.55 ), mvt.modelToViewY( -0.3 ) );
            model.columnState.addObserver( new VoidFunction1<ColumnState>() {
                public void apply( ColumnState columnState ) {
                    if ( columnState == ColumnState.DOUBLE_COLUMNS ) {
                        setText( BalanceAndTorqueResources.Strings.REMOVE_SUPPORTS );
                    }
                    else {
                        setText( BalanceAndTorqueResources.Strings.ADD_SUPPORTS );
                    }
                    centerFullBoundsOnPoint( columnControlButtonLocation.getX(), columnControlButtonLocation.getY() );
                }
            } );
        }};
        nonMassLayer.addChild( columnControlButton );

        // Add the control panel that will allow users to control the visibility
        // of the various indicators.
        PNode controlPanel = new ControlPanelNode( new SwingLayoutNode( new GridLayout( 5, 1 ) ) {{
            addChild( new PText( BalanceAndTorqueResources.Strings.SHOW ) {{
                setFont( new PhetFont( 18 ) );
            }} );
            addChild( new PropertyCheckBoxNode( BalanceAndTorqueResources.Strings.MASS_LABELS, massLabelVisibilityProperty ) );
            addChild( new PropertyCheckBoxNode( BalanceAndTorqueResources.Strings.DISTANCES, distancesVisibleProperty ) );
            addChild( new PropertyCheckBoxNode( BalanceAndTorqueResources.Strings.FORCES_FROM_OBJECTS, forceVectorsFromObjectsVisibleProperty ) );
            addChild( new PropertyCheckBoxNode( BalanceAndTorqueResources.Strings.LEVEL, levelIndicatorVisibleProperty ) );
        }} );
        controlPanel.setOffset( STAGE_SIZE.getWidth() - controlPanel.getFullBoundsReference().width - 20, 100 );
        nonMassLayer.addChild( controlPanel );

        // Add the Reset All button.
        nonMassLayer.addChild( new ResetAllButtonNode( model, this, 14, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            centerFullBoundsOnPoint( columnControlButton.getFullBoundsReference().getCenterX(),
                                     columnControlButton.getFullBoundsReference().getMaxY() + 30 );
            setConfirmationEnabled( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    // Reset properties that control vector visibility.
                    distancesVisibleProperty.reset();
                    forceVectorsFromObjectsVisibleProperty.reset();
                    massLabelVisibilityProperty.reset();
                    levelIndicatorVisibleProperty.reset();
                }
            } );
        }} );
    }

    // Convenience class for avoiding code duplication.
    private static class PropertyCheckBoxNode extends PNode {
        private PropertyCheckBoxNode( String text, BooleanProperty property ) {
            PropertyCheckBox checkBox = new PropertyCheckBox( text, property );
            checkBox.setFont( new PhetFont( 14 ) );
            addChild( new PSwing( checkBox ) );
        }
    }
}
