// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

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

public class GravitySlider extends LinearValueControl {
    private EnergySkateParkModule module;

    public GravitySlider( final EnergySkateParkModule module ) {
        super( 0, 30, EnergySkateParkStrings.getString( "controls.gravity" ), "0.00", EnergySkateParkStrings.getString( "units.accel" ) );

        this.module = module;
        Hashtable modelTicks = new Hashtable();
        modelTicks.put( new Double( 0 ), new JLabel( EnergySkateParkStrings.getString( "location.space" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_EARTH ), new JLabel( EnergySkateParkStrings.getString( "location.earth" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_JUPITER ), new JLabel( EnergySkateParkStrings.getString( "location.jupiter" ) ) );
        setMajorTickSpacing( 10 );
        setMinorTickSpacing( 10 / 2.0 );
        setTickLabels( modelTicks );

        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergySkateParkModel().setGravity( -getValue() );
            }
        } );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                update();
            }
        } );
        setFocusable( false );
        getSlider().setFocusable( false );
        setBorder( BorderFactory.createEtchedBorder() );
        update();
    }

    private void update() {
        setValue( Math.abs( module.getEnergySkateParkModel().getGravity() ) );
    }

}
