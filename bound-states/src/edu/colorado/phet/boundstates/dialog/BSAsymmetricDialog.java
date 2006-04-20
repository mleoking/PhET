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
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSAsymmetricWell;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSAsymmetricDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricDialog extends BSAbstractConfigureDialog implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAsymmetricWell _potential;
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSAsymmetricDialog( Frame parent, BSAsymmetricWell potential ) {
        super( parent, SimStrings.get( "BSAsymmetricDialog.title" ), potential );
        _potential = potential;  
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
    protected JPanel createInputPanel() {
        
        String positionUnits = SimStrings.get( "units.position" );
        String energyUnits = SimStrings.get( "units.energy" );
        
        // Width
        {
            double value = BSConstants.MIN_WELL_WIDTH;
            double min = BSConstants.MIN_WELL_WIDTH;
            double max = BSConstants.MAX_WELL_WIDTH;
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
            double value = BSConstants.MIN_WELL_DEPTH;
            double min = BSConstants.MIN_WELL_DEPTH;
            double max = BSConstants.MAX_WELL_DEPTH;
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
            double value = BSConstants.MIN_WELL_OFFSET;
            double min = BSConstants.MIN_WELL_OFFSET;
            double max = BSConstants.MAX_WELL_OFFSET;
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
        
        return inputPanel;
    }
    
    protected void updateControls() {
        // Sync values
        _widthSlider.setValue( _potential.getWidth() );
        _depthSlider.setValue( _potential.getDepth() );
        _offsetSlider.setValue( _potential.getOffset() );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    private void handleWidthChange() {
        final double width = _widthSlider.getValue();
        _potential.setWidth( width );
    }
    
    private void handleDepthChange() {
        final double depth = _depthSlider.getValue();
        _potential.setDepth( depth );
    }
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        _potential.setOffset( offset );
    }
}
