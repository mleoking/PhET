// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.control;

import java.awt.GridBagConstraints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.RSResources;
import edu.colorado.phet.rutherfordscattering.model.Gun;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingAtomModule;
import edu.colorado.phet.rutherfordscattering.view.LegendPanel;

/**
 * PlumPuddingAtomControlPanel is the control panel for the "Plum Pudding Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingAtomControlPanel extends RSAbstractControlPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // shows the energy value, should be false for production code
    private static final boolean DEBUG_SHOW_ENERGY_VALUE = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PlumPuddingAtomModule _module;
    private Gun _gun;
    private LinearValueControl _energyControl;
    private ChangeListener _energyListener;
    private JCheckBox _tracesCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public PlumPuddingAtomControlPanel( PlumPuddingAtomModule module ) {
        super( module );
        
        _module = module;
        
        _gun = _module.getGun();
        _gun.addObserver( this );
        
        // Legend
        LegendPanel legendPanel = new LegendPanel( 0.85 /* iconScale */, 
                RSConstants.TITLE_FONT, RSConstants.CONTROL_FONT, RSConstants.TITLED_BORDER_STYLE );

        // Alpha Particles panel
        JPanel alphaParticlesPanel = new JPanel();
        {
            TitledBorder border = new TitledBorder( RSResources.getString( "string.alphaParticleProperties" ) );
            border.setTitleFont( RSConstants.TITLE_FONT );
            border.setBorder( RSConstants.TITLED_BORDER_STYLE );
            alphaParticlesPanel.setBorder( border );

            // Energy control
            {
                double value = _gun.getSpeed();
                double min = _gun.getMinSpeed();
                double max = _gun.getMaxSpeed();
                String valuePattern = "0";
                String label = RSResources.getString( "string.energy" );
                String units = "";
                _energyControl = new LinearValueControl( min, max, label, valuePattern, units );
                _energyControl.setValue( value );
                _energyControl.setFont( RSConstants.CONTROL_FONT );
                _energyControl.setTextFieldEditable( true );
                if ( !DEBUG_SHOW_ENERGY_VALUE ) {
                    _energyControl.setTextFieldVisible( false );
                }
                _energyControl.addTickLabel( min, RSResources.getString( "string.minEnergy" ) );
                _energyControl.addTickLabel( max, RSResources.getString( "string.maxEnergy" ) );
                _energyListener = new ChangeListener() {
                    public void stateChanged( ChangeEvent event ) {
                        _module.removeAllAlphaParticles();
                        _gun.setRunning( false );
                        if ( !_energyControl.isAdjusting() ) {
                            handleEnergyChange();
                            _gun.setRunning( true );
                        }
                    }
                };
                _energyControl.addChangeListener( _energyListener );
            }

            // Traces checkbox
            {
                _tracesCheckBox = new JCheckBox( RSResources.getString( "string.showTraces" ) );
                _tracesCheckBox.setFont( RSConstants.CONTROL_FONT );
                _tracesCheckBox.setSelected( _module.getTracesNode().isEnabled() );
                _tracesCheckBox.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent event ) {
                        handleTracesChanged();
                    }
                } );
            }
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( alphaParticlesPanel );
            alphaParticlesPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( _energyControl, row++, col );
            layout.addFilledComponent( new JSeparator(), row++, col, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _tracesCheckBox, row++, col );
        }
        
        // Layout
        final int panelSpacing = 20;
        addControlFullWidth( legendPanel );
        addVerticalSpace( panelSpacing );
        addControlFullWidth( alphaParticlesPanel );
        addVerticalSpace( panelSpacing );
        JButton resetAllButton = addResetAllButton( module );
        resetAllButton.setFont( RSConstants.CONTROL_FONT );
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {
        _gun.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    public void setTracesEnabled( boolean enabled ) {
        _tracesCheckBox.setSelected( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /*
     * Handles changes to the energy control.
     */
    private void handleEnergyChange() {
        
        _module.removeAllAlphaParticles();
        _gun.setRunning( false );
        
        double speed = _energyControl.getValue();
        _gun.deleteObserver( this );
        _gun.setSpeed( speed );
        _gun.addObserver( this );
        
        if ( !_energyControl.isAdjusting() ) {
            // restart the gun when slider is released
            _gun.setRunning( true );
        }
    }

    /*
     * Handles changes to the "Show traces" checkbox.
     */
    private void handleTracesChanged() {
        boolean enabled = _tracesCheckBox.isSelected();
        _module.getTracesNode().setEnabled( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the controls to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _gun && arg == Gun.PROPERTY_SPEED ) {
            _energyControl.removeChangeListener( _energyListener );
            _energyControl.setValue( _gun.getSpeed() );
            _energyControl.addChangeListener( _energyListener );
        }
    }
}
