// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.signalcircuit.paint.animate;

import edu.colorado.phet.signalcircuit.paint.Painter;

/*Can return new Painters, or just modify a single one...*/

public interface Animation {
    public int numFrames();

    public Painter frameAt( int i );
}
