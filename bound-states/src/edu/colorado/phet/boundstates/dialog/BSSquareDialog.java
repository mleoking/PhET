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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.SliderControl;
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
    
    private BSSquareWells _potential;
    private double _separation;
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    private SliderControl _separationSlider;
    private JSeparator _separationSeparator;
    
    private boolean _ignoreUpdate;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSquareDialog( Frame parent, BSSquareWells potential ) {
        super( parent, SimStrings.get( "BSSquareDialog.title" ), potential );
        _potential = potential;
        _separation = _potential.getSpacing() - _potential.getWidth();
        _ignoreUpdate = false;
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

        // Separation
        {
            double value = BSConstants.MIN_WELL_SEPARATION;
            double min = BSConstants.MIN_WELL_SEPARATION;
            double max = BSConstants.MAX_WELL_SEPARATION;
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

        if ( !_ignoreUpdate ) {
            // Sync values
            _widthSlider.setValue( _potential.getWidth() );
            _depthSlider.setValue( _potential.getDepth() );
            _offsetSlider.setValue( _potential.getOffset() );
            _separationSlider.setValue( _separation );

            // Visibility
            _separationSlider.setVisible( _potential.getNumberOfWells() > 1 );
            _separationSeparator.setVisible( _separationSlider.isVisible() );
            pack();
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
   
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        _potential.setOffset( offset );
    }
    
    private void handleDepthChange() {
        final double depth = _depthSlider.getValue();
        _potential.setDepth( depth );
    }
    
    private void handleWidthChange() {
        final double width = _widthSlider.getValue();
        final double spacing = _separation + width;
        _ignoreUpdate = true; // because we're changing 2 properties...
        _potential.setWidth( width );
        _ignoreUpdate = false;
        _potential.setSpacing( spacing );
    }
    
    private void handleSeparationChange() {
        _separation = _separationSlider.getValue();
        final double width = _potential.getWidth();
        final double spacing = _separation + width;
        _potential.setSpacing( spacing );
    }

}
