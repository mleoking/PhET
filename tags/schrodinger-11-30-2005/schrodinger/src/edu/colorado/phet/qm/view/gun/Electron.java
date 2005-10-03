/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:22 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Electron extends GunParticle {
    private PNode graphic;
    private JSlider velocity;
    private double electronMass = 1.0;

    public Electron( AbstractGun abstractGun, String label, String imageLocation ) {
        super( abstractGun, label, imageLocation );
        velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
        graphic = new PSwing( abstractGun.getSchrodingerPanel(), velocity );
    }

    public void setup( AbstractGun abstractGun ) {
        abstractGun.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();

        abstractGun.addChild( graphic );
        graphic.setOffset( -graphic.getWidth() - 2, abstractGun.getComboBox().getHeight() + 2 + abstractGun.getControlOffsetY() );
    }


    public void deactivate( AbstractGun abstractGun ) {
        abstractGun.removeChild( graphic );
    }

    public double getStartPy() {
        double velocityValue = new Function.LinearFunction( 0, 1000, 0, 1.25 ).evaluate( velocity.getValue() );
        return -velocityValue * electronMass;
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        velocity.removeChangeListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        velocity.addChangeListener( changeHandler );
    }

//    protected void hookupListener( AbstractGun.MomentumChangeListener momentumChangeListener ) {
//        velocity.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                notifyMomentumChanged();
//            }
//        } );
//    }

}
