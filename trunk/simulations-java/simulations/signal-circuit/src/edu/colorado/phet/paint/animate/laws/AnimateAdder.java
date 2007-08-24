package edu.colorado.phet.paint.animate.laws;

import edu.colorado.phet.paint.LayeredPanel;
import edu.colorado.phet.phys2d.System2D;

public class AnimateAdder implements Runnable {
    int waitTime;
    AnimateLaw al;
    System2D sys;
    LayeredPanel p;
    int layer;

    public AnimateAdder( int waitTime, AnimateLaw al, System2D sys, LayeredPanel p, int layer ) {
        this.waitTime = waitTime;
        this.al = al;
        this.sys = sys;
        this.p = p;
        this.layer = layer;
    }

    public void run() {
        try {
            Thread.sleep( waitTime );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        sys.addLaw( al );
        p.addPainter( al, layer );
        //o.O.d("hello.");
    }
}
