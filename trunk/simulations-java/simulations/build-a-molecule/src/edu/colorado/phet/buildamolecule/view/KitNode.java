package edu.colorado.phet.buildamolecule.view;

import java.awt.GridLayout;

import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitNode extends PNode {
    public KitNode( Kit kit, ModelViewTransform mvt ) {
        SwingLayoutNode container = new SwingLayoutNode( new GridLayout( 1, kit.getBuckets().size(), 20, 20 ) );

        for ( Bucket bucket : kit.getBuckets() ) {
            BucketView bucketView = new BucketView( bucket, mvt );
            container.addChild( bucketView.getHoleLayer() );
            container.addChild( bucketView.getContainerLayer() );
        }

        addChild( container );
    }
}
