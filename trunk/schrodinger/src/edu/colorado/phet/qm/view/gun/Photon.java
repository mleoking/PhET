/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.controls.SRRWavelengthSlider;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:40 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Photon extends GunParticle {
//    private JSlider wavelength;
    private SRRWavelengthSlider wavelengthSliderGraphic;
    private double hbar = 1.0;

    public Photon( AbstractGun abstractGun, String label, String imageLocation ) {
        super( abstractGun, label, imageLocation );
//        wavelength = new JSlider( JSlider.HORIZONTAL, 8, 500, 500 / 2 );
//        wavelength.setBorder( BorderFactory.createTitledBorder( "Wavelength" ) );
//        wavelengthSliderGraphic = PhetJComponent.newInstance( abstractGun.getComponent(), wavelength );
//        wavelengthSliderGraphic=new SpectrumSlider( abstractGun.getSchrodingerPanel() );
        wavelengthSliderGraphic = new SRRWavelengthSlider( abstractGun.getSchrodingerPanel() );

    }

    public void setup( AbstractGun abstractGun ) {
        getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( this );
        abstractGun.getSchrodingerModule().getDiscreteModel().setPropagatorClassical();
        abstractGun.addGraphic( wavelengthSliderGraphic );
        wavelengthSliderGraphic.setLocation( -wavelengthSliderGraphic.getWidth() - 2, abstractGun.getComboBox().getPreferredSize().height + 2 );

    }

    public void deactivate( AbstractGun abstractGun ) {
        abstractGun.removeGraphic( wavelengthSliderGraphic );
//        abstractGun.getSchrodingerModule().getSchrodingerPanel().setDisplayPhotonColor(null);
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

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * 0.9;
    }

    public double getStartPy() {
        double wavelengthValue = getWavelength();
        double momentum = -hbar * 2 * Math.PI / wavelengthValue;
//            System.out.println( "wavelengthValue = " + wavelengthValue + ", momentum=" + momentum );
        return momentum;
    }

    public double getWavelengthNM() {
        return wavelengthSliderGraphic.getWavelength();
    }

    private double getWavelength() {
        double val = wavelengthSliderGraphic.getWavelength();
        double wavelengthValue = new Function.LinearFunction( VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH,
                                                              minWavelength, maxWavelength ).evaluate( val );
//                                                              8, 45).evaluate( val );
        return wavelengthValue;
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


}
