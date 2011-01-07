/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JCheckBox;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.view.InteractiveSchematicAtomNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class BuildAnAtomCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    // Reset button.
    private final ResetAllButtonNode resetButtonNode;

    private final BooleanProperty viewOrbitals = new BooleanProperty( true );
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

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.30 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.45 ) ),
                2.0,
                true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        rootNode.addChild( new InteractiveSchematicAtomNode(model, mvt, viewOrbitals ));

        // Show the name of the element.
        ElementNameIndicator elementNameIndicator = new ElementNameIndicator( model.getAtom(), showName );
        // Position the name indicator above the nucleus
        elementNameIndicator.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) + elementNameIndicator.getFullBounds().getHeight() / 2 );
        rootNode.addChild( elementNameIndicator );

        // Show whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), showStableUnstable );
        // Position the stability indicator under the nucleus
        model.getAtom().addObserver( new SimpleObserver() {
            public void update() {
                stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2, mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) - stabilityIndicator.getFullBounds().getHeight() );
            }
        } );
        rootNode.addChild( stabilityIndicator );

        // Show the legend/particle count indicator in the top left.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom() );
        particleCountLegend.setOffset( 20, 10 );//top left corner, but with some padding
        rootNode.addChild( particleCountLegend );

        final PDimension windowSize = new PDimension( 400, 100 );//for the 3 lower windows
        final double verticalSpacingBetweenWindows = 12;
        int indicatorWindowPosX = 600;

        // Element indicator
        PDimension elementIndicatorNodeWindowSize = new PDimension( 400, 250 - verticalSpacingBetweenWindows * 2 );
        PeriodicTableNode elementIndicatorNode = new PeriodicTableNode( model.getAtom(), BuildAnAtomConstants.CANVAS_BACKGROUND );
        elementIndicatorWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_ELEMENT, elementIndicatorNodeWindowSize, elementIndicatorNode, true );
        elementIndicatorNode.setOffset( elementIndicatorNodeWindowSize.width / 2 - elementIndicatorNode.getFullBounds().getWidth() / 2, elementIndicatorNodeWindowSize.getHeight() / 2 - elementIndicatorNode.getFullBounds().getHeight() / 2 );
        elementIndicatorWindow.setOffset( indicatorWindowPosX, verticalSpacingBetweenWindows );
        elementIndicatorNode.translate( 0, 10 );//fudge factor since centering wasn't quite right
        rootNode.addChild( elementIndicatorWindow );

        // Symbol indicator
        SymbolIndicatorNode symbolNode = new SymbolIndicatorNode( model.getAtom(), false );
        symbolWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_SYMBOL, windowSize, symbolNode, true );
        //PDebug.debugBounds = true;//helps get the layout and bounds correct
        final double insetX = 20;
        symbolNode.setOffset( insetX, windowSize.height / 2 - symbolNode.getFullBounds().getHeight() / 2 );
        symbolWindow.setOffset( indicatorWindowPosX, 250 );
        rootNode.addChild( symbolWindow );

        // Mass indicator
        massWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_MASS_NUMBER, windowSize, new MassIndicatorNode( model.getAtom() ,viewOrbitals){{
            setOffset( insetX, windowSize.height / 2 - getFullBounds().getHeight() / 2 );
        }}, true );
        massWindow.setOffset( indicatorWindowPosX, symbolWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );
        rootNode.addChild( massWindow );

        // Charge indicator
        final ChargeIndicatorNode chargeIndicatorNode = new ChargeIndicatorNode( model.getAtom() );
        chargeWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_CHARGE, windowSize, chargeIndicatorNode, true );
        chargeIndicatorNode.setOffset( insetX, windowSize.height / 2 - chargeIndicatorNode.getFullBounds().getHeight() / 2 );
        chargeWindow.setOffset( indicatorWindowPosX, massWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );
        rootNode.addChild( chargeWindow );

        PSwing showNameCheckBox = createCheckBox( BuildAnAtomStrings.SHOW_ELEMENT_NAME, showName );
        showNameCheckBox.setOffset( chargeWindow.getFullBounds().getMinX(), chargeWindow.getFullBounds().getMaxY() + 5 );
        rootNode.addChild( showNameCheckBox );

        PSwing showNeutralIonCheckBox = createCheckBox( BuildAnAtomStrings.SHOW_NEUTRAL_ION, showNeutralIon );
        showNeutralIonCheckBox.setOffset( showNameCheckBox.getFullBounds().getX(), showNameCheckBox.getFullBounds().getMaxY() - 5 );
        rootNode.addChild( showNeutralIonCheckBox );

        PSwing showStableUnstableCheckBox = createCheckBox( BuildAnAtomStrings.SHOW_STABLE_UNSTABLE, showStableUnstable );
        showStableUnstableCheckBox.setOffset( showNeutralIonCheckBox.getFullBounds().getX(), showNeutralIonCheckBox.getFullBounds().getMaxY() - 5 );
        rootNode.addChild( showStableUnstableCheckBox );

        // "Reset All" button.
        resetButtonNode = new ResetAllButtonNode( model, this, 16, Color.BLACK, new Color( 255, 153, 0 ) );
        double desiredResetButtonWidth = 100;
        resetButtonNode.setScale( desiredResetButtonWidth / resetButtonNode.getFullBoundsReference().width );
        rootNode.addChild( resetButtonNode );

        double maxCheckboxX = Collections.max( Arrays.asList( showNameCheckBox.getFullBoundsReference().getMaxX(),
                                                              showNeutralIonCheckBox.getFullBoundsReference().getMaxX(),
                                                              showStableUnstableCheckBox.getFullBoundsReference().getMaxX()));
        resetButtonNode.setOffset(
                (maxCheckboxX + (chargeWindow.getFullBounds().getMaxX() - maxCheckboxX) / 2) - resetButtonNode.getFullBoundsReference().width / 2,
                showNeutralIonCheckBox.getFullBoundsReference().getCenterY() - resetButtonNode.getFullBoundsReference().height / 2 );

        //Add the Selection control for how to view the orbitals
        final OrbitalViewControl orbitalViewControl = new OrbitalViewControl( viewOrbitals );
        orbitalViewControl.setOffset( chargeWindow.getFullBounds().getMinX()-orbitalViewControl.getFullBounds().getWidth()-20, chargeWindow.getFullBounds().getY()-verticalSpacingBetweenWindows );
        rootNode.addChild( orbitalViewControl );

        final IonIndicatorNode ionIndicatorNode = new IonIndicatorNode( model.getAtom(), showNeutralIon, 175);
        ionIndicatorNode.setOffset( elementIndicatorWindow.getFullBounds().getMinX() - ionIndicatorNode.getFullBounds().getWidth() - 10, elementIndicatorWindow.getFullBounds().getCenterY() - ionIndicatorNode.getFullBounds().getHeight() / 2 );
        rootNode.addChild( ionIndicatorNode );

        //Make the "orbits" button not focused by default, by focusing the canvas
        setFocusable( true );
        requestFocus();

        // Minimize the indicator windows.  The layout needed to be done with
        // them maximized, which is why we waited until now.
        resetWindowMaximization();
    }

    private static PSwing createCheckBox( String label, final BooleanProperty value ) {
        //"Show Labels" button.
        PSwing showLabelsButton = new PSwing(new JCheckBox( label,value.getValue() ){{
            setFont( new PhetFont(16,true) );
            setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    value.setValue( isSelected() );
                }
            } );
            value.addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( value.getValue() );
                }
            } );
        }});
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

    public boolean getShowStableUnstable(){
        return showStableUnstable.getValue();
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

    public void reset() {
        resetWindowMaximization();
        viewOrbitals.reset();
        showName.reset();
        showNeutralIon.reset();
        showStableUnstable.reset();
    }
}
