package edu.colorado.phet.batteryvoltage.common.electron.paint.animate;

import java.util.Vector;

import edu.colorado.phet.batteryvoltage.common.electron.paint.CompositePainter;
import edu.colorado.phet.batteryvoltage.common.electron.paint.Painter;

public class Movie implements Animation {
    Vector v = new Vector();
    Vector indices = new Vector();

    public void addAnimation( Animation a, int startFrame ) {
        v.add( a );
        indices.add( new Integer( startFrame ) );
    }

    public void addAnimation( Animation a ) {
        addAnimation( a, 0 );
    }

    public Animation animationAt( int i ) {
        return (Animation) v.get( i );
    }

    public int getStartingFrame( int i ) {
        return ( (Integer) indices.get( i ) ).intValue();
    }

    public int numFrames() {
        int max = 0;
        for ( int i = 0; i < v.size(); i++ ) {
            Animation a = animationAt( i );
            int n = a.numFrames() + getStartingFrame( i );
            if ( n > max ) {
                max = n;
            }
        }
        return max;
    }

    public Painter frameAt( int f ) {
        CompositePainter cp = new CompositePainter();
        for ( int i = 0; i < v.size(); i++ ) {
            //o.O.p(""+i);
            Animation a = animationAt( i );
            int relFrame = f - getStartingFrame( i );
            if ( relFrame >= 0 && relFrame < a.numFrames() ) {
                //o.O.p("adding painter: "+a.frameAt(f));
                cp.addPainter( a.frameAt( relFrame ) );
            }
        }
        //o.O.d("Returning cp="+cp);
        return cp;
    }
}
