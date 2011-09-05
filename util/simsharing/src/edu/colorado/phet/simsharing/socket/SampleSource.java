// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.simsharing.Sample;

/**
 * @author Sam Reid
 */
public interface SampleSource {
    Pair<Sample, Integer> getSample( int index ) throws IOException, ClassNotFoundException;

}
