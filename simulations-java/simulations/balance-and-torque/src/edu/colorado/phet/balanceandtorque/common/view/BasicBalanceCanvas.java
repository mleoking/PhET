// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.balanceandtorque.balancelab.view.AttachmentBarNode;
import edu.colorado.phet.balanceandtorque.balancelab.view.MysteryVectorNode;
import edu.colorado.phet.balanceandtorque.balancelab.view.PositionedVectorNode;
import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.LevelSupportColumn;
import edu.colorado.phet.balanceandtorque.common.model.Plank.MassForceVector;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
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
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Strings.*;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserActions.removedMass;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.*;
import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Canvas that displays items in the balance model, primarily the balance
 * beam (i.e. the plank), the various masses, and a floating control panel
 * for controlling the visibility of labels, rulers, etc.
 *
 * @author John Blanco
 */
public abstract class BasicBalanceCanvas extends PhetPCanvas implements Resettable {

    protected final ModelViewTransform mvt;

    public final BooleanProperty massLabelVisibilityProperty = new BooleanProperty( true );
    public final BooleanProperty distancesVisibleProperty = new BooleanProperty( false );
    public final BooleanProperty forceVectorsFromObjectsVisibleProperty = new BooleanProperty( false );
    public final BooleanProperty levelIndicatorVisibleProperty = new BooleanProperty( false );
    protected PNode nonMassLayer;
    protected PNode controlPanel;

    public BasicBalanceCanvas( final BalanceModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( DEFAULT_STAGE_SIZE.getWidth() * 0.4 ), (int) Math.round( DEFAULT_STAGE_SIZE.getHeight() * 0.75 ) ),
                150 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        // Set up a layer for the non-mass model elements.
        nonMassLayer = new PNode();
        rootNode.addChild( nonMassLayer );

        // Set up a separate layer for the masses so that they will be out in
        // front of the other elements of the model.
        final PNode massesLayer = new PNode();
        rootNode.addChild( massesLayer );

        // Whenever a mass is added to the model, create a graphic for it.
        model.massList.addElementAddedObserver( new VoidFunction1<Mass>() {
            public void apply( final Mass mass ) {
                // Create and add the view representation for this mass.
                PNode massNode = MassNodeFactory.createMassNode( mass, massLabelVisibilityProperty, mvt, BasicBalanceCanvas.this );
                massesLayer.addChild( massNode );

                // Add the removal listener for if and when this mass is removed from the model.
                final PNode finalMassNode = massNode;
                model.massList.addElementRemovedObserver( mass, new VoidFunction0() {
                    public void apply() {
                        massesLayer.removeChild( finalMassNode );
                        // Send a sim sharing message that indicates that this
                        // interactive element was removed.
                        SimSharingManager.sendUserMessage( mass.getUserComponent(), mass.getUserComponentType(), removedMass );
                    }
                } );
            }
        } );

        // Add graphics for the plank, the fulcrum, the attachment bar, and the columns.
        nonMassLayer.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        nonMassLayer.addChild( new PlankNode( mvt, model.getPlank(), this ) );
        nonMassLayer.addChild( new AttachmentBarNode( mvt, model.getAttachmentBar() ) );
        for ( LevelSupportColumn supportColumn : model.getSupportColumns() ) {
            nonMassLayer.addChild( new LevelSupportColumnNode( mvt, supportColumn, model.columnState, true ) );
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
                    forceVectorNode = new MysteryVectorNode( addedMassForceVector.forceVectorProperty, forceVectorsFromObjectsVisibleProperty, mvt );
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

        // Add the buttons that will control whether or not the support columns
        // are in place.
        final TextButtonNode addSupportsButton = new TextButtonNode( BalanceAndTorqueResources.Strings.ADD_SUPPORTS, new PhetFont( 14 ) ) {{
            setUserComponent( BalanceAndTorqueSimSharing.UserComponents.addSupportsButton );
            setBackground( Color.YELLOW );
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    model.columnState.set( ColumnState.DOUBLE_COLUMNS );
                }
            } );
            final Point2D columnControlButtonLocation = new Point2D.Double( mvt.modelToViewX( 2.55 ), mvt.modelToViewY( -0.3 ) );
            centerFullBoundsOnPoint( columnControlButtonLocation.getX(), columnControlButtonLocation.getY() );
        }};
        nonMassLayer.addChild( addSupportsButton );

        final TextButtonNode removeSupportsButton = new TextButtonNode( BalanceAndTorqueResources.Strings.REMOVE_SUPPORTS, new PhetFont( 14 ) ) {{
            setUserComponent( BalanceAndTorqueSimSharing.UserComponents.removeSupportsButton );
            setBackground( Color.YELLOW );
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    model.columnState.set( ColumnState.NONE );
                }
            } );
            centerFullBoundsOnPoint( addSupportsButton.getFullBoundsReference().getCenterX(), addSupportsButton.getFullBoundsReference().getCenterY() );
        }};
        nonMassLayer.addChild( removeSupportsButton );

        // Control the support button visibility based on the model state.
        model.columnState.addObserver( new VoidFunction1<ColumnState>() {
            public void apply( ColumnState columnState ) {
                addSupportsButton.setVisible( columnState == ColumnState.NONE );
                removeSupportsButton.setVisible( columnState == ColumnState.DOUBLE_COLUMNS );
            }
        } );

        // Add the control panel that will allow users to control the visibility
        // of the various indicators.
        controlPanel = new ControlPanelNode( new SwingLayoutNode( new GridLayout( 5, 1 ) ) {{
            addChild( new PText( BalanceAndTorqueResources.Strings.SHOW ) {{
                setFont( new PhetFont( 18 ) );
            }} );
            addChild( new PropertyCheckBoxNode( massLabelsCheckBox, MASS_LABELS, massLabelVisibilityProperty ) );
            addChild( new PropertyCheckBoxNode( rulersCheckBox, RULERS, distancesVisibleProperty ) );
            addChild( new PropertyCheckBoxNode( forceFromObjectsCheckBox, FORCES_FROM_OBJECTS, forceVectorsFromObjectsVisibleProperty ) );
            addChild( new PropertyCheckBoxNode( levelCheckBox, LEVEL, levelIndicatorVisibleProperty ) );
        }} );
        controlPanel.setOffset( DEFAULT_STAGE_SIZE.getWidth() - controlPanel.getFullBoundsReference().width - 20, 100 );
        nonMassLayer.addChild( controlPanel );

        // Add the Reset All button.
        Resettable[] resettables = new Resettable[] { this, model };
        nonMassLayer.addChild( new ResetAllButtonNode( resettables, this, 14, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            centerFullBoundsOnPoint( addSupportsButton.getFullBoundsReference().getCenterX(),
                                     addSupportsButton.getFullBoundsReference().getMaxY() + 30 );
            setConfirmationEnabled( false );
        }} );
    }

    public void reset() {
        // Reset the various properties that control the visibility of various indicators.
        distancesVisibleProperty.reset();
        forceVectorsFromObjectsVisibleProperty.reset();
        massLabelVisibilityProperty.reset();
        levelIndicatorVisibleProperty.reset();
    }

    // Convenience class for check boxes, prevents code duplication.
    private static class PropertyCheckBoxNode extends PNode {
        private PropertyCheckBoxNode( IUserComponent userComponent, String text, BooleanProperty property ) {
            PropertyCheckBox checkBox = new PropertyCheckBox( userComponent, text, property );
            checkBox.setFont( new PhetFont( 14 ) );
            addChild( new PSwing( checkBox ) );
        }
    }
}
