package edu.colorado.phet.buildamolecule.view;

import java.awt.GridLayout;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitNode extends PNode {
    public KitNode( Kit kit ) {
        SwingLayoutNode container = new SwingLayoutNode( new GridLayout( 1, kit.getBuckets().size(), 20, 20 ) );

        for ( Bucket bucket : kit.getBuckets() ) {
            // TODO: We are creating a transform here, but it should be eventually passed in or obtained from the
            // canvas.
            container.addChild( new BucketNode( bucket, ModelViewTransform.createSinglePointScaleInvertedYMapping(
                    new Point2D.Double(), new Point2D.Double(), 1 ) ) );
        }

        addChild( container );
    }
}
