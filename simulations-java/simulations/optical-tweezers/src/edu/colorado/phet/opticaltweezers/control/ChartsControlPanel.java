/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.PNode;

/**
 * ChartsControlPanel is the panel used to control the visibility of charts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChartsControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _positionHistogramNode;
    private PNode _potentialEnergyNode;

    private JCheckBox _positionHistogramCheckBox;
    private JCheckBox _potentialEnergyChartCheckBox;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont font used for the control panel's title
     * @param controlFont font used for the controls 
     * @param positionHistogramNode
     * @param potentialEnergyNode
     */
    public ChartsControlPanel( Font titleFont, Font controlFont, PNode positionHistogramNode, PNode potentialEnergyNode ) {
        super();

        _positionHistogramNode = positionHistogramNode;
        _potentialEnergyNode = potentialEnergyNode;

        JLabel titleLabel = new JLabel( OTResources.getString( "title.chartsControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        // Position Histogram
        _positionHistogramCheckBox = new JCheckBox( OTResources.getString( "label.positionHistogram" ) );
        _positionHistogramCheckBox.setFont( controlFont );
        _positionHistogramCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePositionHistogramCheckBox();
            }
        });
        _positionHistogramNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                _positionHistogramCheckBox.setSelected( _positionHistogramNode.getVisible() );
            }
        } );

        // Potential Energy chart
        _potentialEnergyChartCheckBox = new JCheckBox( OTResources.getString( "label.potentialEnergyChart" ) );
        _potentialEnergyChartCheckBox.setFont( controlFont );
        _potentialEnergyChartCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePotentialEnergyCheckBox();
            }
        });
        _potentialEnergyNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                _potentialEnergyChartCheckBox.setSelected( _potentialEnergyNode.getVisible() );
            }
        } );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( _positionHistogramCheckBox, row++, 0 );
        layout.addComponent( _potentialEnergyChartCheckBox, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _positionHistogramCheckBox.setSelected( _positionHistogramNode.getVisible() );
        _potentialEnergyChartCheckBox.setSelected( _potentialEnergyNode.getVisible() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPositionHistogramSelected( boolean b ) {
        _positionHistogramCheckBox.setSelected( b );
        handlePositionHistogramCheckBox();
    }
    
    public boolean isPositionHistogramSelected() {
        return _positionHistogramCheckBox.isSelected();
    }
    
    public void setPotentialEnergySelected( boolean b ) {
        _potentialEnergyChartCheckBox.setSelected( b );
        handlePotentialEnergyCheckBox();
    }
    
    public boolean isPotentialChartSelected() {
        return _potentialEnergyChartCheckBox.isSelected();
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handlePositionHistogramCheckBox() {
        _positionHistogramNode.setVisible( _positionHistogramCheckBox.isSelected() );
    }
    
    private void handlePotentialEnergyCheckBox() {
        _potentialEnergyNode.setVisible( _potentialEnergyChartCheckBox.isSelected() );
    }
}
