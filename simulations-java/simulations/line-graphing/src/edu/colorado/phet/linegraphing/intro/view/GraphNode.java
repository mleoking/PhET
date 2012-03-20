// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Graph that can display zero or more lines.
 * One of the lines can be directly manipulated.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GraphNode extends PhetPNode {

    public GraphNode() {
        addChild( new PPath( new Rectangle2D.Double( 0, 0, 600, 600 )) ); //TODO placeholder for layout
    }
}
