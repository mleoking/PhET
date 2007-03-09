/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import java.awt.GridBagConstraints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.model.Gun;
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtom;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;
import edu.colorado.phet.rutherfordscattering.view.LegendPanel;
import edu.colorado.phet.rutherfordscattering.view.RutherfordAtomNode;

/**
 * RutherfordAtomControlPanel is the control panel for the "Rutherford Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordAtomControlPanel extends AbstractControlPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // shows energy value, should be false for production code
    private static final boolean DEBUG_SHOW_ENERGY_VALUE = true;
       
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
        
    private RutherfordAtomModule _module;
    private Gun _gun;
    private RutherfordAtom _atom;
    private RutherfordAtomNode _atomNode;
    private SliderControl _energyControl;
    private ChangeListener _energyListener;
    private JCheckBox _tracesCheckBox;
    private SliderControl _protonsControl;
    private ChangeListener _protonsListener;
    private SliderControl _neutronsControl;
    private ChangeListener _neutronsListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public RutherfordAtomControlPanel( RutherfordAtomModule module ) {
        super( module );
        
        _module = module;
        _module = module;
        
        _gun = _module.getGun();
        _gun.addObserver( this );
        
        _atom = _module.getAtom();
        _atom.addObserver( this );
        
        _atomNode = _module.getAtomNode();
        
        // Legend
        LegendPanel legendPanel = new LegendPanel( 0.85 /* iconScale */, RSConstants.TITLE_FONT, RSConstants.CONTROL_FONT );
        
        // Alpha Particles panel
        JPanel alphaParticlesPanel = new JPanel();
        {
            TitledBorder border = new TitledBorder( SimStrings.get( "label.alphaParticleProperties" ) );
            border.setTitleFont( RSConstants.TITLE_FONT );
            alphaParticlesPanel.setBorder( border );

            // Energy control
            {
                double value = _gun.getSpeed();
                double min = _gun.getMinSpeed();
                double max = _gun.getMaxSpeed();
                double tickSpacing = max - min;
                int tickDecimalPlaces = 0;
                int valueDecimalPlaces = 1;
                String label = SimStrings.get( "label.energy" );
                String units = "";
                int columns = 3;
                _energyControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
                _energyControl.setTextFieldEditable( true );
                if ( !DEBUG_SHOW_ENERGY_VALUE ) {
                    _energyControl.setTextFieldVisible( false );
                }
                _energyControl.setMinMaxLabels( SimStrings.get( "label.minEnergy" ), SimStrings.get( "label.maxEnergy" ) );
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
                _tracesCheckBox = new JCheckBox( SimStrings.get( "label.showTraces" ) );
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
        
        JPanel atomPanel = new JPanel();
        {
            TitledBorder border = new TitledBorder( SimStrings.get( "label.atomProperties" ) );
            border.setTitleFont( RSConstants.TITLE_FONT );
            atomPanel.setBorder( border );

            // Number of protons
            {
                int value = _atom.getNumberOfProtons();
                int min = _atom.getMinNumberOfProtons();
                int max = _atom.getMaxNumberOfProtons();
                int tickSpacing = max - min;
                int tickDecimalPlaces = 0;
                int valueDecimalPlaces = 0;
                String label = SimStrings.get( "label.numberOfProtons" );
                String units = "";
                int columns = 3;
                _protonsControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
                _protonsControl.setTextFieldEditable( true );
                _protonsListener = new ChangeListener() {

                    public void stateChanged( ChangeEvent event ) {
                        handleProtonsChange();
                    }
                };
                _protonsControl.addChangeListener( _protonsListener );
            }

            // Number of neutrons
            {
                int value = _atom.getNumberOfNeutrons();
                int min = _atom.getMinNumberOfNeutrons();
                int max = _atom.getMaxNumberOfNeutrons();
                int tickSpacing = max - min;
                int tickDecimalPlaces = 0;
                int valueDecimalPlaces = 0;
                String label = SimStrings.get( "label.numberOfNeutrons" );
                String units = "";
                int columns = 3;
                _neutronsControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
                _neutronsControl.setTextFieldEditable( true );
                _neutronsListener = new ChangeListener() {

                    public void stateChanged( ChangeEvent event ) {
                        handleNeutronsChange();
                    }
                };
                _neutronsControl.addChangeListener( _neutronsListener );
            }
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( atomPanel );
            atomPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( _protonsControl, row++, col );
            layout.addFilledComponent( new JSeparator(), row++, col, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _neutronsControl, row++, col );
        }
        
        // Layout
        final int panelSpacing = 20;
        addControlFullWidth( legendPanel );
        addVerticalSpace( panelSpacing );
        addControlFullWidth( alphaParticlesPanel );
        addVerticalSpace( panelSpacing );
        addControlFullWidth(atomPanel );
        addVerticalSpace( panelSpacing );
        addResetButton();
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {
        _gun.deleteObserver( this );
        _atom.deleteObserver( this );
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
    
    /*
     * Handles changes to the "number of protons" control.
     */
    private void handleProtonsChange() {
        
        _module.removeAllAlphaParticles();
        _gun.setRunning( false );
        _atomNode.setOutlineModeEnabled( true );
        
        int numberOfProtons = (int) _protonsControl.getValue();
        _atom.deleteObserver( this );
        _atom.setNumberOfProtons( numberOfProtons );
        _atom.addObserver(  this );
        
        if ( !_protonsControl.isAdjusting() ) {
            _gun.setRunning(  true  );
            _atomNode.setOutlineModeEnabled( false );
        }
    }

    /*
     * Handles changes to the "number of neutrons" control.
     */
    private void handleNeutronsChange() {
        
        _module.removeAllAlphaParticles();
        _gun.setRunning( false );
        _atomNode.setOutlineModeEnabled( true );
        
        int numberOfNeutrons = (int) _neutronsControl.getValue();
        _atom.deleteObserver( this );
        _atom.setNumberOfNeutrons( numberOfNeutrons );
        _atom.addObserver( this );
        
        if ( !_neutronsControl.isAdjusting() ) {
            _gun.setRunning(  true  );
            _atomNode.setOutlineModeEnabled( false );
        }
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
        else if ( o == _atom ) {
            if ( arg == RutherfordAtom.PROPERTY_NUMBER_OF_PROTONS ) {
                _protonsControl.removeChangeListener( _protonsListener );
                _protonsControl.setValue(  _atom.getNumberOfProtons() );
                _protonsControl.addChangeListener( _protonsListener );
            }
            else if ( arg == RutherfordAtom.PROPERTY_NUMBER_OF_NEUTRONS ) {
                _neutronsControl.removeChangeListener( _neutronsListener );
                _neutronsControl.setValue(  _atom.getNumberOfNeutrons() );
                _neutronsControl.addChangeListener( _neutronsListener );
            }
        }
    }
}
