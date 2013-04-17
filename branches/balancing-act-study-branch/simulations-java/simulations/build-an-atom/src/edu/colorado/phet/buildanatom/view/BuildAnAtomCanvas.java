// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.*;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.view.InteractiveSchematicAtomNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class BuildAnAtomCanvas extends PhetPCanvas implements Resettable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final BuildAnAtomModel model;

    // Root node where all other nodes should be added.
    private final PNode rootNode;

    // Nodes used to create layering effect on the canvas.
    private final PNode particleLayer;
    private final PNode indicatorLayer;

    // Transform.
    private final ModelViewTransform mvt;

    // Variable needed to able to reset the canvas.
    private final Property<OrbitalView> orbitalViewProperty = new Property<OrbitalView>( OrbitalView.PARTICLES );
    private final BooleanProperty showName = new BooleanProperty( true );
    private final BooleanProperty showNeutralIon = new BooleanProperty( true );
    private final BooleanProperty showStableUnstable = new BooleanProperty( true );
    private final MaximizeControlNode elementIndicatorWindow;
    private final MaximizeControlNode symbolWindow;
    private final MaximizeControlNode massWindow;
    private final MaximizeControlNode chargeWindow;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public BuildAnAtomCanvas( final BuildAnAtomModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.45 ) ),
                2.0 );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Canvas layers.
        indicatorLayer = new PNode();
        rootNode.addChild( indicatorLayer );
        particleLayer = new PNode();
        rootNode.addChild( particleLayer );

        // Add the atom, which includes all of the sub-atomic particles and the buckets.
        particleLayer.addChild( new InteractiveSchematicAtomNode( model, mvt, this, orbitalViewProperty ) );

        // Show the name of the element.
        ElementNameIndicator elementNameIndicator = new ElementNameIndicator( model.getAtom(), showName, false );
        // Position the name indicator above the nucleus
        elementNameIndicator.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) + elementNameIndicator.getFullBounds().getHeight() / 2 );
        indicatorLayer.addChild( elementNameIndicator );

        // Show whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), showStableUnstable ) {{
            // Position the stability indicator under the nucleus
            setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS * 0.67 ) );
        }};
        indicatorLayer.addChild( stabilityIndicator );

        // Show the legend/particle count indicator in the top left.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom() );
        particleCountLegend.setOffset( 20, 10 );//top left corner, but with some padding
        indicatorLayer.addChild( particleCountLegend );

        final PDimension windowSize = new PDimension( 400, 100 );//for the 3 lower windows
        final double verticalSpacingBetweenWindows = 12;
        int indicatorWindowPosX = 600;

        // Element indicator
        PDimension elementIndicatorNodeWindowSize = new PDimension( 400, 250 - verticalSpacingBetweenWindows * 2 );
        PeriodicTableNode elementIndicatorNode = new HighlightingPeriodicTable( model.getAtom(), BuildAnAtomConstants.CANVAS_BACKGROUND );
        elementIndicatorWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_ELEMENT, elementIndicatorNodeWindowSize, elementIndicatorNode, true );
        elementIndicatorNode.setOffset( elementIndicatorNodeWindowSize.width / 2 - elementIndicatorNode.getFullBounds().getWidth() / 2, elementIndicatorNodeWindowSize.getHeight() / 2 - elementIndicatorNode.getFullBounds().getHeight() / 2 );
        elementIndicatorWindow.setOffset( indicatorWindowPosX, verticalSpacingBetweenWindows );
        elementIndicatorNode.translate( 0, 10 );//fudge factor since centering wasn't quite right
        indicatorLayer.addChild( elementIndicatorWindow );

        // Symbol indicator
        SymbolIndicatorNode symbolNode = new SymbolIndicatorNode( model.getAtom(), false );
        symbolWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_SYMBOL, windowSize, symbolNode, true );
        final double insetX = 20;
        symbolNode.setOffset( insetX, windowSize.height / 2 - symbolNode.getFullBounds().getHeight() / 2 );
        symbolWindow.setOffset( indicatorWindowPosX, 250 );
        indicatorLayer.addChild( symbolWindow );

        // Mass indicator
        massWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_MASS_NUMBER, windowSize, new MassIndicatorNode( model.getAtom(), orbitalViewProperty ) {{
            setOffset( insetX, windowSize.height / 2 - getFullBounds().getHeight() / 2 );
        }}, true );
        massWindow.setOffset( indicatorWindowPosX, symbolWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );
        indicatorLayer.addChild( massWindow );

        // Charge indicator
        final ChargeIndicatorNode chargeIndicatorNode = new ChargeIndicatorNode( model.getAtom() );
        chargeWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_CHARGE, windowSize, chargeIndicatorNode, true );
        chargeIndicatorNode.setOffset( insetX, windowSize.height / 2 - chargeIndicatorNode.getFullBounds().getHeight() / 2 );
        chargeWindow.setOffset( indicatorWindowPosX, massWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );
        indicatorLayer.addChild( chargeWindow );

        // Check boxes that control the visibility of the labels.
        PSwing showNameCheckBox = createCheckBox( BuildAnAtomStrings.SHOW_ELEMENT_NAME, showName );
        showNameCheckBox.setOffset( chargeWindow.getFullBounds().getMinX(), chargeWindow.getFullBounds().getMaxY() + 5 );
        indicatorLayer.addChild( showNameCheckBox );

        PSwing showNeutralIonCheckBox = createCheckBox( BuildAnAtomStrings.SHOW_NEUTRAL_ION, showNeutralIon );
        showNeutralIonCheckBox.setOffset( showNameCheckBox.getFullBounds().getX(), showNameCheckBox.getFullBounds().getMaxY() - 5 );
        indicatorLayer.addChild( showNeutralIonCheckBox );

        PSwing showStableUnstableCheckBox = createCheckBox( BuildAnAtomStrings.SHOW_STABLE_UNSTABLE, showStableUnstable );
        showStableUnstableCheckBox.setOffset( showNeutralIonCheckBox.getFullBounds().getX(), showNeutralIonCheckBox.getFullBounds().getMaxY() - 5 );
        indicatorLayer.addChild( showStableUnstableCheckBox );

        // "Reset Atom" button. TODO: Keep?  If so, needs i18n.
        TextButtonNode resetAtomButtonNode = new TextButtonNode( "Reset Atom", new PhetFont( 19 ), new Color( 0, 255, 127 ) );
        resetAtomButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.reset();
            }
        } );
        resetAtomButtonNode.setOffset( chargeWindow.getFullBoundsReference().getMaxX() - resetAtomButtonNode.getFullBoundsReference().width - 20,
                                       showNameCheckBox.getFullBoundsReference().getY() + 5 );
        indicatorLayer.addChild( resetAtomButtonNode );

        // "Reset All" button. TODO: Font size increased from 16 to support touch testing.
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( this, this, 19, Color.BLACK, new Color( 255, 153, 0 ) );
        resetAllButtonNode.setConfirmationEnabled( false );
        indicatorLayer.addChild( resetAllButtonNode );

        resetAllButtonNode.setOffset( resetAtomButtonNode.getFullBoundsReference().getCenterX() - resetAllButtonNode.getFullBoundsReference().width / 2,
                                   resetAtomButtonNode.getFullBoundsReference().getMaxY() + 10 );

        // Add the Selection control for how to view the orbitals
        final OrbitalViewControl orbitalViewControl = new OrbitalViewControl( orbitalViewProperty );
        orbitalViewControl.setOffset( chargeWindow.getFullBounds().getMinX() - orbitalViewControl.getFullBounds().getWidth() - 20, chargeWindow.getFullBounds().getY() - verticalSpacingBetweenWindows );
        indicatorLayer.addChild( orbitalViewControl );

        // Add the indicator for whether the atom is neutral or an ion.
        final IonIndicatorNode ionIndicatorNode = new IonIndicatorNode( model.getAtom(), showNeutralIon, 175 );
        ionIndicatorNode.setOffset( elementIndicatorWindow.getFullBounds().getMinX() - ionIndicatorNode.getFullBounds().getWidth() - 10, elementIndicatorWindow.getFullBounds().getCenterY() - ionIndicatorNode.getFullBounds().getHeight() / 2 );
        indicatorLayer.addChild( ionIndicatorNode );

        // Make the "orbits" button not focused by default, by focusing on the canvas.
        setFocusable( true );
        requestFocus();

        // Minimize the indicator windows.  The layout needed to be done with
        // them maximized, which is why we waited until now.
        resetWindowMaximization();
    }

    private static PSwing createCheckBox( String label, final BooleanProperty value ) {
        //"Show Labels" button.
        PSwing showLabelsButton = new PSwing( new JCheckBox( label, value.get() ) {{
            setFont( new PhetFont( 16, true ) );
            setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    value.set( isSelected() );
                }
            } );
            value.addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( value.get() );
                }
            } );
        }} );
        return showLabelsButton;
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    private void resetWindowMaximization() {
        elementIndicatorWindow.setMaximized( true );
        symbolWindow.setMaximized( false );
        massWindow.setMaximized( false );
        chargeWindow.setMaximized( false );
    }

    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }

    /**
     * Reset the canvas and the model.
     */
    public void reset() {
        // Reset the model.  Since the model is reset here, take care not to
        // hook this to some reset notification from the model, or undesirable
        // recursion will result.
        model.reset();

        // Reset the view.
        resetWindowMaximization();
        orbitalViewProperty.reset();
        showName.reset();
        showNeutralIon.reset();
        showStableUnstable.reset();
    }
}
