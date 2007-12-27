/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.BoreholeDrill;
import edu.umd.cs.piccolo.nodes.PImage;


public class BoreholeDrillNode extends AbstractToolNode {

    public BoreholeDrillNode( BoreholeDrill boreholeDrill ) {
        super( boreholeDrill );
        PImage imageNode = new PImage( GlaciersImages.BOREHOLE_DRILL );
        addChild( imageNode );
    }
}
