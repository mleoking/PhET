package edu.colorado.phet.rotation.graphs;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:22:10 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphComponent extends PNode {
    private String label;

    public GraphComponent( String label ) {
        this.label = label;
        PText text = new PText( label );
        addChild( text );
    }

    public String getLabel() {
        return label;
    }
}
