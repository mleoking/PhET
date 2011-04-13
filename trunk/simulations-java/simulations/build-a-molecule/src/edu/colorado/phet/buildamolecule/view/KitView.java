// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitView {
    private PNode topLayer = new PNode();
    private PNode bottomLayer = new PNode();

    public KitView( Kit kit, ModelViewTransform mvt ) {
        for ( Bucket bucket : kit.getBuckets() ) {
            BucketView bucketView = new BucketView( bucket, mvt );

            topLayer.addChild( bucketView.getContainerLayer() );
            bottomLayer.addChild( bucketView.getHoleLayer() );
        }
    }

    public PNode getTopLayer() {
        return topLayer;
    }

    public PNode getBottomLayer() {
        return bottomLayer;
    }
}
