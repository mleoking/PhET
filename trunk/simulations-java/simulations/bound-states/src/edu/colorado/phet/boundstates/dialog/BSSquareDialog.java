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
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * BSSquareDialog is the dialog for configuring a potential composed of square wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDialog extends BSAbstractConfigureDialog implements ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _widthControl;
    private LinearValueControl _heightControl;
    private LinearValueControl _offsetControl;
    private LinearValueControl _separationControl;
    private JSeparator _separationSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSquareDialog( Frame parent, BSSquarePotential potential, BSPotentialSpec potentialSpec, boolean offsetControlSupported ) {
        super( parent, BSResources.getString( "BSSquareDialog.title" ), potential );
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
            String offsetLabel = BSResources.getString( "label.wellOffset" );
            String valuePattern = "0.0";
            int columns = 4;
            _offsetControl = new LinearValueControl( min, max, offsetLabel, valuePattern, energyUnits );
            _offsetControl.setValue( value );
            _offsetControl.setUpDownArrowDelta( 0.1 );
            _offsetControl.setTextFieldColumns( columns );
            _offsetControl.setTextFieldEditable( true );
            _offsetControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );
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
        }

        // Separation
        {
            DoubleRange separationRange = potentialSpec.getSeparationRange();
            double value = separationRange.getDefault();
            double min = separationRange.getMin();
            double max = separationRange.getMax();
            String spacingLabel = BSResources.getString( "label.wellSeparation" );
            String valuePattern = "0.00";
            int columns = 4;
            _separationControl = new LinearValueControl( min, max, spacingLabel, valuePattern, positionUnits );
            _separationControl.setValue( value );
            _separationControl.setUpDownArrowDelta( 0.01 );
            _separationControl.setTextFieldColumns( columns );
            _separationControl.setTextFieldEditable( true );
            _separationControl.setNotifyWhileAdjusting( NOTIFY_WHILE_DRAGGING );
        }
        
        // Events
        {
            _offsetControl.addChangeListener( this );
            _heightControl.addChangeListener( this );
            _widthControl.addChangeListener( this );
            _separationControl.addChangeListener( this ); 
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
                layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
                row++;
            }
            layout.addComponent( _heightControl, row, col );
            row++;
            layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _widthControl, row, col );
            row++;
            _separationSeparator = new JSeparator();
            layout.addFilledComponent( _separationSeparator, row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _separationControl, row, col );
            row++;
        }
        
        return inputPanel;
    }

    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        
        BSSquarePotential potential = (BSSquarePotential) getPotential();

        // Sync values
        _offsetControl.setValue( potential.getOffset() );
        _heightControl.setValue( potential.getHeight() );
        _widthControl.setValue( potential.getWidth() );
        _separationControl.setValue( potential.getSeparation() );

        // Visibility
        _separationControl.setVisible( potential.getNumberOfWells() > 1 );
        _separationSeparator.setVisible( _separationControl.isVisible() );
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
        _heightControl.removeChangeListener( this );
        _widthControl.removeChangeListener( this );
        _separationControl.removeChangeListener( this );
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
            else if ( e.getSource() == _heightControl ) {
                handleHeightChange();
                adjustClockState( _heightControl );
            }
            else if ( e.getSource() == _widthControl ) {
                handleWidthChange();
                adjustClockState( _widthControl );
            }
            else if ( e.getSource() == _separationControl ) {
                handleSeparationChange();
                adjustClockState( _separationControl );
            }
            else {
                System.err.println( "WARNING: BSSquareDialog - unsupported event source: " + e.getSource() );
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
    
    private void handleHeightChange() {
        BSSquarePotential potential = (BSSquarePotential) getPotential();
        final double height = _heightControl.getValue();
        potential.setHeight( height );
        setObservePotential( true );
    }
    
    private void handleWidthChange() {
        BSSquarePotential potential = (BSSquarePotential) getPotential();
        final double width = _widthControl.getValue();
        potential.setWidth( width );
    }
    
    private void handleSeparationChange() {
        BSSquarePotential potential = (BSSquarePotential) getPotential();
        final double separation = _separationControl.getValue();
        potential.setSeparation( separation );
    }

}
