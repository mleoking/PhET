// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;

/**
 * User: Sam Reid
 * Date: Nov 8, 2005
 * Time: 11:11:41 AM
 */

public class GravitySlider extends LinearValueControl {
    private final AbstractEnergySkateParkModule module;

    public GravitySlider( final AbstractEnergySkateParkModule module ) {
        super( 0, 30, EnergySkateParkResources.getString( "controls.gravity" ), "0.00", EnergySkateParkResources.getString( "units.accel" ) );

        this.module = module;
        Hashtable modelTicks = new Hashtable();
        modelTicks.put( new Double( 0 ), new JLabel( EnergySkateParkResources.getString( "location.space" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_EARTH ), new JLabel( EnergySkateParkResources.getString( "location.earth" ) ) );
        modelTicks.put( new Double( -EnergySkateParkModel.G_JUPITER ), new JLabel( EnergySkateParkResources.getString( "location.jupiter" ) ) );
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
