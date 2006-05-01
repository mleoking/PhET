/* Copyright 2005, University of Colorado */

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
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.security.krb5.internal.ccache.an;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorWell;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSHarmonicOscillatorDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorDialog extends BSAbstractConfigureDialog implements Observer {
    
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
    public BSHarmonicOscillatorDialog( Frame parent, BSHarmonicOscillatorWell potential,
            BSDoubleRange offsetRange, BSDoubleRange angularFrequencyRange ) {
        super( parent, SimStrings.get( "BSHarmonicOscillatorDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( offsetRange, angularFrequencyRange );
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
    protected JPanel createInputPanel( BSDoubleRange offsetRange, BSDoubleRange angularFrequencyRange ) {
        
        String angularFrequencyUnits = SimStrings.get( "units.angularFrequency" );
        String energyUnits = SimStrings.get( "units.energy" );

        // Offset
        {
            double value = offsetRange.getDefault();
            double min = offsetRange.getMin();
            double max = offsetRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String offsetLabel = SimStrings.get( "label.wellOffset" );
            _offsetSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, offsetLabel, energyUnits, 4, SLIDER_INSETS );
            _offsetSlider.setTextEditable( true );
            _offsetSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _offsetSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) { 
                    handleOffsetChange();
                }
            } );
        }

        // Angular Frequency
        {
            double value = angularFrequencyRange.getDefault();
            double min = angularFrequencyRange.getMin();
            double max = angularFrequencyRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String angularFrequencyLabel = SimStrings.get( "label.wellAngularFrequency" );
            _angularFrequencySlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, 
                    angularFrequencyLabel, angularFrequencyUnits, 4, SLIDER_INSETS );
            _angularFrequencySlider.setTextEditable( true );
            _angularFrequencySlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _angularFrequencySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) { 
                    handleAngularFrequencyChange();
                }
            } );
        }
        
        JPanel inputPanel = new JPanel();
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
        
        return inputPanel;
    }

    protected void updateControls() {
        BSHarmonicOscillatorWell potential = (BSHarmonicOscillatorWell) getPotential();
        _offsetSlider.setValue( potential.getOffset() );
        _angularFrequencySlider.setValue( potential.getAngularFrequency() );
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
        BSHarmonicOscillatorWell potential = (BSHarmonicOscillatorWell) getPotential();
        potential.setAngularFrequency( angularFrequency );
    }

}
