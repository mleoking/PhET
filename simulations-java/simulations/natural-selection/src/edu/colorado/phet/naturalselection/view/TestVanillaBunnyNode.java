package edu.colorado.phet.naturalselection.view;

import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class TestVanillaBunnyNode extends PNode {
    public TestVanillaBunnyNode() {
        addChild( NaturalSelectionResources.getImageNode( "bunny_2_white.png" ) );
    }
}
