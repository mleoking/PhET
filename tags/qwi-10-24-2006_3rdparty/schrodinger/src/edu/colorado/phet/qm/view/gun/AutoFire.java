/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 8:16:11 AM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class AutoFire implements IntensityManager.Listener {
    private SingleParticleGunNode gunGraphic;
    private IntensityManager intensityManager;
    private boolean autoFire = false;
    private ModelElement element;
    public static final double THRESHOLD = 0.015;
    private long lastFire = 0;
    private static final long MIN_WAIT_TIME = 500;//todo this may be unnecessary because of the new gun.isFiring() method

    public AutoFire( SingleParticleGunNode gunGraphic, IntensityManager intensityManager ) {
        this.gunGraphic = gunGraphic;
        this.intensityManager = intensityManager;
        intensityManager.addListener( this );
        element = new ModelElement() {
            public void stepInTime( double dt ) {
                checkDetection();
            }
        };
    }

    private void checkDetection() {
        double mag = gunGraphic.getSchrodingerModule().getQWIModel().getWavefunction().getMagnitude();
//        System.out.println( "mag = " + mag );
        if( mag < THRESHOLD || Double.isNaN( mag ) ) {
            if( timeSinceFire() > MIN_WAIT_TIME && !gunGraphic.isFiring() ) {
//                System.out.println( "timeSinceFire() = " + timeSinceFire() );
                fire();
            }
        }
    }

    private long timeSinceFire() {
        return System.currentTimeMillis() - lastFire;
    }

    private void fire() {
        gunGraphic.clearAndFire();
        lastFire = System.currentTimeMillis();
    }

    public void detectionOccurred() {
        if( autoFire ) {
            fire();
        }
    }

    public boolean isAutoFire() {
        return autoFire;
    }

    public void setAutoFire( boolean autoFire ) {
        if( this.autoFire != autoFire ) {
            this.autoFire = autoFire;
            if( this.autoFire ) {
                if( intensityManager.getSchrodingerPanel().getDiscreteModel().getWavefunction().getMagnitude() == 0 ) {
                    fire();
                }
                gunGraphic.getSchrodingerModule().getModel().addModelElement( element );
            }
            else {
                gunGraphic.getSchrodingerModule().getModel().removeModelElement( element );
            }
        }

    }
}
