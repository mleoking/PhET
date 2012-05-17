// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.awt.BasicStroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeBucket;
import edu.colorado.phet.chemicalreactions.module.ChemicalReactionsCanvas;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.*;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitView {
    private PNode topLayer = new PNode();
    private PNode metadataLayer = new PNode();
    private PNode atomLayer = new PNode();
    private PNode bottomLayer = new PNode();

    private final Kit kit;
    private final ChemicalReactionsCanvas canvas;

    private final Map<MoleculeBucket, MoleculeBucketNode> bucketMap = new HashMap<MoleculeBucket, MoleculeBucketNode>();

    // store the node-atom relationships
//    private Map<Atom, LabeledAtomNode> atomNodeMap = new HashMap<Atom, LabeledAtomNode>();

    public KitView( final Kit kit, ChemicalReactionsCanvas canvas ) {
        this.kit = kit;
        this.canvas = canvas;

        for ( MoleculeBucket bucket : kit.getBuckets() ) {
            MoleculeBucketNode bucketView = new MoleculeBucketNode( bucket );
            bucketMap.put( bucket, bucketView );

            topLayer.addChild( bucketView.getFrontNode() );
            bottomLayer.addChild( bucketView.getHoleNode() );

            for ( Molecule molecule : bucket.getMolecules() ) {
                atomLayer.addChild( new MoleculeNode( molecule ) );
            }
        }

        // pluses between reactants
        final List<MoleculeBucket> reactantBuckets = kit.getReactantBuckets();
        for ( int i = 1; i < reactantBuckets.size(); i++ ) {
            // get the space in between our reactants
            double leftX = bucketMap.get( reactantBuckets.get( i - 1 ) ).getFrontNode().getFullBounds().getMaxX();
            double rightX = bucketMap.get( reactantBuckets.get( i ) ).getFrontNode().getFullBounds().getMinX();

            // center of the plus
            final double centerX = ( leftX + rightX ) / 2;
            final double centerY = kit.getLayoutBounds().getAvailableKitViewBounds().getCenterY();

            // construct it from two areas
            bottomLayer.addChild( new PhetPPath( new Area() {{
                add( new Area( new Rectangle2D.Double( centerX - PLUS_VIEW_THICKNESS / 2, centerY - PLUS_VIEW_LENGTH / 2,
                                                       PLUS_VIEW_THICKNESS, PLUS_VIEW_LENGTH ) ) );
                add( new Area( new Rectangle2D.Double( centerX - PLUS_VIEW_LENGTH / 2, centerY - PLUS_VIEW_THICKNESS / 2,
                                                       PLUS_VIEW_LENGTH, PLUS_VIEW_THICKNESS ) ) );
            }}, PLUS_COLOR, new BasicStroke( 1 ), PLUS_BORDER_COLOR ) );
        }

        // update visibility based on the kit visibility
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                Boolean visible = kit.visible.get();
                topLayer.setVisible( visible );
                metadataLayer.setVisible( visible );
                atomLayer.setVisible( visible );
                bottomLayer.setVisible( visible );
            }
        } );
    }

    public PNode getTopLayer() {
        return topLayer;
    }

    public PNode getMetadataLayer() {
        return metadataLayer;
    }

    public PNode getAtomLayer() {
        return atomLayer;
    }

    public PNode getBottomLayer() {
        return bottomLayer;
    }
}
