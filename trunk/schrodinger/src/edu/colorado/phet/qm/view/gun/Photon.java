/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.controls.ResolutionControl;
import edu.colorado.phet.qm.controls.SRRWavelengthSlider;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;
import edu.colorado.phet.qm.view.colormaps.ColorData;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:40 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Photon extends GunParticle {
    private SRRWavelengthSlider wavelengthSliderGraphic;
//    protected static final double minWavelength = 8;
//    protected static final double maxWavelength = 30;

//    protected static final double minWavelength = 12;
//    protected static final double maxWavelength = 21;

    protected static final double minWavelength = 13.0 + 1.0 / 3.0;
    protected static final double maxWavelength = 23.0 + 1.0 / 3.0;
    private ParticleUnits.PhotonUnits photonUnits;

    public Photon( AbstractGunNode abstractGunNode, String label, String imageLocation ) {
        super( abstractGunNode, label, imageLocation );
        wavelengthSliderGraphic = new SRRWavelengthSlider( abstractGunNode.getSchrodingerPanel() );
        getDiscreteModel().addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                notifyMomentumChanged();//since wavelength depends on grid area size, now.  See McKagan 3-17-2006
            }
        } );
        photonUnits = new ParticleUnits.PhotonUnits();
    }

    public void activate( AbstractGunNode abstractGunNode ) {
        super.active = true;
        getSchrodingerModule().setUnits( photonUnits );
        getGunGraphic().getSchrodingerPanel().setPhoton( this );
        abstractGunNode.getSchrodingerModule().getQWIModel().setPropagatorClassical();
        JComponent sliderAsComponent = new SRRWavelengthSliderComponent( wavelengthSliderGraphic );
        abstractGunNode.setGunControls( sliderAsComponent );
    }

    public void deactivate( AbstractGunNode abstractGunNode ) {
        super.active = false;
        abstractGunNode.removeGunControls();
    }

    public void fireParticle() {
        Propagator propagator = getGunGraphic().getDiscreteModel().getPropagator();
        if( propagator instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator prop = (ClassicalWavePropagator)propagator;
            WaveSetup setup = getInitialWavefunction( getGunGraphic().getDiscreteModel().getWavefunction() );
            Wavefunction init = getGunGraphic().getDiscreteModel().getWavefunction().createEmptyWavefunction();
            setup.initialize( init );
            prop.addInitialization( init, init );
        }
        super.fireParticle();
    }

    public boolean isFiring() {
        return false;//firing is always a one-shot deal, so we're never in the middle of a shot.
    }

    public double getMinimumProbabilityForDetection() {
        return 0.05;
    }

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * 0.9;
    }

    public double getStartPy() {
//        System.out.println( "getWavelength() = " + getWavelength() );
//        System.out.println( "getDiscreteModel().getGridHeight() = " + getDiscreteModel().getGridHeight() );
        double wavelengthValue = getWavelength() * getDiscreteModel().getGridHeight() / ( (double)ResolutionControl.INIT_WAVE_SIZE );
//        double wavelengthValue = getWavelength();// * getDiscreteModel().getGridHeight() / ( (double)ResolutionControl.INIT_WAVE_SIZE );
//        System.out.println( "wavelengthValue = " + wavelengthValue );
        return 2 * Math.PI / wavelengthValue;
    }

    protected double getHBar() {
        return 1.0;
    }

    public double getWavelengthNM() {
        return wavelengthSliderGraphic.getWavelength();
    }

    private double getWavelength() {
        double val = wavelengthSliderGraphic.getWavelength();
        return new Function.LinearFunction( VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH,
                                            minWavelength, maxWavelength ).evaluate( val );
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        wavelengthSliderGraphic.removeChangeListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        wavelengthSliderGraphic.addChangeListener( changeHandler );
    }

    public void autofire() {
        //no-op for cylindersource
    }

    public ColorData getRootColor() {
        return new ColorData( getWavelengthNM() );
    }
}
