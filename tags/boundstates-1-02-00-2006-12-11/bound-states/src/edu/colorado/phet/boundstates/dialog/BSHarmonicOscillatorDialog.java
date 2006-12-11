/* Copyright 2006, University of Colorado */

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

import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSHarmonicOscillatorDialog is the dialog for configuring a potential 
 * composed of harmonic oscillator wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorDialog extends BSAbstractConfigureDialog implements ChangeListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SliderControl _offsetSlider;
    private SliderControl _angularFrequencySlider;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSHarmonicOscillatorDialog( Frame parent, BSHarmonicOscillatorPotential potential, BSPotentialSpec potentialSpec ) {
        super( parent, SimStrings.get( "BSHarmonicOscillatorDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( potentialSpec );
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
    protected JPanel createInputPanel( BSPotentialSpec potentialSpec ) {
        
        String angularFrequencyUnits = SimStrings.get( "units.angularFrequency" );
        String energyUnits = SimStrings.get( "units.energy" );

        // Offset
        {
            DoubleRange offsetRange = potentialSpec.getOffsetRange();
            double value = offsetRange.getDefault();
            double min = offsetRange.getMin();
            double max = offsetRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = offsetRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String offsetLabel = SimStrings.get( "label.wellOffset" );
            _offsetSlider = new SliderControl( value, min, max, 
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces, 
                    offsetLabel, energyUnits, columns, SLIDER_INSETS );
            _offsetSlider.setTextEditable( true );
            _offsetSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
        }

        // Angular Frequency
        {
            DoubleRange angularFrequencyRange = potentialSpec.getAngularFrequencyRange();
            double value = angularFrequencyRange.getDefault();
            double min = angularFrequencyRange.getMin();
            double max = angularFrequencyRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = angularFrequencyRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String angularFrequencyLabel = SimStrings.get( "label.wellAngularFrequency" );
            _angularFrequencySlider = new SliderControl( value, min, max, 
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces, 
                    angularFrequencyLabel, angularFrequencyUnits, columns, SLIDER_INSETS );
            _angularFrequencySlider.setTextEditable( true );
            _angularFrequencySlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
        }
        
        // Events
        {
            _offsetSlider.addChangeListener( this );
            _angularFrequencySlider.addChangeListener( this );    
        }
        
        // Layout
        JPanel inputPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
            inputPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( _offsetSlider, row, col );
            row++;
            layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _angularFrequencySlider, row, col );
            row++;
        }
        
        return inputPanel;
    }

    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        BSHarmonicOscillatorPotential potential = (BSHarmonicOscillatorPotential) getPotential();
        _offsetSlider.setValue( potential.getOffset() );
        _angularFrequencySlider.setValue( potential.getAngularFrequency() );
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------

    /**
     * Removes change listeners before disposing of the dialog.
     * If we don't do this, then we'll get events that are caused by
     * the sliders losing focus.
     */
    public void dispose() {
        _offsetSlider.removeChangeListener( this );
        _angularFrequencySlider.removeChangeListener( this );
        super.dispose();
    }
    
    //----------------------------------------------------------------------------
    // ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Dispatches a ChangeEvent to the proper handler method.
     */
    public void stateChanged( ChangeEvent e ) {
        setObservePotential( false );
        {
            if ( e.getSource() == _offsetSlider ) {
                handleOffsetChange();
                adjustClockState( _offsetSlider );
            }
            else if ( e.getSource() == _angularFrequencySlider ) {
                handleAngularFrequencyChange();
                adjustClockState( _angularFrequencySlider );
            }
            else {
                System.err.println( "WARNING: BSHarmonicOscillatorDialog - unsupported event source: " + e.getSource() );
            }
        }
        setObservePotential( true );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        getPotential().setOffset( offset );
    }
    
    private void handleAngularFrequencyChange() {
        final double angularFrequency = _angularFrequencySlider.getValue();
        BSHarmonicOscillatorPotential potential = (BSHarmonicOscillatorPotential) getPotential();
        potential.setAngularFrequency( angularFrequency );
    }

}
