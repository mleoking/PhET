// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import lombok.Data;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class Graphic {
    public final int numRotations;
    public final F<Graphic, PNode> node;

    public Graphic rotateRight() { return new Graphic( ( numRotations + 1 ) % 4, node ); }

    public PNode toNode() { return node.f( this ); }
}