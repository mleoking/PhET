package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.nodes.PImage;

public class TreeNode extends NaturalSelectionSprite {

    public PImage treeImage;

    public TreeNode( double baseX, double baseY, double scale ) {
        treeImage = NaturalSelectionResources.getImageNode( "tree.png" );
        treeImage.setOffset( 0, 20 );
        addChild( treeImage );
        setSpriteLocation( baseX, 0, getInverseGroundZDepth( baseY ) );
        setScale( scale );
        setOffset( baseX - scale * treeImage.getWidth() / 2, baseY - treeImage.getHeight() * scale );
    }

}
