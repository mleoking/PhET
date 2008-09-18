package edu.colorado.phet.batteryvoltage.man.dir;

import edu.colorado.phet.batteryvoltage.Battery;
import edu.colorado.phet.batteryvoltage.ParticleMoveListener;
import edu.colorado.phet.batteryvoltage.common.electron.man.motions.Translate;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.man.Directional;

public class SpeedDir implements ParticleMoveListener, Directional {
    Translate t;
    boolean right;
    double minSpeed;
    double maxSpeed;
    Battery b;

    public SpeedDir( Battery b, Translate t, boolean right, double minSpeed, double maxSpeed ) {
        this.t = t;
        this.right = right;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.b = b;
    }

    public void setCarryRight( boolean bx ) {
        //util.Debug.traceln("Set right="+bx);
        this.right = bx;

        particleMoved( b, null );
    }

    public void particleMoved( Battery b, Particle p ) {
        //Recompute the speeds.
        double dSpeed = maxSpeed - minSpeed;
        double count = 0;
        int sign = 1;
        if ( right ) {
            count = b.numRight();
        }
        else {
            count = b.numLeft();
            sign = -1;
        }
        double numElectrons = b.numElectrons();
        double amt = 1.0 - count / b.numElectrons();

        double speed = minSpeed + dSpeed * amt;
        speed *= sign;

        //util.Debug.traceln("Count="+count+"numElec="+numElectrons+", amt="+amt+", Speed="+speed+", SIGN="+sign);
        t.setVelocity( speed, 0 );
    }
}
