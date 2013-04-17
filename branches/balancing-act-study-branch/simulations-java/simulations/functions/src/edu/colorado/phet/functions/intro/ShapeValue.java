// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;
import lombok.Data;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class ShapeValue {
    public final int numRotations;
    public final F<ShapeValue, PNode> node;

    public ShapeValue rotateRight() { return new ShapeValue( ( numRotations + 1 ) % 4, node ); }

    public PNode toNode() { return node.f( this ); }
}