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

import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * BSCoulomb1DDialog is the dialog for configuring a potential composed of 1-D Coulomb wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DDialog extends BSAbstractConfigureDialog implements ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _offsetControl;
    private LinearValueControl _spacingControl;
    private JSeparator _spacingSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSCoulomb1DDialog( Frame parent, BSCoulomb1DPotential potential, BSPotentialSpec potentialSpec, boolean offsetControlSupported ) {
        super( parent, BSResources.getString( "BSCoulomb1DDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( potentialSpec, offsetControlSupported );
        createUI( inputPanel );
        updateControls();
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    protected JPanel createInputPanel( BSPotentialSpec potentialSpec, boolean offsetControlSupported ) {
        
        String positionUnits = BSResources.getString( "units.position" );
        String energyUnits = BSResources.getString( "units.energy" );

        // Offset
        {
            DoubleRange offsetRange = potentialSpec.getOffsetRange();
            double value = offsetRange.getDefault();
            double min = offsetRange.getMin();
            double max = offsetRange.getMax();
            String valuePattern = "0.0";
            int columns = 4;
            String offsetLabel = BSResources.getString( "label.wellOffset" );
            _offsetControl = new LinearValueControl( min, max, offsetLabel, valuePattern, energyUnits );
            _offsetControl.setValue( value );
            _offsetControl.setUpDownArrowDelta( 0.1 );
            _offsetControl.setTextFieldColumns( columns );
            _offsetControl.setTextFieldEditable( true );
            _offsetControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );   
        }

        // Spacing
        {
            DoubleRange spacingRange = potentialSpec.getSpacingRange();
            double value = spacingRange.getDefault();
            double min = spacingRange.getMin();
            double max = spacingRange.getMax();
            String valuePattern = "0.00";
            int columns = 4;
            String spacingLabel = BSResources.getString( "label.wellSpacing" );
            _spacingControl = new LinearValueControl( min, max, spacingLabel, valuePattern, positionUnits );
            _spacingControl.setValue( value );
            _spacingControl.setUpDownArrowDelta( 0.01 );
            _spacingControl.setTextFieldColumns( columns );
            _spacingControl.setTextFieldEditable( true );
            _spacingControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );
            
            _spacingSeparator = new JSeparator();
        };
        
        // Events
        {
            _offsetControl.addChangeListener( this );
            _spacingControl.addChangeListener( this );
        }
        
        // Layout
        JPanel inputPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
            inputPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            if ( offsetControlSupported ) {
                layout.addComponent( _offsetControl, row, col );
                row++;
                layout.addFilledComponent( _spacingSeparator, row, col, GridBagConstraints.HORIZONTAL );
                row++;
            }
            layout.addComponent( _spacingControl, row, col );
            row++;
        }
        
        return inputPanel;
    }

    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential) getPotential();
        
        // Sync values
        _offsetControl.setValue( potential.getOffset() );
        _spacingControl.setValue( potential.getSpacing() );
        
        // Visiblility
        _spacingControl.setVisible( potential.getNumberOfWells() > 1 );
        _spacingSeparator.setVisible( _spacingControl.isVisible() );
        pack();
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
        _offsetControl.removeChangeListener( this );
        _spacingControl.removeChangeListener( this );
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
            if ( e.getSource() == _offsetControl ) {
                handleOffsetChange();
                adjustClockState( _offsetControl );
            }
            else if ( e.getSource() == _spacingControl ) {
                handleSpacingChange();
                adjustClockState( _spacingControl );
            }
            else {
                System.err.println( "WARNING: BSCoulomb3DDialog - unsupported event source: " + e.getSource() );
            }
        }
        setObservePotential( true );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleOffsetChange() {
        final double offset = _offsetControl.getValue();
        getPotential().setOffset( offset );
    }
    
    private void handleSpacingChange() {
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential) getPotential();
        final double spacing = _spacingControl.getValue();
        potential.setSpacing( spacing );
    }

}
