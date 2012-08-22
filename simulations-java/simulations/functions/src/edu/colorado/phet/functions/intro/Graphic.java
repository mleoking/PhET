// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public @Data class Graphic {
    public final int numRotations;

    public Graphic rotateRight() { return new Graphic( ( numRotations + 1 ) % 4 ); }

    public PNode toNode() {
        return new PImage( BufferedImageUtils.getRotatedImage( BufferedImageUtils.multiScaleToWidth( Images.KEY, 60 ), -Math.PI / 2 + getNumRotations() * Math.PI / 2 ) );
    }
}