/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.umd.cs.piccolo.nodes.PImage;


public class TracerFlagNode extends AbstractToolNode {

    public TracerFlagNode( TracerFlag tracerFlag ) {
        super( tracerFlag );
        PImage imageNode = new PImage( GlaciersImages.TRACER_FLAG );
        addChild( imageNode );
    }
}
