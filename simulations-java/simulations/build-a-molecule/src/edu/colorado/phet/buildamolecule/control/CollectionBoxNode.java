package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

public class CollectionBoxNode extends PNode {
    public CollectionBoxNode() {
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 50 ), Color.BLACK ) );
    }
}
