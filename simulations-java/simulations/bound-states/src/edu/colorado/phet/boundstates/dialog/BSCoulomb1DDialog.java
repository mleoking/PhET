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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
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
public class BSCoulomb1DDialog extends BSAbstractConfigureDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _offsetControl;
    private LinearValueControl _spacingControl;
    private JSeparator _spacingSeparator;
    private JLabel _nothingToConfigureLabel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSCoulomb1DDialog( Frame parent, BSCoulomb1DPotential potential, BSAbstractModuleSpec moduleSpec ) {
        super( parent, BSResources.getString( "BSCoulomb1DDialog.title" ), potential );
        System.out.println( "BSCoulomb1DDialog" );
        JPanel inputPanel = createInputPanel( moduleSpec );
        createUI( inputPanel );
        updateControls();
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    protected JPanel createInputPanel( BSAbstractModuleSpec moduleSpec ) {
        
        BSPotentialSpec potentialSpec = moduleSpec.getCoulomb1DSpec();
        String positionUnits = BSResources.getString( "units.position" );
        String energyUnits = BSResources.getString( "units.energy" );

        // Offset
        if ( moduleSpec.isOffsetControlSupported() ) {
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
            _offsetControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    handleOffsetChange();
                }
            } );
        }

        // Spacing
        if ( moduleSpec.getNumberOfWellsRange().getMax() > 1 ) {
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
            _spacingControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    handleSpacingChange();
                }
            } );
        };
        
        // "Nothing to configure" label
        _nothingToConfigureLabel = new JLabel( BSResources.getString( "label.nothingToConfigure" ) );
        
        // Layout
        JPanel inputPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
            inputPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            if ( _offsetControl != null ) {
                layout.addComponent( _offsetControl, row++, col );
            }
            if ( _offsetControl != null && _spacingControl != null ) {
                _spacingSeparator = new JSeparator();
                layout.addFilledComponent( _spacingSeparator, row++, col, GridBagConstraints.HORIZONTAL );
            }
            if ( _spacingControl != null ) {
                layout.addComponent( _spacingControl, row++, col );
            }
            if ( _nothingToConfigureLabel != null ) {
                layout.addComponent( _nothingToConfigureLabel, row++, col );
            }
            row++;
        }
        
        return inputPanel;
    }

    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential) getPotential();
        
        if ( _offsetControl != null ) {
            _offsetControl.setValue( potential.getOffset() );
        }
        
        if ( _spacingControl != null ) {
            _spacingControl.setValue( potential.getSpacing() );
            _spacingControl.setVisible( potential.getNumberOfWells() > 1 );
            if ( _spacingSeparator != null ) {
                _spacingSeparator.setVisible( _spacingControl.isVisible() );
            }
        }
        
        _nothingToConfigureLabel.setVisible( _offsetControl == null && (_spacingControl == null || !_spacingControl.isVisible() ) );

        pack();
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
    
    private void handleSpacingChange() {
        final double spacing = _spacingControl.getValue();
        setObservePotential( false );
        ((BSCoulomb1DPotential) getPotential()).setSpacing( spacing );
        setObservePotential( true );
        adjustClockState( _spacingControl );
    }

}
