/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import javax.swing.JCheckBox;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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

    // Model
    private final BuildAnAtomModel model;

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    // Layers on the canvas.
    private final PNode backLayer = new PNode();
    private final PNode particleLayer = new PNode();
    private final PNode frontLayer = new PNode();

    // Reset button.
    private final GradientButtonNode resetButtonNode;

    final BooleanProperty viewOrbitals = new BooleanProperty( true );
    final BooleanProperty showLabels = new BooleanProperty( true );
    private final MaximizeControlNode elementIndicatorWindow;
    private final MaximizeControlNode symbolWindow;
    private final MaximizeControlNode massWindow;
    private final MaximizeControlNode chargeWindow;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomCanvas( final BuildAnAtomModel model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        //        mvt = new ModelViewTransform2D( model.getModelViewport(),
        //                new Rectangle2D.Double( 0, BuildAnAtomDefaults.STAGE_SIZE.getHeight() * ( 1 - 0.8 ),
        //                BuildAnAtomDefaults.STAGE_SIZE.getWidth() * 0.7, BuildAnAtomDefaults.STAGE_SIZE.getHeight() * 0.7 ) );

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

        // Layers on the canvas.
        rootNode.addChild( backLayer );
        rootNode.addChild( particleLayer );
        rootNode.addChild( frontLayer );

        // Put up the bounds of the model.
        //        rootNode.addChild( new PhetPPath( mvt.createTransformedShape( model.getModelViewport() ), Color.PINK, new BasicStroke( 3f ), Color.BLACK ) );

        // Add the atom's nucleus location to the canvas.
        //        Shape nucleusOutlineShape = mvt.createTransformedShape( new Ellipse2D.Double(
        //                -model.getAtom().getNucleusRadius(),
        //                -model.getAtom().getNucleusRadius(),
        //                model.getAtom().getNucleusRadius() * 2,
        //                model.getAtom().getNucleusRadius() * 2 ) );
        //        PNode nucleusOutlineNode = new PhetPPath( nucleusOutlineShape, new BasicStroke(1f), Color.RED );
        //        backLayer.addChild( nucleusOutlineNode );
        DoubleGeneralPath nucleusXMarkerModelCoords = new DoubleGeneralPath();
        double xMarkerSize = Proton.RADIUS; // Arbitrary, adjust as desired.
        nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                model.getAtom().getPosition().getY() - xMarkerSize / 2 );
        nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                model.getAtom().getPosition().getY() + xMarkerSize / 2 );
        nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                model.getAtom().getPosition().getY() + xMarkerSize / 2 );
        nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                model.getAtom().getPosition().getY() - xMarkerSize / 2 );
        Shape nucleusXMarkerShape = mvt.createTransformedShape( nucleusXMarkerModelCoords.getGeneralPath() );
        PNode nucleusXMarkerNode = new PhetPPath( nucleusXMarkerShape, new BasicStroke( 4f ), new Color( 255, 0, 0, 75 ) );
        backLayer.addChild( nucleusXMarkerNode );

        // Add the atom's electron shells to the canvas.
        for ( ElectronShell electronShell : model.getAtom().getElectronShells() ) {
            backLayer.addChild( new ElectronShellNode( mvt, viewOrbitals, model.getAtom(), electronShell,true ) );
        }

        // Add the buckets that hold the sub-atomic particles.
        BucketNode electronBucketNode = new BucketNode( model.getElectronBucket(), mvt );
        electronBucketNode.setOffset( mvt.modelToViewDouble( model.getElectronBucket().getPosition() ) );
        backLayer.addChild( electronBucketNode.getHoleLayer() );
        frontLayer.addChild( electronBucketNode.getContainerLayer() );
        BucketNode protonBucketNode = new BucketNode( model.getProtonBucket(), mvt );
        protonBucketNode.setOffset( mvt.modelToViewDouble( model.getProtonBucket().getPosition() ) );
        backLayer.addChild( protonBucketNode.getHoleLayer() );
        frontLayer.addChild( protonBucketNode.getContainerLayer() );
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToViewDouble( model.getNeutronBucket().getPosition() ) );
        backLayer.addChild( neutronBucketNode.getHoleLayer() );
        frontLayer.addChild( neutronBucketNode.getContainerLayer() );

        // Add the subatomic particles.
        for ( int i = 0; i < model.numElectrons(); i++ ) {
            final int finalI = i;
            particleLayer.addChild( new ElectronNode( mvt, model.getElectron( i ) ){{
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() || !model.getAtom().containsElectron( model.getElectron( finalI ) ) );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
                updateVisibility.update();

                model.getAtom().addObserver( updateVisibility );
            }} );
        }

        for ( int i = 0; i < Math.max( model.numProtons(), model.numNeutrons() ); i++ ) {
            if ( i < model.numProtons() ) {
                particleLayer.addChild( new ProtonNode( mvt, model.getProton( i ) ) );
            }
            if ( i < model.numNeutrons() ) {
                particleLayer.addChild( new NeutronNode( mvt, model.getNeutron( i ) ) );
            }
        }

        // Show the name of the element.
        ElementNameIndicator elementNameIndicator = new ElementNameIndicator( model.getAtom(), showLabels );
        // Position the name indicator above the nucleus
        elementNameIndicator.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) + elementNameIndicator.getFullBounds().getHeight() / 2 );
        rootNode.addChild( elementNameIndicator );

        // Show whether the nucleus is stable.
        StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), showLabels );
        // Position the stability indicator under the nucleus
        stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2, mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) - stabilityIndicator.getFullBounds().getHeight() );
        rootNode.addChild( stabilityIndicator );

        // Show the legend/particle count indicator in the top left.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom() );
        particleCountLegend.setOffset( 20, 20 );//top left corner, but with some padding
        rootNode.addChild( particleCountLegend );

        final PDimension windowSize = new PDimension( 400, 100 );//for the 3 lower windows
        final double verticalSpacingBetweenWindows = 20;
        int indicatorWindowPosX = 600;

        // Element indicator
        PDimension elementIndicatorNodeWindowSize = new PDimension( 400, 250 - verticalSpacingBetweenWindows * 2 );
        ElementIndicatorNode elementIndicatorNode = new ElementIndicatorNode( model.getAtom() );
        elementIndicatorWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_ELEMENT, elementIndicatorNodeWindowSize, elementIndicatorNode, true );
        elementIndicatorNode.setOffset( elementIndicatorNodeWindowSize.width / 2 - elementIndicatorNode.getFullBounds().getWidth() / 2, elementIndicatorNodeWindowSize.getHeight() / 2 - elementIndicatorNode.getFullBounds().getHeight() / 2 );
        elementIndicatorWindow.setOffset( indicatorWindowPosX, verticalSpacingBetweenWindows );
        elementIndicatorNode.translate( 0, 10 );//fudge factor since centering wasn't quite right
        rootNode.addChild( elementIndicatorWindow );

        // Symbol indicator
        SymbolIndicatorNode symbolNode = new SymbolIndicatorNode( model.getAtom());
        symbolWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_SYMBOL, windowSize, symbolNode, true );
        //PDebug.debugBounds = true;//helps get the layout and bounds correct
        final double insetX = 20;
        symbolNode.setOffset( insetX, windowSize.height / 2 - symbolNode.getFullBounds().getHeight() / 2 );
        symbolWindow.setOffset( indicatorWindowPosX, 250 );
        rootNode.addChild( symbolWindow );

        // Mass indicator
        massWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_MASS, windowSize, new MassIndicatorNode( model.getAtom() ,viewOrbitals){{
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

        final int controlButtonOffset=30;//distance between "show labels" and "reset all" buttons
        //"Show Labels" button.
        PSwing showLabelsButton = new PSwing(new JCheckBox( BuildAnAtomStrings.SHOW_LABELS,showLabels.getValue() ){{
            setFont( new PhetFont(16,true) );
            setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    showLabels.setValue( isSelected() );
                }
            } );
            showLabels.addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( showLabels.getValue() );
                }
            } );
        }}){{
//COMPILE ERROR            setOffset( chargeWindow.getFullBounds().getCenterX()-getFullBounds().getWidth()-controlButtonOffset/2, chargeWindow.getFullBounds().getMaxY()+verticalSpacingBetweenWindows);
        }};
        rootNode.addChild( showLabelsButton );

        // "Reset All" button.
        resetButtonNode = new GradientButtonNode( BuildAnAtomStrings.RESET_ALL, 16, new Color( 255, 153, 0 ) );
        double desiredResetButtonWidth = 100;
        resetButtonNode.setScale( desiredResetButtonWidth / resetButtonNode.getFullBoundsReference().width );
        rootNode.addChild( resetButtonNode );
        resetButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BuildAnAtomCanvas.this.model.reset();
                reset();
            }
        } );
        resetButtonNode.setOffset(
                chargeWindow.getFullBounds().getCenterX() +controlButtonOffset/2 ,
                chargeWindow.getFullBounds().getMaxY() + verticalSpacingBetweenWindows );

        //Add the Selection control for how to view the orbitals
        rootNode.addChild( new OrbitalViewControl( viewOrbitals ){{
//COMPILE ERROR            setOffset( chargeWindow.getFullBounds().getMinX()-getFullBounds().getWidth()-20, chargeWindow.getFullBounds().getY()-verticalSpacingBetweenWindows );
        }} );

        rootNode.addChild( new IonIndicatorNode( model.getAtom(), showLabels ) {{
//COMPILE ERROR            setOffset( elementIndicatorWindow.getFullBounds().getMinX() - getFullBounds().getWidth() - 80, elementIndicatorWindow.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        //Make the "orbits" button not focused by default, by focusing the canvas
        setFocusable( true );
        requestFocus();

        //Start with the symbol, mass and charge windows minimized.
        //TODO: the reason we didn't put these values for maximized = false above is because the layout code depends on the maximized size for each component.
        //TODO: it would be nice to rewrite so that we can initialize values properly and still get the layout correct
        resetWindowMaximization();
    }

    private void resetWindowMaximization() {
        elementIndicatorWindow.setMaximized( true );
        symbolWindow.setMaximized( false );
        massWindow.setMaximized( false );
        chargeWindow.setMaximized( false );
    }
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

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
        showLabels.reset();
    }
}
