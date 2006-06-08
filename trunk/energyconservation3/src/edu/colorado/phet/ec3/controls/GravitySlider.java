/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.controls;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.model.EnergyConservationModel;

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

    public GravitySlider( final EC3Module module ) {
        super( "Gravity", "N/kg", 0, -EnergyConservationModel.G_JUPITER * 1.2, -EnergyConservationModel.G_EARTH, new DecimalFormat( "0.00" ) );
        Hashtable modelTicks = new Hashtable();
//        modelTicks.put( new Double( 0 ), new JLabel( "Space" ) );
        modelTicks.put( new Double( -EnergyConservationModel.G_EARTH ), new JLabel( "Earth" ) );
        modelTicks.put( new Double( -EnergyConservationModel.G_MOON ), new JLabel( "Moon" ) );
        modelTicks.put( new Double( -EnergyConservationModel.G_JUPITER ), new JLabel( "Jupiter" ) );
        setModelTicks( new double[]{-EnergyConservationModel.G_MOON, -EnergyConservationModel.G_EARTH, -EnergyConservationModel.G_JUPITER} );
        setModelLabels( modelTicks );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergyConservationModel().setGravity( -getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                double val = module.getEnergyConservationModel().getGravity();
                setValue( -val );
            }
        } );
    }
}
