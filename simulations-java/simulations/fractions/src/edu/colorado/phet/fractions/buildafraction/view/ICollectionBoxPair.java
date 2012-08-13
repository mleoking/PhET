// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Interface for a collection box combined with the target pattern.
 *
 * @author Sam Reid
 */
public interface ICollectionBoxPair {
    PNode getCollectionBoxNode();

    PNode getTargetNode();
}