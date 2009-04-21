/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.nodes.PImage;

public class ShrubNode extends NaturalSelectionSprite {

    public PImage shrubImage;

    public ShrubNode( double baseX, double baseY, double scale ) {
        shrubImage = NaturalSelectionResources.getImageNode( "shrub.png" );
        shrubImage.setOffset( 0, 15 );
        addChild( shrubImage );
        setSpriteLocation( baseX, 0, getInverseGroundZDepth( baseY ) );
        setScale( scale );
        setOffset( baseX - scale * shrubImage.getWidth() / 2, baseY - shrubImage.getHeight() * scale );
    }

}