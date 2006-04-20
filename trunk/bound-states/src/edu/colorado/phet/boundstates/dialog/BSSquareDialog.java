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
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    private SliderControl _spacingSlider;
    private JSeparator _spacingSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSquareDialog( Frame parent, BSSquareWells potential ) {
        super( parent, SimStrings.get( "BSSquareDialog.title" ), potential );
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

        // Spacing
        {
            double value = BSConstants.MIN_WELL_SPACING;
            double min = BSConstants.MIN_WELL_SPACING;
            double max = BSConstants.MAX_WELL_SPACING;
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
        _spacingSeparator = new JSeparator();
        layout.addFilledComponent( _spacingSeparator, row, col, GridBagConstraints.HORIZONTAL );
        row++;
        layout.addComponent( _spacingSlider, row, col );
        row++;
        
        return inputPanel;
    }

    protected void updateControls() {

        // Sync values
        _widthSlider.setValue( _potential.getWidth() );
        _depthSlider.setValue( _potential.getDepth() );
        _offsetSlider.setValue( _potential.getOffset() );
        _spacingSlider.setValue( _potential.getSpacing() );
    
        // Visibility
        _spacingSlider.setVisible( _potential.getNumberOfWells() > 1 );
        _spacingSeparator.setVisible( _spacingSlider.isVisible() );
        pack();
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
        _potential.setWidth( width );
        final double spacing = _potential.getSpacing();
        if ( width > spacing - BSConstants.MIN_WELL_SPACING ) {
            _potential.setSpacing( width + BSConstants.MIN_WELL_SPACING );
        }
    }
    
    private void handleSpacingChange() {
        final double spacing = _spacingSlider.getValue();
        _potential.setSpacing( spacing );
        final double width = _potential.getWidth();
        if ( width > spacing - BSConstants.MIN_WELL_SPACING ) {
            _potential.setWidth( spacing - BSConstants.MIN_WELL_SPACING );
        }
    }

}
