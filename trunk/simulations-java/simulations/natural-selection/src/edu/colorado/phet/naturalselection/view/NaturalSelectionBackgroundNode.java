package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class NaturalSelectionBackgroundNode extends PNode {

    private boolean isEquator = true;
    private PImage equatorImage;
    private PImage arcticImage;

    public NaturalSelectionBackgroundNode() {
        equatorImage = NaturalSelectionResources.getImageNode( "natural_selection_background_equator_2.png" );
        arcticImage = NaturalSelectionResources.getImageNode( "natural_selection_background_arctic_2.png" );
        addChild( equatorImage );
    }

}
