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

import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSCoulomb1DWells;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSCoulomb1DDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DDialog extends BSAbstractConfigureDialog implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SliderControl _offsetSlider;
    private SliderControl _spacingSlider;
    private JSeparator _spacingSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSCoulomb1DDialog( Frame parent, BSCoulomb1DWells potential, 
            BSDoubleRange offsetRange, BSDoubleRange spacingRange ) {
        super( parent, SimStrings.get( "BSCoulomb1DDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( offsetRange, spacingRange );
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
    protected JPanel createInputPanel( BSDoubleRange offsetRange, BSDoubleRange spacingRange ) {
        
        String positionUnits = SimStrings.get( "units.position" );
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

        // Spacing
        {
            double value = spacingRange.getDefault();
            double min = spacingRange.getMin();
            double max = spacingRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String spacingLabel = SimStrings.get( "label.wellSpacing" );
            _spacingSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, spacingLabel, positionUnits, 4, SLIDER_INSETS );
            _spacingSlider.setTextEditable( true );
            _spacingSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _spacingSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) { 
                    handleSpacingChange();
                }
            } );
        };
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _offsetSlider, row, col );
        row++;
        _spacingSeparator = new JSeparator();
        layout.addFilledComponent( _spacingSeparator, row, col, GridBagConstraints.HORIZONTAL );
        row++;
        layout.addComponent( _spacingSlider, row, col );
        row++;
        
        return inputPanel;
    }

    protected void updateControls() {
        
        BSCoulomb1DWells potential = (BSCoulomb1DWells) getPotential();
        
        // Sync values
        _offsetSlider.setValue( potential.getOffset() );
        _spacingSlider.setValue( potential.getSpacing() );
        
        // Visiblility
        _spacingSlider.setVisible( potential.getNumberOfWells() > 1 );
        _spacingSeparator.setVisible( _spacingSlider.isVisible() );
        pack();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        getPotential().setOffset( offset );
    }
    
    private void handleSpacingChange() {
        BSCoulomb1DWells potential = (BSCoulomb1DWells) getPotential();
        final double spacing = _spacingSlider.getValue();
        potential.setSpacing( spacing );
    }

}
