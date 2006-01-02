/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:48 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class DefaultGunParticle extends GunParticle {
    private JSlider massSlider;
    private JSlider velocitySlider;
    protected VerticalLayoutPanel controlPanel;
    private PSwing controlPanelPSwing;
    private double mass = 1.0;

    public DefaultGunParticle( AbstractGunGraphic gunGraphic, String label, String imageLocation, double mass ) {
        super( gunGraphic, label, imageLocation );
        createControls( gunGraphic, false );
        this.mass = mass;
    }

    public DefaultGunParticle( AbstractGunGraphic gunGraphic, String label, String imageLocation ) {
        super( gunGraphic, label, imageLocation );
        createControls( gunGraphic, true );
    }

    private void createControls( AbstractGunGraphic gunGraphic, boolean useMassSlider ) {
        velocitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocitySlider.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );

        controlPanel = new VerticalLayoutPanel();

        if( useMassSlider ) {
            massSlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
            massSlider.setBorder( BorderFactory.createTitledBorder( "Mass" ) );
            controlPanel.addFullWidth( massSlider );
        }

        controlPanel.addFullWidth( velocitySlider );
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
        return -getVelocity() * getMass();
    }

    private double getVelocity() {
        return new Function.LinearFunction( 0, 1000, 0, 1.5 ).evaluate( velocitySlider.getValue() );
    }

    private double getMass() {
        if( massSlider == null ) {
            return mass;
        }
        else {
            double mass = new Function.LinearFunction( 0, 1000, 0, 2.5 ).evaluate( massSlider.getValue() );
            System.out.println( "mass = " + mass );
            return mass;
        }
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        massSlider.removeChangeListener( changeHandler );
        velocitySlider.removeChangeListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        massSlider.addChangeListener( changeHandler );
        velocitySlider.addChangeListener( changeHandler );
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGunGraphic.GUN_PARTICLE_OFFSET;
        return p;
    }

    public static DefaultGunParticle createElectron( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Electrons", "images/electron-thumb.jpg", 1 );
    }

    public static DefaultGunParticle createHelium( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Helium Atoms", "images/atom-thumb.jpg", 2.25 );
    }

    public static DefaultGunParticle createNeutron( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Neutrons", "images/neutron-thumb.gif", 2.0 );
    }

    public static DefaultGunParticle createCustomAtom( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Custom Atoms", "images/atom-thumb.jpg" );
    }

    public Map getModelParameters() {
        Map map = super.getModelParameters();
        map.put( "init_mass", "" + getMass() );
        map.put( "init_vel", "" + getVelocity() );
        map.put( "init_momentum", "" + getStartPy() );
        map.put( "start_y", "" + getStartY() );
        map.put( "initial_dx_lattice", "" + getStartDxLattice() );
        return map;
    }

    public boolean isFiring() {
        return false;//firing is always a one-shot deal, so we're never in the middle of a shot.
    }
}
