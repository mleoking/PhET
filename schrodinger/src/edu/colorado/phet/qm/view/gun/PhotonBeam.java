/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.CylinderWave;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:04:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class PhotonBeam extends HighIntensityBeam {
    private CylinderWave cylinderWave;
    private AbstractGun abstractGun;

    public PhotonBeam( AbstractGun abstractGun ) {
        this.abstractGun = abstractGun;
        cylinderWave = new CylinderWave( getGunGraphic().getSchrodingerModule(), getGunGraphic().getDiscreteModel(), abstractGun );
//        wavelength.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                cylinderWave.setMomentum( getStartPy() );
//            }
//        } );
    }

    private AbstractGun getGunGraphic() {
        return abstractGun;
    }

    public void setHighIntensityModeOn( boolean on ) {
        super.setHighIntensityModeOn( on );
        if( on ) {
            cylinderWave.setOn();
        }
        else {
            cylinderWave.setOff();
        }
    }

    public void setIntensity( double intensity ) {
        super.setIntensity( intensity );
        cylinderWave.setIntensity( intensity );
    }
}
