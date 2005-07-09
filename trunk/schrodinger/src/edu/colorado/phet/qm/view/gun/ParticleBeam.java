/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:09:55 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class ParticleBeam extends HighIntensityBeam {

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
//        if( alwaysOnCheckBox.isSelected() && intensitySlider.getValue() != 0 ) {
        if( isHighIntensityModeOn() ) {
            int numStepsBetweenFire = getNumStepsBetweenFire();
            return time >= numStepsBetweenFire + lastFireTime;
        }
        return false;
    }

    private int getNumStepsBetweenFire() {
        return numStepsBetweenFire;
//        double frac = intensitySlider.getValue() / ( (double)intensitySlider.getMaximum() );
//        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 1, 20, 1 );
//        return (int)linearFunction.evaluate( frac );
    }

    private void autofire() {
        lastFireTime = time;
        getGunParticle().autofire();
    }
}
