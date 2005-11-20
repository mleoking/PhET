/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotentialEnergy;
import edu.colorado.phet.quantumtunneling.model.BarrierPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.view.EnergyPlot;


/**
 * ConfigureEnergyDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConfigureEnergyDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension CHART_SIZE = new Dimension( 350, 150 );
    public static final Font AXES_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    public static final Color BARRIER_PROPERTIES_COLOR = Color.RED;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TotalEnergy _totalEnergy;
    private AbstractPotentialEnergy _potentialEnergy;
    private JButton _applyButton, _closeButton;
    private EventListener _listener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param app the application
     */
    public ConfigureEnergyDialog( Frame frame, TotalEnergy totalEnergy, AbstractPotentialEnergy potentialEnergy ) {
        super( frame );
        
        super.setTitle( SimStrings.get( "dialog.configureEnergy") );
        super.setModal( false );
        super.setResizable( false );
        
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        
        _listener = new EventListener();
        
        createUI( frame );
        
        addWindowListener( new WindowAdapter() {  
            public void windowClosed( WindowEvent event ) {
                cleanup();
            }
        } );
    }
    
    private void cleanup() {
        _totalEnergy.deleteObserver( this );
        _potentialEnergy.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Internal initializers
    //----------------------------------------------------------------------------
    
    /**
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        
        JPanel chartPanel = createChartPanel();
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( chartPanel, BorderLayout.NORTH );
        panel.add( inputPanel, BorderLayout.CENTER );
        panel.add( actionsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( panel );
        this.pack();
        this.setLocationRelativeTo( parent );
    }
    
    /**
     * Creates the dialog's chart panel.
     * 
     * @return the chart panel
     */
    private JPanel createChartPanel() {
        // Chart
        EnergyPlot energyPlot = new EnergyPlot();
        energyPlot.setAxesFont( AXES_FONT );
        JFreeChart chart = new JFreeChart( null /*title*/, null /*font*/, energyPlot, false /* no legend */ );
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( CHART_SIZE );
        return chartPanel;
    }
    
    /**
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
       
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( inputPanel );
        inputPanelLayout.setMinimumWidth( 3, 60 );
        inputPanel.setLayout( inputPanelLayout );
        int row = 0;

        // Total Energy
        {
            JLabel teLabel = new JLabel( "Total Energy:" );
            teLabel.setForeground( QTConstants.TOTAL_ENERGY_COLOR );
            SpinnerModel model = new SpinnerNumberModel( 8.00, QTConstants.ENERGY_RANGE.getLowerBound(), 
                    QTConstants.ENERGY_RANGE.getUpperBound(), 0.01 );
            JSpinner teSpinner = new JSpinner( model );
            JLabel teUnits = new JLabel( "eV" );
            inputPanelLayout.addAnchoredComponent( teLabel, row, 0, GridBagConstraints.EAST );
            inputPanelLayout.addComponent( teSpinner, row, 1 );
            inputPanelLayout.addComponent( teUnits, row, 2 );
            row++;
        }
        
        // Potential Energy for each region...
        {
            JLabel peTitle = new JLabel( "Potential Energy:" );
            peTitle.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
            inputPanelLayout.addAnchoredComponent( peTitle, row, 0, GridBagConstraints.EAST );
            row++;
            int numberOfRegions = _potentialEnergy.getNumberOfRegions();
            for ( int i = 0; i < numberOfRegions; i++ ) {
                JLabel peLabel = new JLabel( "<html>R<sub>" + ( i + 1 ) + "</sub>:</html>" );
                peLabel.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                SpinnerModel model = new SpinnerNumberModel( 5.00, QTConstants.ENERGY_RANGE.getLowerBound(), 
                        QTConstants.ENERGY_RANGE.getUpperBound(), 0.01 );
                JSpinner peSpinner = new JSpinner( model );
                JLabel peUnits = new JLabel( "eV" );
                inputPanelLayout.addAnchoredComponent( peLabel, row, 0, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( peSpinner, row, 1 );
                inputPanelLayout.addComponent( peUnits, row, 2 );
                row++;
            }
        }
        
        // Barriers...
        if ( _potentialEnergy instanceof BarrierPotential ) {
            
            row = 1;
            int column = 4;
            
            int numberOfBarriers = ((BarrierPotential) _potentialEnergy).getNumberOfBarriers();
            for ( int i = 0; i < numberOfBarriers; i++ ) {
                // Title...
                JLabel widthTitle = new JLabel( "<html>Barrier B<sub>" + ( i + 1 ) + "</sub>:</html>" );
                widthTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( widthTitle, row, column, GridBagConstraints.EAST );
                row++;
                
                // Width...
                JLabel widthLabel = new JLabel( "width:" );
                widthLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                SpinnerModel widthModel = new SpinnerNumberModel( 5.00, QTConstants.POSITION_RANGE.getLowerBound(), 
                        QTConstants.POSITION_RANGE.getUpperBound(), 0.01 );
                JSpinner widthSpinner = new JSpinner( widthModel );
                JLabel widthUnits = new JLabel( "nm" );
                inputPanelLayout.addAnchoredComponent( widthLabel, row, column, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( widthSpinner, row, column + 1 );
                inputPanelLayout.addComponent( widthUnits, row, column + 2 );
                row++;
                
                // Position...
                JLabel positionLabel = new JLabel( "position:" );
                positionLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                SpinnerModel positionModel = new SpinnerNumberModel( 2.00, QTConstants.POSITION_RANGE.getLowerBound(), 
                        QTConstants.POSITION_RANGE.getUpperBound(), 0.01 );
                JSpinner positionSpinner = new JSpinner( positionModel );
                JLabel positionUnits = new JLabel( "nm" );
                inputPanelLayout.addAnchoredComponent( positionLabel, row, column, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( positionSpinner, row, column + 1 );
                inputPanelLayout.addComponent( positionUnits, row, column + 2 );
                row++;
            }
        }

        return inputPanel;
    }
    
    /** 
     * Creates the dialog's actions panel, consisting of Apply and Close buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _applyButton = new JButton( SimStrings.get( "button.apply" ) );
        _applyButton.addActionListener( _listener );
        
        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _listener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, 10, 0 ) );
        buttonPanel.add( _applyButton );
        buttonPanel.add( _closeButton );
        
        JPanel flowPanel = new JPanel( new FlowLayout() );
        flowPanel.add( buttonPanel );

        JPanel actionPanel = new JPanel( new BorderLayout() );
        actionPanel.add( new JSeparator(), BorderLayout.NORTH );
        actionPanel.add( flowPanel, BorderLayout.SOUTH );

        return actionPanel;
    }
    
    //----------------------------------------------------------------------------
    // Event dispatcher
    //----------------------------------------------------------------------------
    
    private class EventListener implements ActionListener {
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _applyButton ) {
                handleApply();
            }
            else if ( event.getSource() == _closeButton ) {
                handleClose();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Button handlers
    //----------------------------------------------------------------------------
    
    private void handleApply() {
        //XXX change the original energy profile
    }
    
    private void handleClose() {
        //XXX check for changes that haven't been applied
        dispose();
    }
    
    //----------------------------------------------------------------------------
    // Button handlers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the dialog if the model changes.
     */
    public void update( Observable o, Object arg ) {
        System.out.println( "ConfigureEnergyDialog.update" );//XXX
        // TODO Auto-generated method stub
    }
}
