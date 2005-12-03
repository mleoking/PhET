/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:22 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Electron extends GunParticle {
    private PNode velocityControlPSwing;
    private JSlider velocity;
    private double electronMass = 1.0;

    public Electron( AbstractGunGraphic abstractGunGraphic, String label, String imageLocation ) {
        super( abstractGunGraphic, label, imageLocation );
        velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
        velocityControlPSwing = new PSwing( abstractGunGraphic.getSchrodingerPanel(), velocity );
    }

    public void setup( AbstractGunGraphic abstractGunGraphic ) {
        abstractGunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();
        abstractGunGraphic.setGunControls( velocityControlPSwing );
//        abstractGunGraphic.addChild( velocityControlPSwing );
//        velocityControlPSwing.setOffset( -velocityControlPSwing.getWidth() - 2, abstractGunGraphic.getComboBox().getHeight() + 2 + abstractGunGraphic.getControlOffsetY() );
    }


    public void deactivate( AbstractGunGraphic abstractGunGraphic ) {
        abstractGunGraphic.removeGunControls();
//        abstractGunGraphic.removeChild( velocityControlPSwing );
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

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= 20;
        return p;
    }
}
