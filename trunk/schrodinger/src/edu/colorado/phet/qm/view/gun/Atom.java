/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
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
    private JSlider velocity;
    protected final VerticalLayoutPanel controlPanel;
    private PSwing controlPanelPSwing;

    public Atom( AbstractGunGraphic gunGraphic, String label, String imageLocation ) {
        super( gunGraphic, label, imageLocation );
        mass = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        mass.setBorder( BorderFactory.createTitledBorder( "Mass" ) );


        velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );

        controlPanel = new VerticalLayoutPanel();
        controlPanel.addFullWidth( mass );
        controlPanel.addFullWidth( velocity );
        controlPanelPSwing = new PSwing( gunGraphic.getComponent(), controlPanel );
    }

    public void setup( AbstractGunGraphic gunGraphic ) {
        gunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();
        gunGraphic.setGunControls( controlPanelPSwing );
    }

    public void deactivate( AbstractGunGraphic abstractGunGraphic ) {
        abstractGunGraphic.removeGunControls();
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
