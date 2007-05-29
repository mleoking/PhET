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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

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

    private JRadioButton _noChartsRadioButton;
    private JRadioButton _positionHistogramRadioButton;
    private JRadioButton _potentialEnergyChartRadioButton;

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
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleChartChoice();
            }
        };

        // "no charts" choice
        _noChartsRadioButton = new JRadioButton( OTResources.getString( "choice.noCharts" ) );
        _noChartsRadioButton.setFont( controlFont );
        _noChartsRadioButton.addActionListener( actionListener );

        // Position Histogram
        _positionHistogramRadioButton = new JRadioButton( OTResources.getString( "choice.positionHistogram" ) );
        _positionHistogramRadioButton.setFont( controlFont );
        _positionHistogramRadioButton.addActionListener( actionListener );
        _positionHistogramNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( !_positionHistogramNode.getVisible() && _positionHistogramRadioButton.isSelected() ) {
                    _noChartsRadioButton.setSelected( true );
                }
            }
        } );

        // Potential Energy chart
        _potentialEnergyChartRadioButton = new JRadioButton( OTResources.getString( "choice.potentialEnergyChart" ) );
        _potentialEnergyChartRadioButton.setFont( controlFont );
        _potentialEnergyChartRadioButton.addActionListener( actionListener );
        _potentialEnergyNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( !_potentialEnergyNode.getVisible() && _potentialEnergyChartRadioButton.isSelected() ) {
                    _noChartsRadioButton.setSelected( true );
                }
            }
        } );

        ButtonGroup bg = new ButtonGroup();
        bg.add( _noChartsRadioButton );
        bg.add( _positionHistogramRadioButton );
        bg.add( _potentialEnergyChartRadioButton );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( _noChartsRadioButton, row++, 0 );
        layout.addComponent( _positionHistogramRadioButton, row++, 0 );
        layout.addComponent( _potentialEnergyChartRadioButton, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _noChartsRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isNoneSelected() {
        return _noChartsRadioButton.isSelected();
    }
    
    public void setPositionHistogramSelected( boolean b ) {
        _noChartsRadioButton.setSelected( !b );
        _positionHistogramRadioButton.setSelected( b );
        handleChartChoice();
    }
    
    public boolean isPositionHistogramSelected() {
        return _positionHistogramRadioButton.isSelected();
    }
    
    public void setPotentialEnergySelected( boolean b ) {
        _noChartsRadioButton.setSelected( !b );
        _potentialEnergyChartRadioButton.setSelected( b );
        handleChartChoice();
    }
    
    public boolean isPotentialChartSelected() {
        return _potentialEnergyChartRadioButton.isSelected();
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /*
     * Sets the visibility of the charts to match the controls.
     */
    private void handleChartChoice() {
        _positionHistogramNode.setVisible( _positionHistogramRadioButton.isSelected() );
        _potentialEnergyNode.setVisible( _potentialEnergyChartRadioButton.isSelected() );
    }
}
