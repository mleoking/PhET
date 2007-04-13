/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.controls.ResolutionControl;

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
    private Pauser pauser;

    public PhotonBeamParticle( AbstractGunNode gunNode, String label, final PhotonBeam photonBeam ) {
        super( gunNode, label, photonBeam.getImageLocation() );
        this.photonBeam = photonBeam;
        rampUp = new RampUp();
        rampDown = new RampDown();
        pauser = new Pauser();
        photonBeam.getPhoton().removeMomentumChangeListener( photonBeam.getColorChangeHandler() );
    }

    private double getFinalNormValue() {
        return 0.995 * getWaveValueScale();
    }

    private double getWaveValueScale() {
        double ratio = getWaveArea() / getDefaultArea();
        return ratio * ratio;
    }

    double getDefaultArea() {
        return ResolutionControl.INIT_WAVE_SIZE * ResolutionControl.INIT_WAVE_SIZE;
    }

    double getWaveArea() {
        return getDiscreteModel().getGridHeight() * getDiscreteModel().getGridWidth();
    }

    private double getRampDownPeak() {
        return 0.98 * getWaveValueScale();
    }

    private double getRampUpPeak() {
        return 0.40 * getWaveValueScale();
    }

    public void reset() {
//        super.reset();
        photonBeam.setIntensity( 0.0 );
        photonBeam.setHighIntensityModeOn( false );
        clearModelElements();
        pauser.reset();
    }

    class Pauser implements ModelElement {
        int count = 0;
        int maxCount = 20;

        public void stepInTime( double dt ) {
            count++;
            getDiscreteModel().setWavefunctionNorm( getFinalNormValue() );
            if( count >= maxCount ) {
                while( getSchrodingerModule().getModel().containsModelElement( this ) ) {
                    removeModelElement( this );
                }
                photonBeam.setIntensity( 0.0 );
                photonBeam.setHighIntensityModeOn( false );
                count = 0;
            }
        }

        public void reset() {
            count = 0;
        }
    }

    abstract class AbstractBeamAdder implements ModelElement {
        public void stepInTime( double dt ) {
            getDiscreteModel().getWavefunction().setMagnitudeDirty();
            double mag = getDiscreteModel().getWavefunction().getMagnitude();

            double intensity = photonBeam.getIntensity();
            if( mag > getRampUpPeak() && this instanceof RampUp ) {
                removeModelElement( this );
                addModelElement( rampDown );
            }

            intensity += getIncrement();
            intensity = Math.max( intensity, 0 );
            photonBeam.setIntensity( intensity );

            if( mag > getRampDownPeak() || ( ( this instanceof RampDown ) && intensity == 0 ) ) {
                getDiscreteModel().setWavefunctionNorm( getFinalNormValue() );
                removeModelElement( this );
                addModelElement( pauser );
                photonBeam.setIntensity( 0.0 );
                getGunGraphic().notifyGunFired();
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
        getGunGraphic().getSchrodingerPanel().setPhoton( photonBeam.getPhoton() );
        clearModelElements();
        photonBeam.setHighIntensityModeOn( true );
        photonBeam.setIntensity( 0 );
        //wait until norm >1.0, then normalize & stop. (maybe ramp down?)
        addModelElement( rampUp );
    }

    public boolean isFiring() {
        return containsModelElement( rampUp ) || containsModelElement( rampDown ) || containsModelElement( pauser );
    }

    public double getMinimumProbabilityForDetection() {
        return 0.05;
    }

    public boolean getTimeThresholdAllowed() {
        return true;
    }

    public int getTimeThresholdCount() {
        return 30;
    }

    private boolean containsModelElement( ModelElement element ) {
        return getSchrodingerModule().getModel().containsModelElement( element );
    }

    private void clearModelElements() {
        removeModelElement( rampUp );
        removeModelElement( rampDown );
        removeModelElement( pauser );
    }

    public void activate( AbstractGunNode abstractGunNode ) {
        super.active = true;
        photonBeam.getPhoton().activate( abstractGunNode );
    }

    public void deactivate( AbstractGunNode abstractGunNode ) {
        super.active = false;
        photonBeam.getPhoton().deactivate( abstractGunNode );
        getGunGraphic().getSchrodingerPanel().setPhoton( null );
    }

    public double getStartPy() {
        return photonBeam.getPhoton().getStartPy();
    }

    protected double getHBar() {
        return 1.0;
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        photonBeam.getPhoton().detachListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        photonBeam.getPhoton().hookupListener( changeHandler );
    }
}
