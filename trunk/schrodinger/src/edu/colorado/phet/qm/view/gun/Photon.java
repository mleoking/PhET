/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.controls.SRRWavelengthSlider;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;
import edu.colorado.phet.qm.view.colormaps.ColorData;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:40 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Photon extends GunParticle {
    private SRRWavelengthSlider wavelengthSliderGraphic;
    private double hbar = 1.0;
    protected static final double minWavelength = 8;
    protected static final double maxWavelength = 30;

    public Photon( AbstractGunGraphic abstractGunGraphic, String label, String imageLocation ) {
        super( abstractGunGraphic, label, imageLocation );
        wavelengthSliderGraphic = new SRRWavelengthSlider( abstractGunGraphic.getSchrodingerPanel() );
    }

    public void setup( AbstractGunGraphic abstractGunGraphic ) {
        getGunGraphic().getSchrodingerPanel().setPhoton( this );
        abstractGunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorClassical();
        abstractGunGraphic.setGunControls( wavelengthSliderGraphic );
    }

    public void deactivate( AbstractGunGraphic abstractGunGraphic ) {
        abstractGunGraphic.removeGunControls();
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

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * 0.9;
    }

    public double getStartPy() {
        double wavelengthValue = getWavelength();
        return -hbar * 2 * Math.PI / wavelengthValue;
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
