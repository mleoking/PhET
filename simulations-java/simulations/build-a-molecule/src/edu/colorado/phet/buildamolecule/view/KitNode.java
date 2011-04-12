package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitNode extends PNode {
    public KitNode( Kit kit ) {
        SwingLayoutNode container = new SwingLayoutNode( new GridLayout( 1, kit.getBuckets().size(), 20, 20 ) );

        for ( Bucket bucket : kit.getBuckets() ) {
            container.addChild( new BucketNode( bucket ) );
        }

        addChild( container );
    }
}
