/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:02:48 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class Atom extends GunParticle {
    private JSlider mass;
    private PhetGraphic massGraphic;
    private JSlider velocity;
    private PhetGraphic velocityGraphic;

    public Atom( AbstractGun gun, String label, String imageLocation ) {
        super( gun, label, imageLocation );
        mass = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        mass.setBorder( BorderFactory.createTitledBorder( "Mass" ) );
        massGraphic = PhetJComponent.newInstance( gun.getComponent(), mass );

        velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
        velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
        velocityGraphic = PhetJComponent.newInstance( gun.getComponent(), velocity );
    }

    public void setup( AbstractGun gun ) {
        gun.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();

        gun.addGraphic( massGraphic );
        massGraphic.setLocation( -massGraphic.getWidth() - 2, gun.initComboBox().getHeight() + 2 );

        gun.addGraphic( velocityGraphic );
        velocityGraphic.setLocation( -velocityGraphic.getWidth() - 2, massGraphic.getY() + massGraphic.getHeight() + 2 );
    }

    public void deactivate( AbstractGun abstractGun ) {
        abstractGun.removeGraphic( massGraphic );
        abstractGun.removeGraphic( velocityGraphic );
    }

    public double getStartPy() {
        double velocityValue = new Function.LinearFunction( 0, 1000, 0, 1.5 ).evaluate( velocity.getValue() );
        double massValue = new Function.LinearFunction( 0, 1000, 0, 1 ).evaluate( mass.getValue() );
        return -velocityValue * massValue;
    }

    protected void hookupListener( AbstractGun.MomentumChangeListener momentumChangeListener ) {
        mass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMomentumChanged();
            }
        } );
        velocity.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMomentumChanged();
            }
        } );
    }
}
