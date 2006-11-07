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

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.hydrogenatom.control.SliderControl;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.hydrogenatom.model.HAModel;
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
    
    private static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HAModule _module;
    
    private SliderControl _ticksPerPhotonSlider;
    private SliderControl _ticksPerAlphaParticleSlider;
    private JCheckBox _particleLimitCheckBox;
    
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

        EventListener listener = new EventListener();

        // Photon production
        double ticksPerPhoton = _module.getGun().getTicksPerPhoton();
        _ticksPerPhotonSlider = new SliderControl( ticksPerPhoton, 1, 20, 20, 0, 1, "Fired 1 photon every", "ticks", 3, SLIDER_INSETS );
        _ticksPerPhotonSlider.addChangeListener( listener );
              
        // Alpha Particle production
        double ticksPerAlphaParticle = _module.getGun().getTicksPerAlphaParticle();
        _ticksPerAlphaParticleSlider = new SliderControl( ticksPerAlphaParticle, 1, 20, 20, 0, 1, "Fired 1 alpha particle", "ticks", 3, SLIDER_INSETS );
        _ticksPerAlphaParticleSlider.addChangeListener( listener );
        
        // Limits model to one particle at a time
        _particleLimitCheckBox = new JCheckBox( "Limit model to 1 particle" );
        _particleLimitCheckBox.setSelected( HAModel.getParticleLimit() );
        _particleLimitCheckBox.addChangeListener( listener );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        panel.setLayout( layout );
        int row = 0;
        layout.addComponent( _ticksPerPhotonSlider, row++, 0 ); 
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _ticksPerAlphaParticleSlider, row++, 0 );
        layout.addFilledComponent( new JSeparator(), row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _particleLimitCheckBox, row++, 0 );
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _ticksPerPhotonSlider ) {
                handleTicksPerPhotonSlider();
            }
            else if ( source == _ticksPerAlphaParticleSlider ) {
                handleTicksPerAlphaParticleSlider();
            }
            else if ( source == _particleLimitCheckBox ) {
                handleParticleLimitCheckBox();
            }
        }
    }
    
    private void handleTicksPerPhotonSlider() {
        Gun gun = _module.getGun();
        gun.setTicksPerPhoton( _ticksPerPhotonSlider.getValue() );
    }
    
    private void handleTicksPerAlphaParticleSlider() {
        Gun gun = _module.getGun();
        gun.setTicksPerAlphaParticle( _ticksPerAlphaParticleSlider.getValue() ); 
    }
    
    private void handleParticleLimitCheckBox() {
        HAModel.setParticleLimit( _particleLimitCheckBox.isSelected() );
    }
}
