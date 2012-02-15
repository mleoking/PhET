// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.Bucket;

/**
 * @author Sam Reid
 */
public interface SliceFactory {
    Bucket bucket();

    List<Pie> createEmptyPies( int i );

    List<Slice> createSlicesForBucket( int denominator, int numSlices );

    Slice createBucketSlice( int denominator );

    Slice createPieCell( int container, int cell, int denominator );
}