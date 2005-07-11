/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:40 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Photon extends GunParticle {
    private JSlider wavelength;
    private PhetGraphic wavelengthSliderGraphic;
    private double hbar = 1.0;

    public Photon( AbstractGun abstractGun, String label, String imageLocation ) {
        super( abstractGun, label, imageLocation );
        wavelength = new JSlider( JSlider.HORIZONTAL, 8, 500, 500 / 2 );
        wavelength.setBorder( BorderFactory.createTitledBorder( "Wavelength" ) );
        wavelengthSliderGraphic = PhetJComponent.newInstance( abstractGun.getComponent(), wavelength );
    }

    public void setup( AbstractGun abstractGun ) {
        abstractGun.getSchrodingerModule().getDiscreteModel().setPropagatorClassical();
        abstractGun.addGraphic( wavelengthSliderGraphic );
        wavelengthSliderGraphic.setLocation( -wavelengthSliderGraphic.getWidth() - 2, abstractGun.getComboBox().getPreferredSize().height + 2 );
    }

    public void deactivate( AbstractGun abstractGun ) {
        abstractGun.removeGraphic( wavelengthSliderGraphic );
    }

    public void fireParticle() {

        Propagator propagator = getGunGraphic().getDiscreteModel().getPropagator();
        if( propagator instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator prop = (ClassicalWavePropagator)propagator;
            WaveSetup setup = getInitialWavefunction( getGunGraphic().getDiscreteModel().getWavefunction() );
            Wavefunction init = getGunGraphic().getDiscreteModel().getWavefunction().createEmptyWavefunction();
            setup.initialize( init );
            init.scale( 2.0 );//since we lose half out the bottom.
//                new RandomizePhase().randomizePhase( init );
//                int numAvg = 5;
//                for( int i = 0; i < numAvg; i++ ) {
//                    new AveragePropagator().propagate( init );
//                }
//
//                init.setMagnitude( 1.0 );
            prop.addInitialization( init, init );
        }
        super.fireParticle();

    }

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * 0.9;
    }

    public double getStartPy() {
        double wavelengthValue = new Function.LinearFunction( 0, wavelength.getMaximum(), 5, 20 ).evaluate( wavelength.getValue() );
        double momentum = -hbar * 2 * Math.PI / wavelengthValue;
//            System.out.println( "wavelengthValue = " + wavelengthValue + ", momentum=" + momentum );
        return momentum;
    }

    protected void hookupListener( AbstractGun.MomentumChangeListener momentumChangeListener ) {
        wavelength.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMomentumChanged();
            }
        } );
    }


    public void autofire() {
        //no-op for cylindersource
    }
}
