package electron.paint.animate;

import electron.paint.Painter;

/*Can return new Painters, or just modify a single one...*/

public interface Animation {
    public int numFrames();

    public Painter frameAt( int i );
}
