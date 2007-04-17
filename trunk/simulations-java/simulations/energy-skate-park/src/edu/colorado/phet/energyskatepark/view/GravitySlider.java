/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Nov 8, 2005
 * Time: 11:11:41 AM
 * Copyright (c) Nov 8, 2005 by Sam Reid
 */

public class GravitySlider extends ModelSlider {

    public GravitySlider( final EnergySkateParkModule module ) {
//        super( "Gravity", "N/kg", 0, -EnergyConservationModel.G_JUPITER * 1.2, -EnergyConservationModel.G_EARTH, new DecimalFormat( "0.00" ), new DecimalFormat( "0" ) );
        super( EnergySkateParkStrings.getString( "gravity" ), EnergySkateParkStrings.getString( "n.kg" ), 0, 30, -EnergySkateParkModel.G_EARTH, new DefaultDecimalFormat( "0.00" ) );
        Hashtable modelTicks = new Hashtable();
        modelTicks.put( new Double( 0 ), new JLabel( EnergySkateParkStrings.getString( "space" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_EARTH ), new JLabel( EnergySkateParkStrings.getString( "earth" ) ) );
//        modelTicks.put( new Double( -EnergyConservationModel.G_MOON ), new JLabel( "Moon" ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_JUPITER ), new JLabel( EnergySkateParkStrings.getString( "jupiter" ) ) );
//        setModelTicks( new double[]{-EnergyConservationModel.G_MOON, -EnergyConservationModel.G_EARTH, -EnergyConservationModel.G_JUPITER} );
//        setModelTicks( new double[]{0, -EnergyConservationModel.G_EARTH, -EnergyConservationModel.G_JUPITER} );
//        setModelTicks( new double[]{0, 5,10,15,20,25,30} );
        setMajorTickSpacing( 10 );
        setNumMinorTicksPerMajorTick( 2 );
//        setNumMinorTicks( 0);
        setModelLabels( modelTicks );

//        setModelTicks( new double[]{0, 10, 20, 30, 40} );
//        setNumMajorTicks( 4 );
//        setNumMinorTicksPerMajorTick( 2 );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergySkateParkModel().setGravity( -getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                double val = module.getEnergySkateParkModel().getGravity();
                setValue( -val );

            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                if( getUnitsReadout().getBorder() != null ) {
                    getUnitsReadout().setBorder( null );
                }
            }
        } );

        getUnitsReadout().setBackground( EC3LookAndFeel.backgroundColor );
        getUnitsReadout().setBorder( null );
        setFocusable( false );
        getSlider().setFocusable( false );
    }
}
