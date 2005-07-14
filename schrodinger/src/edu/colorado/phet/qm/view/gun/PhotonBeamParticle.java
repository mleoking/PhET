/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 14, 2005
 * Time: 12:33:12 PM
 * Copyright (c) Jul 14, 2005 by Sam Reid
 */

public class PhotonBeamParticle extends GunParticle {
    private PhotonBeam photonBeam;
    private ModelElement rampUp;
    private ModelElement rampDown;
    private ModelElement pauser;

    public PhotonBeamParticle( AbstractGun gun, String label, final PhotonBeam photonBeam ) {
        super( gun, label, photonBeam.getImageLocation() );
        this.photonBeam = photonBeam;
        rampUp = new RampUp();
        rampDown = new RampDown();
        pauser = new Pauser();
    }

    class Pauser implements ModelElement {
        int count = 0;
        int maxCount = 20;

        public void stepInTime( double dt ) {
            count++;
            getDiscreteModel().getWavefunction().normalize();
            if( count >= maxCount ) {
                while( getSchrodingerModule().getModel().containsModelElement( this ) ) {
                    removeModelElement( this );
                }
                photonBeam.setIntensity( 0.0 );
                photonBeam.setHighIntensityModeOn( false );
                count = 0;
            }
        }
    }

    abstract class AbstractBeamAdder implements ModelElement {
        public void stepInTime( double dt ) {
            double mag = getDiscreteModel().getWavefunction().getMagnitude();

            double intensity = photonBeam.getIntensity();
            if( mag > 0.35 && this instanceof RampUp ) {
                removeModelElement( this );
                addModelElement( rampDown );
            }

            intensity += getIncrement();
            intensity = Math.max( intensity, 0 );
            System.out.println( "this = " + this + ", setting intensity=" + intensity );
            photonBeam.setIntensity( intensity );


            System.out.println( "mag = " + mag );
            if( mag > 0.93 ) {
                removeModelElement( this );
                addModelElement( pauser );
                getDiscreteModel().getWavefunction().normalize();
                photonBeam.setIntensity( 0.0 );
            }
        }

        abstract double getIncrement();

    }

    class RampUp extends AbstractBeamAdder {

        double getIncrement() {
            return 0.05;
        }

    }

    class RampDown extends AbstractBeamAdder {

        double getIncrement() {
            return -0.05;
        }
    }

    private void removeModelElement( ModelElement modelElement ) {
        getSchrodingerModule().getModel().removeModelElement( modelElement );
    }

    private void addModelElement( ModelElement modelElement ) {
        getSchrodingerModule().getModel().addModelElement( modelElement );
    }

    public void fireParticle() {
        removeModelElement( rampUp );
        removeModelElement( rampDown );
        removeModelElement( pauser );
        photonBeam.setHighIntensityModeOn( true );
        photonBeam.setIntensity( 0 );
        //wait until norm >1.0, then normalize & stop. (maybe ramp down?)
        addModelElement( rampUp );
    }

    public void setup( AbstractGun abstractGun ) {
        photonBeam.getPhoton().setup( abstractGun );
    }

    public void deactivate( AbstractGun abstractGun ) {
        photonBeam.getPhoton().deactivate( abstractGun );
    }

    public double getStartPy() {
        return photonBeam.getPhoton().getStartPy();
    }

    protected void hookupListener( AbstractGun.MomentumChangeListener momentumChangeListener ) {
        photonBeam.getPhoton().hookupListener( momentumChangeListener );
    }
}
