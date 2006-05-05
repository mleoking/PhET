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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSSquareWells;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSSquareDialog is the dialog for configuring a potential composed of square wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDialog extends BSAbstractConfigureDialog implements Observer, ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    private SliderControl _separationSlider;
    private JSeparator _separationSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSquareDialog( Frame parent, BSSquareWells potential,
            DoubleRange offsetRange, DoubleRange depthRange, DoubleRange widthRange, DoubleRange separationRange ) {
        super( parent, SimStrings.get( "BSSquareDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( offsetRange, depthRange, widthRange, separationRange );
        createUI( inputPanel );
        updateControls();
    }
    
    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    protected JPanel createInputPanel(
            DoubleRange offsetRange, DoubleRange depthRange, 
            DoubleRange widthRange, DoubleRange separationRange ) {
        
        String positionUnits = SimStrings.get( "units.position" );
        String energyUnits = SimStrings.get( "units.energy" );
 
        // Offset
        {
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

        // Depth
        {
            double value = depthRange.getDefault();
            double min = depthRange.getMin();
            double max = depthRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = depthRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String depthLabel = SimStrings.get( "label.wellDepth" );
            _depthSlider = new SliderControl( value, min, max,
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces,
                    depthLabel, energyUnits, columns, SLIDER_INSETS );
            _depthSlider.setTextEditable( true );
            _depthSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
        }
        
        // Width
        {
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
            _depthSlider.addChangeListener( this );
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
            layout.addComponent( _offsetSlider, row, col );
            row++;
            layout.addFilledComponent( new JSeparator(), row, col, GridBagConstraints.HORIZONTAL );
            row++;
            layout.addComponent( _depthSlider, row, col );
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

        BSSquareWells potential = (BSSquareWells) getPotential();

        // Sync values
        _offsetSlider.setValue( potential.getOffset() );
        _depthSlider.setValue( potential.getDepth() );
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
        _depthSlider.removeChangeListener( this );
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
        if ( e.getSource() == _offsetSlider ) {
            handleOffsetChange();
        }
        else if ( e.getSource() == _depthSlider ) {
            handleDepthChange();
        }
        else if ( e.getSource() == _widthSlider ) {
            handleWidthChange();
        }
        else if ( e.getSource() == _separationSlider ) {
            handleSeparationChange();
        }
        else {
            throw new IllegalArgumentException( "unsupported event source: " + e.getSource() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
   
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        getPotential().setOffset( offset );
    }
    
    private void handleDepthChange() {
        BSSquareWells potential = (BSSquareWells) getPotential();
        final double depth = _depthSlider.getValue();
        potential.setDepth( depth );
    }
    
    private void handleWidthChange() {
        BSSquareWells potential = (BSSquareWells) getPotential();
        final double width = _widthSlider.getValue();
        potential.setWidth( width );
    }
    
    private void handleSeparationChange() {
        BSSquareWells potential = (BSSquareWells) getPotential();
        final double separation = _separationSlider.getValue();
        potential.setSeparation( separation );
    }

}
