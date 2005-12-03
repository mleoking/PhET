/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.piccolo.pswing.PSwing;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:48 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Atom extends GunParticle {
    private JSlider mass;
    private PSwing massGraphic;
    private JSlider velocity;
    private PSwing velocityGraphic;

    public Atom( AbstractGunGraphic gunGraphic, String label, String imageLocation ) {
        super( gunGraphic, label, imageLocation );
        mass = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        mass.setBorder( BorderFactory.createTitledBorder( "Mass" ) );
        massGraphic = new PSwing( gunGraphic.getComponent(), mass );

        velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
        velocityGraphic = new PSwing( gunGraphic.getComponent(), velocity );
    }

    public void setup( AbstractGunGraphic gunGraphic ) {
        gunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();

        gunGraphic.addChild( massGraphic );
        massGraphic.setOffset( -massGraphic.getWidth() - 2, gunGraphic.getComboBoxGraphic().getFullBounds().getMaxY() );

        gunGraphic.addChild( velocityGraphic );
        velocityGraphic.setOffset( -velocityGraphic.getWidth() - 2, massGraphic.getFullBounds().getMaxY() );
    }

    public void deactivate( AbstractGunGraphic abstractGunGraphic ) {
        abstractGunGraphic.removeChild( massGraphic );
        abstractGunGraphic.removeChild( velocityGraphic );
    }

    public double getStartPy() {
        double velocityValue = new Function.LinearFunction( 0, 1000, 0, 1.5 ).evaluate( velocity.getValue() );
        double massValue = new Function.LinearFunction( 0, 1000, 0, 1 ).evaluate( mass.getValue() );
        return -velocityValue * massValue;
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        mass.removeChangeListener( changeHandler );
        velocity.removeChangeListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        mass.addChangeListener( changeHandler );
        velocity.addChangeListener( changeHandler );
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGunGraphic.GUN_PARTICLE_OFFSET;
        return p;
    }
}
