/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
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
 */

//public class GravitySlider extends ModelSlider {
public class GravitySlider extends LinearValueControl {

    public GravitySlider( final EnergySkateParkModule module ) {
        super( 0, 30, EnergySkateParkStrings.getString( "gravity" ), "0.00", EnergySkateParkStrings.getString( "n.kg" ) );
        Hashtable modelTicks = new Hashtable();
        modelTicks.put( new Double( 0 ), new JLabel( EnergySkateParkStrings.getString( "space" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_EARTH ), new JLabel( EnergySkateParkStrings.getString( "earth" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_JUPITER ), new JLabel( EnergySkateParkStrings.getString( "jupiter" ) ) );
        setMajorTickSpacing( 10 );
        setMinorTickSpacing( 10 / 2.0 );
        setTickLabels( modelTicks );

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
        setFocusable( false );
        getSlider().setFocusable( false );
        setBorder( BorderFactory.createEtchedBorder() );
    }

    public void setValue( double value ) {
        if( value != super.getValue() ) {
            super.setValue( value );
        }
    }
}
