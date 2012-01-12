// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * BSHarmonicOscillatorDialog is the dialog for configuring a potential 
 * composed of harmonic oscillator wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorDialog extends BSAbstractConfigureDialog {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _offsetControl;
    private LinearValueControl _angularFrequencyControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSHarmonicOscillatorDialog( Frame parent, BSHarmonicOscillatorPotential potential, BSAbstractModuleSpec moduleSpec ) {
        super( parent, BSResources.getString( "BSHarmonicOscillatorDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( moduleSpec );
        createUI( inputPanel );
        updateControls();
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    protected JPanel createInputPanel( BSAbstractModuleSpec moduleSpec ) {
        
        BSPotentialSpec potentialSpec = moduleSpec.getHarmonicOscillatorSpec();
        String angularFrequencyUnits = BSResources.getString( "units.angularFrequency" );
        String energyUnits = BSResources.getString( "units.energy" );

        // Offset
        if ( moduleSpec.isOffsetControlSupported() ) {
            DoubleRange offsetRange = potentialSpec.getOffsetRange();
            double value = offsetRange.getDefault();
            double min = offsetRange.getMin();
            double max = offsetRange.getMax();
            String offsetLabel = BSResources.getString( "label.wellOffset" );
            String valuePattern = "0.0";
            int columns = 4;
            _offsetControl = new LinearValueControl( min, max, offsetLabel, valuePattern, energyUnits );
            _offsetControl.setValue( value );
            _offsetControl.setUpDownArrowDelta( 0.1 );
            _offsetControl.setTextFieldColumns( columns );
            _offsetControl.setTextFieldEditable( true );
            _offsetControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING ); 
            _offsetControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleOffsetChange();
                }
            });
        }

        // Angular Frequency
        {
            DoubleRange angularFrequencyRange = potentialSpec.getAngularFrequencyRange();
            double value = angularFrequencyRange.getDefault();
            double min = angularFrequencyRange.getMin();
            double max = angularFrequencyRange.getMax();
            String angularFrequencyLabel = BSResources.getString( "label.wellAngularFrequency" );
            String valuePattern = "0.0";
            int columns = 4;
            _angularFrequencyControl = new LinearValueControl( min, max, angularFrequencyLabel, valuePattern, angularFrequencyUnits );
            _angularFrequencyControl.setValue( value );
            _angularFrequencyControl.setUpDownArrowDelta( 0.1 );
            _angularFrequencyControl.setTextFieldColumns( columns );
            _angularFrequencyControl.setTextFieldEditable( true );
            _angularFrequencyControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );
            _angularFrequencyControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleAngularFrequencyChange();
                }
            });
        }
        
        // Layout
        JPanel inputPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
            inputPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            if ( _offsetControl != null ) {
                layout.addComponent( _offsetControl, row, col );
                row++;
                layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
                row++;
            }
            layout.addComponent( _angularFrequencyControl, row, col );
            row++;
        }
        
        return inputPanel;
    }

    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        BSHarmonicOscillatorPotential potential = (BSHarmonicOscillatorPotential) getPotential();
        if ( _offsetControl != null ) {
            _offsetControl.setValue( potential.getOffset() );
        }
        _angularFrequencyControl.setValue( potential.getAngularFrequency() );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleOffsetChange() {
        final double offset = _offsetControl.getValue();
        setObservePotential( false );
        getPotential().setOffset( offset );
        setObservePotential( true );
        adjustClockState( _offsetControl );
    }
    
    private void handleAngularFrequencyChange() {
        final double angularFrequency = _angularFrequencyControl.getValue();
        setObservePotential( false );
        ((BSHarmonicOscillatorPotential) getPotential()).setAngularFrequency( angularFrequency );
        setObservePotential( true );
        adjustClockState( _angularFrequencyControl );
    }

}
