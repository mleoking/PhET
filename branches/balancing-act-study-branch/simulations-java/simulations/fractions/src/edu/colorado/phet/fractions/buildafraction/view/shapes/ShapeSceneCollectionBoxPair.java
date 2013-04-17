// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import lombok.Data;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.view.ICollectionBoxPair;
import edu.umd.cs.piccolo.PNode;

/**
 * Data structure for the scoring region.
 *
 * @author Sam Reid
 */
public @Data class ShapeSceneCollectionBoxPair implements ICollectionBoxPair {
    public final ShapeCollectionBoxNode collectionBoxNode;
    public final PNode targetNode;
    public final MixedFraction value;
}