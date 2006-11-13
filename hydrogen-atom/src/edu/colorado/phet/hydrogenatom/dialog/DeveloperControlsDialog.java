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
import java.awt.Toolkit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SwingUtils;
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
    
    private static final int MIN_PARTICLES = 1;
    private static final int MAX_PARTICLES = 40;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAModule _module;
    
    private JSpinner _maxParticlesSpinner;
    private JCheckBox _rutherfordScatteringOutputCheckBox;
    private JCheckBox _bohrAbsorptionCheckBox;
    private JCheckBox _bohrEmissionCheckBox;
    private JCheckBox _bohrStimulatedEmissionCheckBox;
    
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
            SpinnerModel model = new SpinnerNumberModel( maxParticles, MIN_PARTICLES, MAX_PARTICLES, 1 /* stepSize */);
            _maxParticlesSpinner = new JSpinner( model );
            JFormattedTextField tf = ( (JSpinner.DefaultEditor) _maxParticlesSpinner.getEditor() ).getTextField();
            tf.setEditable( false );
            
            maxParticlesPanel.setInsets( new Insets( 5, 5, 5, 5 ) );
            maxParticlesPanel.add( label );
            maxParticlesPanel.add( _maxParticlesSpinner );
        }
        
        // Enables debug output from Rutherford Scattering algorithm
        _rutherfordScatteringOutputCheckBox = new JCheckBox( "Rutherford Scattering debug output" );
        _rutherfordScatteringOutputCheckBox.setSelected( RutherfordScattering.DEBUG_OUTPUT_ENABLED );
        
        // Bohr absorption/emission enables
        _bohrAbsorptionCheckBox = new JCheckBox( "absorption enabled", BohrModel.DEBUG_ASBORPTION_ENABLED );
        _bohrEmissionCheckBox = new JCheckBox( "emission enabled", BohrModel.DEBUG_EMISSION_ENABLED );
        _bohrStimulatedEmissionCheckBox = new JCheckBox( "stimulated emission enabled", BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED );
        
        // Event handling
        EventListener listener = new EventListener();
        _maxParticlesSpinner.addChangeListener( listener );
        _rutherfordScatteringOutputCheckBox.addChangeListener( listener );
        _bohrAbsorptionCheckBox.addChangeListener( listener );
        _bohrEmissionCheckBox.addChangeListener( listener );
        _bohrStimulatedEmissionCheckBox.addChangeListener( listener );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        layout.addComponent( maxParticlesPanel, row++, 0 ); 
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _rutherfordScatteringOutputCheckBox, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( new JLabel( "Bohr model:"), row++, 0 );
        layout.addComponent( _bohrAbsorptionCheckBox, row++, 0 );
        layout.addComponent( _bohrEmissionCheckBox, row++, 0 );
        layout.addComponent( _bohrStimulatedEmissionCheckBox, row++, 0 );
        
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
                handleMaxParticlesSlider();
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
        }
    }
    
    private void handleMaxParticlesSlider() {
        Gun gun = _module.getGun();
        SpinnerNumberModel spinnerModel = (SpinnerNumberModel) _maxParticlesSpinner.getModel();
        int maxParticles = spinnerModel.getNumber().intValue();
        gun.setMaxParticles( maxParticles );
    }
    
    private void handleRutherfordScatteringOutputCheckBox() {
        RutherfordScattering.DEBUG_OUTPUT_ENABLED = _rutherfordScatteringOutputCheckBox.isSelected();
    }
    
    private void handleBohrAbsorptionEmission() {
        BohrModel.DEBUG_ASBORPTION_ENABLED = _bohrAbsorptionCheckBox.isSelected();
        BohrModel.DEBUG_EMISSION_ENABLED = _bohrEmissionCheckBox.isSelected();
        BohrModel.DEBUG_STIMULATED_EMISSION_ENABLED = _bohrStimulatedEmissionCheckBox.isSelected();
    }
}
