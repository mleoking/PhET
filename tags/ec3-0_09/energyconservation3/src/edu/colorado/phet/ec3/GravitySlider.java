/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.components.ModelSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Nov 8, 2005
 * Time: 11:11:41 AM
 * Copyright (c) Nov 8, 2005 by Sam Reid
 */

public class GravitySlider extends ModelSlider {
    private static final double G_EARTH = 9.81;
    private static final double G_MOON = 1.62;
    private static final double G_JUPITER = 25.95;

    public GravitySlider( final EC3Module module ) {
        super( "Gravity", "N/kg", 0, 29.95, G_EARTH, new DecimalFormat( "0.00" ) );
        Hashtable modelTicks = new Hashtable();
        modelTicks.put( new Double( G_EARTH ), new JLabel( "Earth" ) );
        modelTicks.put( new Double( G_MOON ), new JLabel( "Moon" ) );
        modelTicks.put( new Double( G_JUPITER ), new JLabel( "Jupiter" ) );
        setModelTicks( new double[]{G_MOON, G_EARTH, G_JUPITER} );
        setModelLabels( modelTicks );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergyConservationModel().setGravity( getValue() );
            }
        } );
        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                double val = module.getEnergyConservationModel().getGravity();
                setValue( val );
            }
        } );
    }
}
