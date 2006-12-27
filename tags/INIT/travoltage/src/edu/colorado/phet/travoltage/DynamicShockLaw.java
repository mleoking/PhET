package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.ParticleContainer;
import edu.colorado.phet.common.gui.Painter;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.travoltage.rotate.AngleListener;
import edu.colorado.phet.travoltage.rotate.Finger;
import edu.colorado.phet.travoltage.rotate.RotatingImage;

import java.applet.AudioClip;
import java.awt.*;
import java.util.Random;
import java.util.Vector;

/**
 * Uses the edu.colorado.phet.common count to determine the distance at which electrons are released.
 */
public class DynamicShockLaw implements AngleListener, ParticleContainer, Painter {
    Vector electrons;
    int doorknobX;
    int doorknobY;
    RotatingImage arm;
    GoToElbow gte;
    boolean paintSpark;
    Spark s;
    int maxSparkPoints;
    //AudioClip[]clips;
    AudioProxy ap;
    static final Random random = new Random();

    public DynamicShockLaw( RotatingImage arm, int doorknobX, int doorknobY, GoToElbow gte, Spark s, int maxSparkPoints, AudioClip[] clips ) {
        this.ap = new AudioProxy( clips, 1000 );
        //this.clips=clips;
        this.maxSparkPoints = maxSparkPoints;
        this.s = s;
        this.arm = arm;
        this.doorknobX = doorknobX;
        this.doorknobY = doorknobY;
        electrons = new Vector();
        this.gte = gte;
    }

    public int numParticles() {
        return electrons.size();
    }

    public Particle particleAt( int i ) {
        return (Particle)electrons.get( i );
    }

    public void add( Particle p ) {
        electrons.add( p );
        fireEvent();
    }

    public void remove( Particle p ) {
        electrons.remove( p );
        if( electrons.size() == 0 ) {
            paintSpark = false;
        }
    }

    public void angleChanged( double newAngle ) {
        fireEvent();
    }

    private void fireEvent() {
        boolean ok = shouldFire();
        if( ok ) {
            //edu.colorado.phet.common.util.Debug.traceln("Firing="+ok);
            paintSpark = true;
            moveElectrons();
            //helper.ThreadHelper.quietNap(300);
            ap.play();
            //paintSpark=false;
        }
    }

    public void paint( Graphics2D g ) {
        //edu.colorado.phet.common.util.Debug.traceln("Painting path="+paintSpark);
        if( !paintSpark ) {
            return;
        }
        Point finger = Finger.getFingerLocation( arm );
        s.setSink( finger );
        s.toSparkPath( maxSparkPoints ).paint( g );
    }

    private void moveElectrons() {
        for( int i = 0; i < electrons.size(); i++ ) {
            ( (ShockElectron)electrons.get( i ) ).setPropagator( gte );
        }
    }

    int[] numElectrons = new int[]{10, 15, 20, 35, 30, 35, 40, 50, 60, 70};
    double[] dist = new double[]{20, 30, 40, 40, 60, 70, 80, 100, 120, 140};

    private boolean shouldFire() {
        //low number of electrons requires really close
        Point finger = Finger.getFingerLocation( arm );
        double distToKnob = new DoublePoint( doorknobX, doorknobY ).distance( new DoublePoint( finger.x, finger.y ) );
        int n = electrons.size();

        //edu.colorado.phet.common.util.Debug.traceln("Distance to knob="+distToKnob+", edu.colorado.phet.common count="+n);
        for( int i = 0; i < numElectrons.length; i++ ) {
            if( n > numElectrons[i] && distToKnob < dist[i] ) {
                return true;
            }
        }
        paintSpark = false;
        return false;
    }
}
