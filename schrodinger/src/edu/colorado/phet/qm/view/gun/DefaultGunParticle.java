/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.propagators.ModifiedRichardsonPropagator;
import edu.umd.cs.piccolox.pswing.PSwing;

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
    private JSlider velocitySlider;
    protected VerticalLayoutPanel controlPanel;
    private PSwing controlPanelPSwing;
    private ParticleUnits particleUnits;

    public DefaultGunParticle( AbstractGunGraphic gunGraphic, String label, String imageLocation, ParticleUnits particleUnits ) {
        super( gunGraphic, label, imageLocation );
        createControls( gunGraphic );
        this.particleUnits = particleUnits;
    }

    public DefaultGunParticle( AbstractGunGraphic gunGraphic, String label, String imageLocation ) {
        super( gunGraphic, label, imageLocation );
        createControls( gunGraphic );
    }

    private void createControls( AbstractGunGraphic gunGraphic ) {
        velocitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocitySlider.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );

        controlPanel = new VerticalLayoutPanel();
        controlPanel.addFullWidth( velocitySlider );
        controlPanelPSwing = new PSwing( gunGraphic.getComponent(), controlPanel );
    }

    public void activate( AbstractGunGraphic gunGraphic ) {
        getSchrodingerModule().setUnits( particleUnits );
        getDiscreteModel().setPropagator( createPropagator() );
        gunGraphic.setGunControls( controlPanelPSwing );
    }

    public void deactivate( AbstractGunGraphic abstractGunGraphic ) {
        abstractGunGraphic.removeGunControls();
    }

    private Propagator createPropagator() {
        return new ModifiedRichardsonPropagator( getDT(), getDiscreteModel().getWave(), getDiscreteModel().getPotential(), getHBar(), getParticleMass() );
    }

    protected double getHBar() {
        return particleUnits.getHbar().getValue();
    }

    private double getDT() {
        return particleUnits.getDt().getValue();
    }

    public double getStartPy() {
        return -getVelocity() * getParticleMass();
    }

    private double getVelocity() {
        return new Function.LinearFunction( 0, 1000, getMinVelocity(), getMaxVelocity() ).evaluate( velocitySlider.getValue() );
    }

    private double getMaxVelocity() {
        return particleUnits.getMaxVelocity().getValue();
    }

    private double getMinVelocity() {
        return particleUnits.getMinVelocity().getValue();
    }

    protected void detachListener( ChangeHandler changeHandler ) {
        velocitySlider.removeChangeListener( changeHandler );
    }

    protected void hookupListener( ChangeHandler changeHandler ) {
        velocitySlider.addChangeListener( changeHandler );
    }

    public Point getGunLocation() {
        Point p = super.getGunLocation();
        p.y -= AbstractGunGraphic.GUN_PARTICLE_OFFSET;
        return p;
    }

    public Map getModelParameters() {
        Map map = super.getModelParameters();
        map.put( "init_mass", "" + getParticleMass() );
        map.put( "init_vel", "" + getVelocity() );
        map.put( "init_momentum", "" + getStartPy() );
        map.put( "start_y", "" + getStartY() );
        map.put( "initial_dx_lattice", "" + getStartDxLattice() );
        return map;
    }

    private double getParticleMass() {
        return particleUnits.getMass().getValue();
    }

    public boolean isFiring() {
        return false;//firing is always a one-shot deal, so we're never in the middle of a shot.
    }

    public static DefaultGunParticle createElectron( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Electrons", "images/electron-thumb.jpg", new ParticleUnits.ElectronUnits() );
    }

    public static DefaultGunParticle createHelium( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Helium Atoms", "images/atom-thumb.jpg", new ParticleUnits.HeliumUnits() );
    }

    public static DefaultGunParticle createNeutron( AbstractGunGraphic gun ) {
        return new DefaultGunParticle( gun, "Neutrons", "images/neutron-thumb.gif", new ParticleUnits.NeutronUnits() );
    }
}
