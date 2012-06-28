// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view;

import java.awt.geom.AffineTransform;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class SceneNode extends PNode {
    public void reset() {
        setTransform( AffineTransform.getTranslateInstance( 0, 0 ) );
        removeAllChildren();
    }
}