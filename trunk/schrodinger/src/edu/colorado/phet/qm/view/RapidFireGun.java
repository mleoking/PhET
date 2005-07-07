/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class RapidFireGun extends GunGraphic {
    private JCheckBox alwaysOnCheckBox;
    private JSlider intensitySlider;
    private int lastFireTime = 0;
    private int time = 0;
    private boolean on = false;

    public RapidFireGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        alwaysOnCheckBox = new JCheckBox( "On" );
        alwaysOnCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setOn( alwaysOnCheckBox.isSelected() );
            }
        } );

        intensitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 0 );
        intensitySlider.setBorder( BorderFactory.createTitledBorder( "Rate" ) );
        PhetGraphic intensityGraphic = PhetJComponent.newInstance( schrodingerPanel, intensitySlider );

        PhetGraphic onJC = PhetJComponent.newInstance( schrodingerPanel, alwaysOnCheckBox );

        addGraphic( onJC );
        addGraphic( intensityGraphic );
//        onJC.setLocation( getGunImageGraphic().getWidth() + 2, fireJC.getHeight() + 2 );
        onJC.setLocation( getGunImageGraphic().getWidth() + 2, 0 );

        intensityGraphic.setLocation( getGunImageGraphic().getWidth() + 2, onJC.getY() + onJC.getHeight() + 4 );
        intensitySlider.setEnabled( false );

        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                time++;
                if( isTimeToFire() ) {
                    autofire();
                }
            }
        } );
    }

    private boolean isTimeToFire() {
        if( alwaysOnCheckBox.isSelected() && intensitySlider.getValue() != 0 ) {
            int numStepsBetweenFire = getNumStepsBetweenFire();
            return time >= numStepsBetweenFire + lastFireTime;
        }
        return false;
    }

    private int getNumStepsBetweenFire() {

        double frac = intensitySlider.getValue() / ( (double)intensitySlider.getMaximum() );
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 1, 20, 1 );
        return (int)linearFunction.evaluate( frac );
    }

    private void setOn( boolean on ) {
//        fireOne.setEnabled( !on );
        intensitySlider.setEnabled( on );
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    private void autofire() {
        lastFireTime = time;
//        System.out.println( "System.currentTimeMillis() = " + System.currentTimeMillis() );
        fireParticle();
    }
}
