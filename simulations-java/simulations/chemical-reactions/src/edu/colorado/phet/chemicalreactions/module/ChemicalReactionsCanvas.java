// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.module;

import java.awt.*;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsApplication;
import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.control.KitPanel;
import edu.colorado.phet.chemicalreactions.control.TimePanel;
import edu.colorado.phet.chemicalreactions.model.ChemicalReactionsModel;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.LayoutBounds;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.ReactionShape;
import edu.colorado.phet.chemicalreactions.view.KitView;
import edu.colorado.phet.chemicalreactions.view.MoleculeNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Base canvas for everything visual in the simulation
 */
public class ChemicalReactionsCanvas extends PhetPCanvas {

    private final PNode root = new PNode();

    private ChemicalReactionsModel model;

    public ChemicalReactionsCanvas( IClock clock ) {
        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, ChemicalReactionsConstants.STAGE_SIZE ) );

        setBackground( ChemicalReactionsConstants.CANVAS_BACKGROUND_COLOR );

        addWorldChild( root );

        final LayoutBounds layoutBounds = new LayoutBounds();

        model = new ChemicalReactionsModel( clock, layoutBounds );

        // background for the play area
        root.addChild( new PhetPPath( layoutBounds.getAvailablePlayAreaViewBounds(),
                                      ChemicalReactionsConstants.PLAY_AREA_BACKGROUND_COLOR,
                                      new BasicStroke( 3 ), Color.BLACK ) );

        root.addChild( new KitPanel( model.getKitCollection(), layoutBounds.getAvailableKitModelBounds() ) );
        root.addChild( new TimePanel( clock, layoutBounds ) );

        for ( Kit kit : model.getKitCollection().getKits() ) {
            final KitView kitView = new KitView( kit );

            root.addChild( kitView.getBottomLayer() );
            root.addChild( kitView.getAtomLayer() );
            root.addChild( kitView.getTopLayer() );
            root.addChild( kitView.getMetadataLayer() );
            root.addChild( kitView.getDebugOverlayLayer() );
        }

        /*---------------------------------------------------------------------------*
        * reaction shape debugging
        *----------------------------------------------------------------------------*/

        final PNode reactionShapeDebuggingNode = new PNode();
        root.addChild( reactionShapeDebuggingNode );
        ChemicalReactionsApplication.SHOW_DEBUG_REACTION_SHAPES.addObserver( new SimpleObserver() {
            public void update() {
                reactionShapeDebuggingNode.setVisible( ChemicalReactionsApplication.SHOW_DEBUG_REACTION_SHAPES.get() );
            }
        } );

        ReactionShape[] reactions = new ReactionShape[]{ReactionShape.H2_O2_TO_H2O, ReactionShape.H2_N2_TO_NH3, ReactionShape.H2_Cl2_TO_HCl};

//        final double xOffset = 600;
        final double xOffset = 600;
        final double yOffset = 400;
        final ImmutableVector2D offset = new ImmutableVector2D( -800, -800 );
        for ( int i = 0; i < reactions.length; i++ ) {
            final double y = i * yOffset;
            ReactionShape reactionShape = reactions[i];

            // left: reactant
            for ( final ReactionShape.MoleculeSpot spot : reactionShape.reactantSpots ) {
                reactionShapeDebuggingNode.addChild( new MoleculeNode( new Molecule( spot.shape ) {{
                    setAngle( (float) spot.rotation );
                    setPosition( spot.position.plus( 0, y ).plus( offset ) );
                }} ) );
            }

            // middle: product
            for ( final ReactionShape.MoleculeSpot spot : reactionShape.productSpots ) {
                reactionShapeDebuggingNode.addChild( new MoleculeNode( new Molecule( spot.shape ) {{
                    setAngle( (float) spot.rotation );
                    setPosition( spot.position.plus( new ImmutableVector2D( xOffset, y ).plus( offset ) ) );
                }} ) );
            }

            // right: both (overlapping)
            for ( final ReactionShape.MoleculeSpot spot : reactionShape.reactantSpots ) {
                reactionShapeDebuggingNode.addChild( new MoleculeNode( new Molecule( spot.shape ) {{
                    setAngle( (float) spot.rotation );
                    setPosition( spot.position.plus( xOffset * 2, y ).plus( offset ) );
                }} ) );
            }
            for ( final ReactionShape.MoleculeSpot spot : reactionShape.productSpots ) {
                reactionShapeDebuggingNode.addChild( new MoleculeNode( new Molecule( spot.shape ) {{
                    setAngle( (float) spot.rotation );
                    setPosition( spot.position.plus( new ImmutableVector2D( xOffset * 2, y ).plus( offset ) ) );
                }} ) {{
                    setTransparency( 0.5f );
                }} );
            }
        }
    }

    public ChemicalReactionsModel getModel() {
        return model;
    }
}
