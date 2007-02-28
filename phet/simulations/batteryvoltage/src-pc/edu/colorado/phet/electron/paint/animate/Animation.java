package edu.colorado.phet.electron.paint.animate;

import edu.colorado.phet.electron.paint.Painter;

/*Can return new Painters, or just modify a single one...*/

public interface Animation {
    public int numFrames();

    public Painter frameAt( int i );
}
