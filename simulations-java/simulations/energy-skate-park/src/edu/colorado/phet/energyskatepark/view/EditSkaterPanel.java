package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.physics.Particle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Sam Reid
 * Apr 27, 2007, 12:44:56 AM
 */
public class EditSkaterPanel extends AdvancedPanel {
    private EnergySkateParkModule module;

    public EditSkaterPanel( final EnergySkateParkModule module ) {
        super( EnergySkateParkStrings.getString( "edit.skater" ), EnergySkateParkStrings.getString( "hide.skater.properties" ) );
        this.module = module;
        final ModelSlider restitution = new ModelSlider( EnergySkateParkStrings.getString( "bounciness" ), "", 0, 1.0, 1.0 );
        restitution.setModelTicks( new double[]{0, 0.5, 1} );
        restitution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setBounciness( restitution.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    restitution.setValue( module.getEnergySkateParkModel().getBody( 0 ).getBounciness() );//todo: refactor to listener pattern.
                }
            }
        } );


        final ModelSlider mass = new ModelSlider( EnergySkateParkStrings.getString( "mass" ), EnergySkateParkStrings.getString( "kg" ), 1, 200, 75 );
        mass.setModelTicks( new double[]{1, 75, 200} );
        mass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setMass( mass.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    mass.setValue( module.getEnergySkateParkModel().getBody( 0 ).getMass() );
                }
            }
        } );

        final ModelSlider stickiness = new ModelSlider( "Stickiness", "", 0.01, 5, Body.DEFAULT_STICKINESS );
        stickiness.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setStickiness( stickiness.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    stickiness.setValue( module.getEnergySkateParkModel().getBody( 0 ).getStickiness() );
                }
            }
        } );

        JButton revertToDefaults = new JButton( "Restore Defaults" );
        revertToDefaults.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setMass( module.getSkaterCharacter().getMass() );
                module.setBounciness( Particle.DEFAULT_ELASTICITY );
                setStickiness( Body.DEFAULT_STICKINESS );
            }
        } );

        final JCheckBox keepEnergyOnLanding = new JCheckBox( "Frictionless Landing", isKeepEnergyOnLanding() );
        keepEnergyOnLanding.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < module.getEnergySkateParkModel().getNumBodies(); i++ ) {
                    module.getEnergySkateParkModel().getBody( i ).setKeepEnergyOnLanding( keepEnergyOnLanding.isSelected() );
                }
            }
        } );

        addControl( restitution );
        addControl( mass );
        addControl( stickiness );
        addControl( keepEnergyOnLanding );
        addControl( revertToDefaults );
    }

    private boolean isKeepEnergyOnLanding() {
        EnergySkateParkModel model = module.getEnergySkateParkModel();
        
        for( int i = 0; i < model.getNumBodies(); i++ ) {
            Body b = model.getBody( i );
            return b.isKeepEnergyOnLanding();
        }
        return false;
    }

    private void setMass( double massValue ) {
        EnergySkateParkModel model = module.getEnergySkateParkModel();
        for( int i = 0; i < model.getNumBodies(); i++ ) {
            Body b = model.getBody( i );
            b.setMass( massValue );
        }
    }

    private void setStickiness( double stickinessValue ) {
        Body.staticSticky = stickinessValue;
        for( int i = 0; i < Body.particles.size(); i++ ) {
            Body body = (Body)Body.particles.get( i );
            body.setStickiness( Body.staticSticky );
        }
    }
}
