/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.BohrModel;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.hydrogenatom.model.RutherfordScattering;
import edu.colorado.phet.hydrogenatom.module.HAModule;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperControlsDialog extends JDialog {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int MIN_PARTICLES_IN_BOX = 1;
    private static final int MAX_PARTICLES_IN_BOX = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAModule _module;
    
    private JSpinner _maxParticlesSpinner;
    private JSpinner _absorptionClosenessSpinner;
    private JCheckBox _rutherfordScatteringOutputCheckBox;

    private JCheckBox _bohrAbsorptionCheckBox;
    private JCheckBox _bohrEmissionCheckBox;
    private JCheckBox _bohrStimulatedEmissionCheckBox;
    private JSpinner _minStateTimeSpinner;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperControlsDialog( Frame owner, HAModule module ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        
        _module = module;
        
        JPanel inputPanel = createInputPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {

        // Photon & alpha particle production
        HorizontalLayoutPanel maxParticlesPanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "Max particles in box:" );
            
            int maxParticles = _module.getGun().getMaxParticles();
            SpinnerModel model = new SpinnerNumberModel( maxParticles, MIN_PARTICLES_IN_BOX, MAX_PARTICLES_IN_BOX, 1 /* stepSize */);
            _maxParticlesSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _maxParticlesSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            maxParticlesPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            maxParticlesPanel.add( label );
            maxParticlesPanel.add( _maxParticlesSpinner );
        }
        
        // How close photon must be to electron for it to be absorbed
        HorizontalLayoutPanel absorptionClosenessPanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "Photon absorbed when this close:" );
            
            int closeness = AbstractHydrogenAtom.ABSORPTION_CLOSENESS;
            SpinnerModel model = new SpinnerNumberModel( closeness, closeness, closeness * 4, 1 /* stepSize */);
            _absorptionClosenessSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _absorptionClosenessSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            absorptionClosenessPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            absorptionClosenessPanel.add( label );
            absorptionClosenessPanel.add( _absorptionClosenessSpinner );
        }
        
        // Enables debug output from Rutherford Scattering algorithm
        _rutherfordScatteringOutputCheckBox = new JCheckBox( "Rutherford Scattering debug output" );
        _rutherfordScatteringOutputCheckBox.setSelected( RutherfordScattering.DEBUG_OUTPUT_ENABLED );
        
        // Bohr absorption/emission enables
        _bohrAbsorptionCheckBox = new JCheckBox( "absorption enabled", BohrModel.DEBUG_ASBORPTION_ENABLED );
        _bohrEmissionCheckBox = new JCheckBox( "emission enabled", BohrModel.DEBUG_EMISSION_ENABLED );
        _bohrStimulatedEmissionCheckBox = new JCheckBox( "stimulated emission enabled", BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED );
        
        // Min time that electron stays in a state
        HorizontalLayoutPanel minStateTimePanel = new HorizontalLayoutPanel();
        {
            JLabel label = new JLabel( "<html>Min time that electron must spend<br>in a state before it can emit a photon:</html>" );
            JLabel units = new JLabel( "dt" );
            
            int minStateTime = (int) BohrModel.MIN_TIME_IN_STATE;
            SpinnerModel model = new SpinnerNumberModel( minStateTime, 1, 300, 1 /* stepSize */);
            _minStateTimeSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _minStateTimeSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            minStateTimePanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            minStateTimePanel.add( label );
            minStateTimePanel.add( _minStateTimeSpinner );
            minStateTimePanel.add( units );
        }
        
        // Event handling
        EventListener listener = new EventListener();
        _maxParticlesSpinner.addChangeListener( listener );
        _absorptionClosenessSpinner.addChangeListener( listener );
        _rutherfordScatteringOutputCheckBox.addChangeListener( listener );
        _bohrAbsorptionCheckBox.addChangeListener( listener );
        _bohrEmissionCheckBox.addChangeListener( listener );
        _bohrStimulatedEmissionCheckBox.addChangeListener( listener );
        _minStateTimeSpinner.addChangeListener( listener );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        layout.addComponent( maxParticlesPanel, row++, 0 ); 
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( absorptionClosenessPanel, row++, 0 ); 
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _rutherfordScatteringOutputCheckBox, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( new JLabel( "Bohr model:"), row++, 0 );
        layout.addComponent( _bohrAbsorptionCheckBox, row++, 0 );
        layout.addComponent( _bohrEmissionCheckBox, row++, 0 );
        layout.addComponent( _bohrStimulatedEmissionCheckBox, row++, 0 );
        layout.addComponent( minStateTimePanel, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _maxParticlesSpinner ) {
                handleMaxParticlesSpinner();
            }
            else if ( source == _absorptionClosenessSpinner ) {
                handleAbsorptionClosenessSpinner();
            }
            else if ( source == _rutherfordScatteringOutputCheckBox ) {
                handleRutherfordScatteringOutputCheckBox();
            }
            else if ( source == _bohrAbsorptionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrEmissionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _bohrStimulatedEmissionCheckBox ) {
                handleBohrAbsorptionEmission();
            }
            else if ( source == _minStateTimeSpinner ) {
                handleMinStateTime();
            }
        }
    }
    
    private void handleMaxParticlesSpinner() {
        Gun gun = _module.getGun();
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _maxParticlesSpinner.getModel();
        int maxParticles = spinnerModel.getNumber().intValue();
        gun.setMaxParticles( maxParticles );
    }
    
    private void handleAbsorptionClosenessSpinner() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _absorptionClosenessSpinner.getModel();
        int closeness = spinnerModel.getNumber().intValue();
        AbstractHydrogenAtom.ABSORPTION_CLOSENESS = closeness;
    }
    
    private void handleRutherfordScatteringOutputCheckBox() {
        RutherfordScattering.DEBUG_OUTPUT_ENABLED = _rutherfordScatteringOutputCheckBox.isSelected();
    }
    
    private void handleBohrAbsorptionEmission() {
        BohrModel.DEBUG_ASBORPTION_ENABLED = _bohrAbsorptionCheckBox.isSelected();
        BohrModel.DEBUG_EMISSION_ENABLED = _bohrEmissionCheckBox.isSelected();
        BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED = _bohrStimulatedEmissionCheckBox.isSelected();
    }
    
    private void handleMinStateTime() {
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _minStateTimeSpinner.getModel();
        int minStateTime = spinnerModel.getNumber().intValue();
        BohrModel.MIN_TIME_IN_STATE = minStateTime;
    }
}
