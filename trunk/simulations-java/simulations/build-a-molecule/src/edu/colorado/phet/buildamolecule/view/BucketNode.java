package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Visual representation of a bucket of atoms (atoms are not contained here)
 */
public class BucketNode extends PNode {
    public BucketNode( Bucket bucket ) {
        final PhetPPath bucketBaseNode = new PhetPPath( new Rectangle( 0, 0, 150, 30 ), bucket.getColor() );
        addChild( bucketBaseNode );
        addChild( new PText( bucket.getName() ) {{
            setFont( new PhetFont( 18 ) );
            centerBoundsOnPoint( bucketBaseNode.getFullBounds().getCenterX(), bucketBaseNode.getFullBounds().getCenterY() );
        }} );
    }
}
