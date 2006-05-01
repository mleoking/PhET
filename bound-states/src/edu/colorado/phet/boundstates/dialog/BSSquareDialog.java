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
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSSquareWells;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSSquareDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDialog extends BSAbstractConfigureDialog implements Observer {

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
            BSDoubleRange offsetRange, BSDoubleRange depthRange, BSDoubleRange widthRange, BSDoubleRange separationSpacing ) {
        super( parent, SimStrings.get( "BSSquareDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( offsetRange, depthRange, widthRange, separationSpacing );
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
    protected JPanel createInputPanel(
            BSDoubleRange offsetRange, BSDoubleRange depthRange, 
            BSDoubleRange widthRange, BSDoubleRange separationSpacing ) {
        
        String positionUnits = SimStrings.get( "units.position" );
        String energyUnits = SimStrings.get( "units.energy" );
        
        // Width
        {
            double value = widthRange.getDefault();
            double min = widthRange.getMin();
            double max = widthRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String widthLabel = SimStrings.get( "label.wellWidth" );
            _widthSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, widthLabel, positionUnits, 4, SLIDER_INSETS );
            _widthSlider.setTextEditable( true );
            _widthSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _widthSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) { 
                    handleWidthChange();
                }
            } );
        }
        
        // Depth
        {
            double value = depthRange.getDefault();
            double min = depthRange.getMin();
            double max = depthRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String depthLabel = SimStrings.get( "label.wellDepth" );
            _depthSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, depthLabel, energyUnits, 4, SLIDER_INSETS );
            _depthSlider.setTextEditable( true );
            _depthSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _depthSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) { 
                    handleDepthChange();
                }
            } );
        }

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

        // Separation
        {
            double value = separationSpacing.getDefault();
            double min = separationSpacing.getMin();
            double max = separationSpacing.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String spacingLabel = SimStrings.get( "label.wellSeparation" );
            _separationSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, spacingLabel, positionUnits, 4, SLIDER_INSETS );
            _separationSlider.setTextEditable( true );
            _separationSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _separationSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) { 
                    handleSeparationChange();
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
        
        return inputPanel;
    }

    protected void updateControls() {

        BSSquareWells potential = (BSSquareWells) getPotential();

        // Sync values
        _widthSlider.setValue( potential.getWidth() );
        _depthSlider.setValue( potential.getDepth() );
        _offsetSlider.setValue( potential.getOffset() );
        _separationSlider.setValue( potential.getSeparation() );

        // Visibility
        _separationSlider.setVisible( potential.getNumberOfWells() > 1 );
        _separationSeparator.setVisible( _separationSlider.isVisible() );
        pack();
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
