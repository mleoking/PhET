package edu.colorado.phet.signalcircuit.paint.animate.laws;

import edu.colorado.phet.signalcircuit.paint.LayeredPanel;
import edu.colorado.phet.signalcircuit.paint.Painter;
import edu.colorado.phet.signalcircuit.paint.animate.Animation;
import edu.colorado.phet.signalcircuit.phys2d.Law;
import edu.colorado.phet.signalcircuit.phys2d.System2D;

import java.awt.*;

public class AnimateLaw implements Law, Painter {
    double dt;
    Animation a;
    Painter current;
    LayeredPanel paintMe;
    double simulationTime = 0;
    int numFrames; //not protected if the user adds frames during running.
    int layer;

    public AnimateLaw( double dt, Animation a, LayeredPanel paintMe, int layer ) {
        this.layer = layer;
        this.numFrames = a.numFrames();
        this.paintMe = paintMe;
        this.dt = dt;//simulation time between frames.
        this.a = a;
        this.current = null;
    }

    public void paint( Graphics2D g ) {
        if( current != null ) {
            current.paint( g );
        }
    }

    public void iterate( double dx, System2D sys ) {
        //just see if we need a new frame.
        int frameA = (int)( simulationTime / dt );
        simulationTime += dx;
        int frameB = (int)( simulationTime / dt );
        //o.O.p("a="+frameA+", b="+frameB);
        if( frameA == frameB ) {
            return;
        }
        if( frameB > numFrames ) {
            sys.remove( this );
            paintMe.remove( this, layer );
        }
        else {
            if( frameB >= numFrames )//a.numFrames())
            {
                return;
            }
            current = a.frameAt( frameB );
            paintMe.repaint();
        }
    }
}
