/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class GunGraphic extends GraphicLayerSet {
    public JButton fireOne;
    public JCheckBox alwaysOn;
    public JSlider intensitySlider;
    private int time = 0;
    private int lastFireTime = 0;
    private SchrodingerPanel schrodingerPanel;
    public PhetImageGraphic phetImageGraphic;

    public GunGraphic( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
        phetImageGraphic = new PhetImageGraphic( getComponent(), "images/laser.gif" );
        addGraphic( phetImageGraphic );

        fireOne = new JButton( "Fire Once" );
        fireOne.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                schrodingerPanel.getSchrodingerModule().getSchrodingerControlPanel().fireParticle();
            }
        } );
        alwaysOn = new JCheckBox( "Always On" );
        alwaysOn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setAlwaysOn( alwaysOn.isSelected() );
            }
        } );

        intensitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 0 );
        intensitySlider.setBorder( BorderFactory.createTitledBorder( "Intensity" ) );
        PhetGraphic intensityGraphic = PhetJComponent.newInstance( schrodingerPanel, intensitySlider );

        PhetGraphic fireJC = PhetJComponent.newInstance( schrodingerPanel, fireOne );
        PhetGraphic onJC = PhetJComponent.newInstance( schrodingerPanel, alwaysOn );
        addGraphic( fireJC );
        addGraphic( onJC );
        addGraphic( intensityGraphic );
        onJC.setLocation( phetImageGraphic.getWidth() + 2, fireJC.getHeight() + 2 );
        fireJC.setLocation( phetImageGraphic.getWidth() + 2, 0 );
        intensityGraphic.setLocation( phetImageGraphic.getWidth() + 2, onJC.getY() + onJC.getHeight() + 4 );
        intensitySlider.setEnabled( false );

        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                time++;
                if( isTimeToFire() ) {
                    fire();
                }
            }
        } );
    }

    private void fire() {
        lastFireTime = time;
//        if( schrodingerPanel.getSchrodingerModule().getDiscreteModel().getWavefunction().getMagnitude() > 0.8 ) {
        if( intensitySlider.getValue() == intensitySlider.getMaximum() ) {
            for( int i = 0; i < 10; i++ ) {
                schrodingerPanel.getSchrodingerModule().getIntensityDisplay().detectOne();
            }
        }
        else {
            schrodingerPanel.getSchrodingerModule().getIntensityDisplay().detectOne();
        }
//        }
    }

    private boolean isTimeToFire() {
        if( alwaysOn.isSelected() && intensitySlider.getValue() != 0 ) {
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

    private void setAlwaysOn( boolean on ) {
        fireOne.setEnabled( !on );
        intensitySlider.setEnabled( on );
    }

    public int getGunWidth() {
        return phetImageGraphic.getWidth();
    }
}
