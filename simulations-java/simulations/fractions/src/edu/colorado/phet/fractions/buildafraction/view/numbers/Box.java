// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Data structure for handling the whole, numerator and denominator parts in a FractionNode.  Parts are null until assigned and null if not attached.
 *
 * @author Sam Reid
 */
public class Box {
    public PhetPPath box;
    public NumberCardNode card;
    public NumberNode number;
    public PNode parent;
}