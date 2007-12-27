/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.umd.cs.piccolo.nodes.PImage;


public class IceThicknessToolNode extends AbstractToolNode {

    public IceThicknessToolNode( IceThicknessTool iceThicknessTool ) {
        super( iceThicknessTool );
        PImage imageNode = new PImage( GlaciersImages.ICE_THICKNESS_TOOL );
        addChild( imageNode );
    }
}
