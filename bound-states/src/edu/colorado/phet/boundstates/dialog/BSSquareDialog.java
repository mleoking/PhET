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
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


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
    
    private SliderControl _widthSlider;
    private SliderControl _heightSlider;
    private SliderControl _offsetSlider;
    private SliderControl _separationSlider;
    private JSeparator _separationSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSquareDialog( Frame parent, BSSquarePotential potential, BSPotentialSpec potentialSpec, boolean offsetControlSupported ) {
        super( parent, SimStrings.get( "BSSquareDialog.title" ), potential );
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
        
        String positionUnits = SimStrings.get( "units.position" );
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

        // Height
        {
            DoubleRange heightRange = potentialSpec.getHeightRange();
            double value = heightRange.getDefault();
            double min = heightRange.getMin();
            double max = heightRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = heightRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String heightLabel = SimStrings.get( "label.wellHeight" );
            _heightSlider = new SliderControl( value, min, max,
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces,
                    heightLabel, energyUnits, columns, SLIDER_INSETS );
            _heightSlider.setTextEditable( true );
            _heightSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
        }
        
        // Width
        {
            DoubleRange widthRange = potentialSpec.getWidthRange();
            double value = widthRange.getDefault();
            double min = widthRange.getMin();
            double max = widthRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = widthRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String widthLabel = SimStrings.get( "label.wellWidth" );
            _widthSlider = new SliderControl( value, min, max,
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces,
                    widthLabel, positionUnits, columns, SLIDER_INSETS );
            _widthSlider.setTextEditable( true );
            _widthSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
        }

        // Separation
        {
            DoubleRange separationRange = potentialSpec.getSeparationRange();
            double value = separationRange.getDefault();
            double min = separationRange.getMin();
            double max = separationRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = separationRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String spacingLabel = SimStrings.get( "label.wellSeparation" );
            _separationSlider = new SliderControl( value, min, max,
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces,
                    spacingLabel, positionUnits, columns, SLIDER_INSETS );
            _separationSlider.setTextEditable( true );
            _separationSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
        }
        
        // Events
        {
            _offsetSlider.addChangeListener( this );
            _heightSlider.addChangeListener( this );
            _widthSlider.addChangeListener( this );
            _separationSlider.addChangeListener( this ); 
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
                layout.addComponent( _offsetSlider, row, col );
                row++;
                layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
                row++;
            }
            layout.addComponent( _heightSlider, row, col );
            row++;
            layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _widthSlider, row, col );
            row++;
            _separationSeparator = new JSeparator();
            layout.addFilledComponent( _separationSeparator, row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _separationSlider, row, col );
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
        _offsetSlider.setValue( potential.getOffset() );
        _heightSlider.setValue( potential.getHeight() );
        _widthSlider.setValue( potential.getWidth() );
        _separationSlider.setValue( potential.getSeparation() );

        // Visibility
        _separationSlider.setVisible( potential.getNumberOfWells() > 1 );
        _separationSeparator.setVisible( _separationSlider.isVisible() );
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
        _offsetSlider.removeChangeListener( this );
        _heightSlider.removeChangeListener( this );
        _widthSlider.removeChangeListener( this );
        _separationSlider.removeChangeListener( this );
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
            else if ( e.getSource() == _heightSlider ) {
                handleHeightChange();
                adjustClockState( _heightSlider );
            }
            else if ( e.getSource() == _widthSlider ) {
                handleWidthChange();
                adjustClockState( _widthSlider );
            }
            else if ( e.getSource() == _separationSlider ) {
                handleSeparationChange();
                adjustClockState( _separationSlider );
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
        final double offset = _offsetSlider.getValue();
        getPotential().setOffset( offset ); 
    }
    
    private void handleHeightChange() {
        BSSquarePotential potential = (BSSquarePotential) getPotential();
        final double height = _heightSlider.getValue();
        potential.setHeight( height );
        setObservePotential( true );
    }
    
    private void handleWidthChange() {
        BSSquarePotential potential = (BSSquarePotential) getPotential();
        final double width = _widthSlider.getValue();
        potential.setWidth( width );
    }
    
    private void handleSeparationChange() {
        BSSquarePotential potential = (BSSquarePotential) getPotential();
        final double separation = _separationSlider.getValue();
        potential.setSeparation( separation );
    }

}
