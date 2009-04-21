/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.PNode;

public class TestVanillaBunnyNode extends PNode {
    public TestVanillaBunnyNode() {
        addChild( NaturalSelectionResources.getImageNode( "bunny_2_white.png" ) );
    }
}
