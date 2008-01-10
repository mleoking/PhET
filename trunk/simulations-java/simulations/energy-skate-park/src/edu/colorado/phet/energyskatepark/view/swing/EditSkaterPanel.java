package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
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
        super( EnergySkateParkStrings.getString( "controls.edit-skater" )+" >>", EnergySkateParkStrings.getString( "controls.hide-skater-properties" ) +" <<");
        this.module = module;
        final EnergySkateParkSlider restitution = new EnergySkateParkSlider( EnergySkateParkStrings.getString( "controls.bounciness" ), "", 0, 1.0, 1.0 );
        restitution.setModelTicks( new double[]{0, 0.5, 1} );
        restitution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setBounciness( restitution.getValue() );
            }
        } );
        final EnergySkateParkSlider mass = new EnergySkateParkSlider(
                EnergySkateParkStrings.getString( "controls.mass" ), EnergySkateParkStrings.getString( "units.kg" ), 0.2, 200, 75 );
        mass.setModelTicks( new double[]{0.2, 75, 200} );
        mass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setMass( mass.getValue() );
            }
        } );

        final EnergySkateParkSlider stickiness = new EnergySkateParkSlider( EnergySkateParkStrings.getString( "controls.skater.stickiness" ), "", 0.01, 5, Body.DEFAULT_STICKINESS );
        stickiness.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setStickiness( stickiness.getValue() );
            }
        } );

        JButton revertToDefaults = new JButton( EnergySkateParkStrings.getString( "controls.skater.restore-defaults" ) );
        revertToDefaults.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                restoreDefaults();
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

        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                if( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    restitution.setValue( module.getEnergySkateParkModel().getBody( 0 ).getBounciness() );
                    mass.setValue( module.getEnergySkateParkModel().getBody( 0 ).getMass() );
                    stickiness.setValue( module.getEnergySkateParkModel().getBody( 0 ).getStickiness() );
                }
            }
        } );

        addControl( restitution );
        addControl( mass );
        addControl( stickiness );
        addControl( revertToDefaults );
    }

    private void restoreDefaults() {
        setMass( module.getSkaterCharacter().getMass() );
        module.setBounciness( Particle.DEFAULT_ELASTICITY );
        setStickiness( Body.DEFAULT_STICKINESS );
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

    public void reset() {
        restoreDefaults();
        setAdvancedControlsVisible( false );
    }
}
