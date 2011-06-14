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
import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * BSAsymmetricDialog is the dialog for configuring a potential composed of asymmetric wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricDialog extends BSAbstractConfigureDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _offsetControl;
    private LinearValueControl _heightControl;
    private LinearValueControl _widthControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSAsymmetricDialog( Frame parent, BSAsymmetricPotential potential, BSAbstractModuleSpec moduleSpec ) {
        super( parent, BSResources.getString( "BSAsymmetricDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( moduleSpec );
        createUI( inputPanel );
        updateControls();
    }
    
    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel( BSAbstractModuleSpec moduleSpec ) {
        
        BSPotentialSpec potentialSpec = moduleSpec.getAsymmetricSpec();
        String positionUnits = BSResources.getString( "units.position" );
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
        
        // Height
        {
            DoubleRange heightRange = potentialSpec.getHeightRange();
            double value = heightRange.getDefault();
            double min = heightRange.getMin();
            double max = heightRange.getMax();
            String heightLabel = BSResources.getString( "label.wellHeight" );
            String valuePattern = "0.0";
            int columns = 4;
            _heightControl = new LinearValueControl( min, max, heightLabel, valuePattern, energyUnits );
            _heightControl.setValue( value );
            _heightControl.setUpDownArrowDelta( 0.1 );
            _heightControl.setTextFieldColumns( columns );
            _heightControl.setTextFieldEditable( true );
            _heightControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );
            _heightControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleHeightChange();
                }
            });
        }
        
        // Width
        {
            DoubleRange widthRange = potentialSpec.getWidthRange();
            double value = widthRange.getDefault();
            double min = widthRange.getMin();
            double max = widthRange.getMax();
            String widthLabel = BSResources.getString( "label.wellWidth" );
            String valuePattern = "0.0";
            int columns = 4;
            _widthControl = new LinearValueControl( min, max, widthLabel, valuePattern, positionUnits );
            _widthControl.setValue( value );
            _widthControl.setUpDownArrowDelta( 0.1 );
            _widthControl.setTextFieldColumns( columns );
            _widthControl.setTextFieldEditable( true );
            _widthControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );
            _widthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleWidthChange();
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
            }
            layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _heightControl, row, col );
            row++;
            layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _widthControl, row, col );
            row++;
        }
        
        return inputPanel;
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        // Sync values
        BSAsymmetricPotential potential = (BSAsymmetricPotential) getPotential();
        if ( _offsetControl != null ) {
            _offsetControl.setValue( potential.getOffset() );
        }
        _heightControl.setValue( potential.getHeight() );
        _widthControl.setValue( potential.getWidth() );
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
    
    private void handleHeightChange() {
        final double height = _heightControl.getValue();
        setObservePotential( false );
        ((BSAsymmetricPotential) getPotential()).setHeight( height );
        setObservePotential( true );
        adjustClockState( _heightControl );
    }
    
    private void handleWidthChange() {
        final double width = _widthControl.getValue();
        setObservePotential( false );
        ((BSAsymmetricPotential) getPotential()).setWidth( width );
        setObservePotential( true );
        adjustClockState( _widthControl );
    }
}
