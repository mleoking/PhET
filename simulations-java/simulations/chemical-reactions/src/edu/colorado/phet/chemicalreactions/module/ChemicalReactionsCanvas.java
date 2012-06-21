// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.module;

import java.awt.*;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.control.KitPanel;
import edu.colorado.phet.chemicalreactions.control.TimePanel;
import edu.colorado.phet.chemicalreactions.model.ChemicalReactionsModel;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.LayoutBounds;
import edu.colorado.phet.chemicalreactions.view.KitView;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
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
            final KitView kitView = new KitView( kit, this );

            root.addChild( kitView.getBottomLayer() );
            root.addChild( kitView.getAtomLayer() );
            root.addChild( kitView.getTopLayer() );
            root.addChild( kitView.getMetadataLayer() );
            root.addChild( kitView.getDebugOverlayLayer() );
        }

//        for ( final ReactionShape.MoleculeSpot spot : ReactionShape.H2_O2_TO_H2O.reactantSpots ) {
//            root.addChild( new MoleculeNode( new Molecule( spot.shape ) {{
//                setAngle( (float) spot.rotation );
//                setPosition( spot.position );
//            }} ) );
//        }
    }

}
