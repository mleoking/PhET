// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:09:55 PM
 */

public class ParticleBeam extends IntensityBeam {

    private int numStepsBetweenFire = 4;
    private int lastFireTime = 0;
    private int time = 0;

    public ParticleBeam( GunParticle gunParticle ) {
        super( gunParticle );
    }

    public void stepBeam() {
        time++;
        if( isTimeToFire() ) {
            autofire();
        }
    }

    private boolean isTimeToFire() {
        if( isHighIntensityModeOn() ) {
            int numStepsBetweenFire = getNumStepsBetweenFire();
            return time >= numStepsBetweenFire + lastFireTime;
        }
        return false;
    }

    private int getNumStepsBetweenFire() {
        return numStepsBetweenFire;
    }

    private void autofire() {
        lastFireTime = time;
        getGunParticle().autofire();
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGunNode.GUN_PARTICLE_OFFSET;
        return p;
    }
}
